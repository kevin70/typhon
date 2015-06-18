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

package org.skfiy.typhon.repository.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.typhon.DbException;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.domain.GlobalData;
import org.skfiy.typhon.repository.GlobalDataRepository;
import org.skfiy.typhon.repository.ObjectNotFoundException;
import org.skfiy.typhon.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class GlobalDataRepositoryImpl implements GlobalDataRepository {
    private static final Logger PLOG =
            LoggerFactory.getLogger(Globals.REPOSITORY_UPDATE_EXCEPION_LOG_PREFIX
                    + IncidentRepositoryImpl.class);
    private final String GET_GLOBAL_DATA_SQL = "select t.data from " + getTableName()
            + " t where t.type=?";
    private final String UPDATE_GLOBAL_DATA = "update " + getTableName()
            + " t set t.data=? where t.type=?";

    @Inject
    private ConnectionProvider connectionProvider;

    @Override
    public GlobalData getGlobalData(GlobalData.Type gtype) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(GET_GLOBAL_DATA_SQL);
            ps.setString(1, gtype.name());

            rs = ps.executeQuery();

            if (rs.next()) {
                GlobalData globalData = new GlobalData();
                globalData.setType(gtype);
                globalData.setData(rs.getString("data"));

                // commit
                DbUtils.commitQuietly(conn);

                return globalData;
            }

            throw new ObjectNotFoundException("Not found GlobalData by type[" + gtype + "]");
        } catch (SQLException ex) {
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public void updateGlobalData(GlobalData gdata) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(UPDATE_GLOBAL_DATA);
            ps.setString(1, gdata.getData());
            ps.setString(2, gdata.getType().name());

            if (ps.executeUpdate() <= 0) {
                throw new SQLException();
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            File file =
                    new File(System.getProperty("typhon.home"), "logs/"
                            + gdata.getType().toString().split("_data")[0] + ""
                            + System.currentTimeMillis() + ".log");
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(file, true);
                fileWriter.write(gdata.getData());
                fileWriter.close();
            } catch (IOException e) {
                PLOG.warn("updateGlobalData:\ngdataName={}SaveFailure", file.getName());
            }
            PLOG.warn("updateGlobalData:\ngdataName={}SaveSuccess", file.getName());
            DbUtils.rollbackQuietly(conn);
            throw new DbException("update[" + gdata + "]", ex);
        } finally {
            DbUtils.closeQuietly(conn, ps);
        }
    }

    private String getTableName() {
        return "t_global_data";
    }

}
