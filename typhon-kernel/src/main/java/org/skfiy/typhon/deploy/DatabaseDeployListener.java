/*
 * Copyright 2013 The Skfiy Open Association.
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
package org.skfiy.typhon.deploy;

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
import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.Lifecycle;
import org.skfiy.typhon.LifecycleEvent;
import org.skfiy.typhon.LifecycleListener;
import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.Version;
import org.skfiy.typhon.database.DatabaseSchema;
import org.skfiy.typhon.database.SimpleConnectionProvider;
import org.skfiy.typhon.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class DatabaseDeployListener implements LifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseDeployListener.class);

    @Override
    public void execute(LifecycleEvent event) {
//        if (!Typhons.getBoolean(Constants.AUTO_UPGRADE_DATABASE)) {
//            LOG.debug("No enabled auto upgrade database.");
//            return;
//        }

        if (Lifecycle.START_EVENT.equals(event.getEvent())) {
            // 部署数据库
            SimpleConnectionProvider scp = new SimpleConnectionProvider();
            scp.init();

            Version dbVersion = findVersion(scp);
            int ct = Version.currentVersion().compareTo(dbVersion);
            
            // update database schema
            if (ct > 0) {
                upgrade(scp, dbVersion);
            }
            
            scp.destroy();
        }
    }
    
    private void upgrade(ConnectionProvider connectionProvider, Version dbVersion) {
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
                    conn = connectionProvider.getConnection();
                    dbSchema.executeSQLScript(conn, in);
                } catch (SQLException | IOException ex) {
                    LOG.error(sqlFile.getAbsolutePath(), ex);
                    throw new TyphonException(ex);
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
        updateVersion(connectionProvider, Version.currentVersion());
    }
    
    private List<Upgrade> loadUpgrades() {
        File upgradeDir = new File(Typhons.getProperty(Constants.DATABASE_SCRIPTS_DIR), "upgrade");
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
    
    private void updateVersion(ConnectionProvider connectionProvider, Version version) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(
                    "update version set major=?,minor=?,incremental=?,qualifier=?");
            ps.setInt(1, version.getMajor());
            ps.setInt(2, version.getMinor());
            ps.setInt(3, version.getIncremental());
            ps.setString(4, version.getQualifier());
            
            if (ps.executeUpdate() <= 0) {
                throw new SQLException("没有数据被更新");
            }
        } catch (SQLException ex) {
            LOG.error("更新数据库Version失败", ex);
            throw new TyphonException("更新数据库Version失败", ex);
        } finally {
            DbUtils.closeQuietly(ps);
            DbUtils.commitAndCloseQuietly(conn);
        }
    }
    
    private Version findVersion(ConnectionProvider connectionProvider) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Version version = null;
        
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement("select major,minor,incremental,qualifier from version");
            rs = ps.executeQuery();
            if (rs.next()) {
                version = new Version(rs.getInt("major"),
                        rs.getInt("minor"),
                        rs.getInt("incremental"),
                        rs.getString("qualifier"));
            }
        } catch (SQLException ex) {
            LOG.error("查询数据库Version失败", ex);
            throw new TyphonException("查询数据库Version失败", ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
        return version;
    }
}
