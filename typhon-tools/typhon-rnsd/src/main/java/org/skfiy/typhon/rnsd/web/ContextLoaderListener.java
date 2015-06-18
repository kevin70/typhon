/*
 * Copyright 2014 The Skfiy Open Association.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skfiy.typhon.rnsd.web;

import org.skfiy.typhon.rnsd.Version;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import org.skfiy.typhon.rnsd.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@WebListener
public class ContextLoaderListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ContextLoaderListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(
                sce.getServletContext());
        DataSource dataSource = wac.getBean("dataSource", DataSource.class);

        // 更新数据库
        Version dbVersion = findVersion(dataSource);

        if (dbVersion == null) {
            dbVersion = new Version(0, 0, 0, null);
            saveVersion(dataSource, dbVersion);
        }

        int ct = Version.currentVersion().compareTo(dbVersion);

        // update database schema
        if (ct > 0) {
            upgrade(dataSource, dbVersion);
        }

        // load global settings
        loadGlobalSettings(dataSource);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private void loadGlobalSettings(DataSource dataSource) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("select t.key,t.value from t_global_settings t");
            rs = ps.executeQuery();
            while (rs.next()) {
                System.setProperty(rs.getString("key"), rs.getString("value"));
            }
        } catch (SQLException ex) {
            LOG.error("load global settings", ex);
            throw new RuntimeException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    private void upgrade(DataSource dataSource, Version dbVersion) {
        DatabaseSchema dbSchema = new DatabaseSchema();

        for (Upgrade upg : loadUpgrades()) {
            // 当前升级脚本的版本号，大于当前应用版本号
            if (upg.getVersion().compareTo(Version.currentVersion()) > 0) {
                break;
            }

            // 当前升级脚本的版本号与小于等于数据库版本号时
            if (upg.getVersion().compareTo(dbVersion) <= 0) {
                continue;
            }

            for (File sqlFile : upg.getSqlFiles()) {
                InputStream in = null;
                Connection conn = null;
                try {
                    in = new FileInputStream(sqlFile);
                    conn = dataSource.getConnection();
                    dbSchema.executeSQLScript(conn, in);
                } catch (SQLException | IOException ex) {
                    LOG.error(sqlFile.getAbsolutePath(), ex);
                    throw new RuntimeException(ex);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            // nothing
                        }
                    }
                    DbUtils.commitAndCloseQuietly(conn);
                }
            }
        }

        // 更新数据库版本号
        updateVersion(dataSource, Version.currentVersion());
    }

    private List<Upgrade> loadUpgrades() {
        File upgradeDir = new File(getClass().getResource("/database/upgrade").getPath());
        File[] subDirs = upgradeDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        List<Upgrade> upgrades = new ArrayList<>();
        for (File subDir : subDirs) {
            upgrades.add(new Upgrade(subDir));
        }

        Collections.sort(upgrades);
        return upgrades;
    }

    private void saveVersion(DataSource dataSource, Version version) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                    "insert into version(major,minor,incremental,qualifier) values(?,?,?,?)");
            int i = 1;
            ps.setInt(i++, version.getMajor());
            ps.setInt(i++, version.getMinor());
            ps.setInt(i++, version.getIncremental());
            ps.setString(i++, version.getQualifier());

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("没有数据被更新");
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException e) {
            DbUtils.rollbackQuietly(conn);

            LOG.error("如果数据库没有版本记录，添加最低版本", e);
            throw new RuntimeException("添加最低版本号失败", e);
        } finally {
            DbUtils.closeQuietly(conn, ps);
        }
    }

    private void updateVersion(DataSource dataSource, Version version) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                    "update version set major=?,minor=?,incremental=?,qualifier=?");
            ps.setInt(1, version.getMajor());
            ps.setInt(2, version.getMinor());
            ps.setInt(3, version.getIncremental());
            ps.setString(4, version.getQualifier());

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("没有数据被更新");
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            DbUtils.rollbackQuietly(conn);
            LOG.error("更新数据库Version失败", ex);
            throw new RuntimeException("更新数据库Version失败", ex);
        } finally {
            DbUtils.closeQuietly(conn, ps);
        }
    }

    private Version findVersion(DataSource dataSource) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Version version = null;

        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement("select major,minor,incremental,qualifier from version");
            rs = ps.executeQuery();
            if (rs.next()) {
                version = new Version(rs.getInt("major"), rs.getInt("minor"),
                        rs.getInt("incremental"), rs.getString("qualifier"));
            }
        } catch (SQLException ex) {
            LOG.error("查询数据库Version失败", ex);
            throw new RuntimeException("查询数据库Version失败", ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
        return version;
    }

}
