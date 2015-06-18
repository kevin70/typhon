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
package org.skfiy.typhon.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.typhon.DbException;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.domain.CDKeyObject;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.packet.Platform;
import org.skfiy.typhon.repository.UserRepository;
import org.skfiy.typhon.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
@Singleton
public class UserRepositoryImpl implements UserRepository {

    private static final Logger LOG = LoggerFactory.getLogger(
            Globals.REPOSITORY_UPDATE_EXCEPION_LOG_PREFIX + UserRepositoryImpl.class.getSimpleName());

    private final String SAVE_USER_SQL = "insert into "
            + getTableName()
            + "(username,password,creationTime,lastAccessedTime,platform)"
            + " values(?,?,?,?,?)";
    // private static final String UPDATE_PASSWORD_SQL = "update t_user set password=?";
    private final String DELETE_USER_SQL = "delete from "
            + getTableName()
            + " where uid=?";
    private final String FIND_USER_BY_UID_SQL
            = "select t.username,t.password,t.creationTime,t.lastAccessedTime ,t.platform from "
            + getTableName()
            + " t where t.uid=?";
    private final String FIND_USER_BY_USERNAME_SQL
            = "select t.uid,t.password,t.creationTime,t.lastAccessedTime,t.platform from "
            + getTableName()
            + " t where t.username=?";
    private final String UPDATE_LAST_ACCESS_SQL = "update " + getTableName()
            + " t set t.lastAccessedTime=? where t.uid=?";
    private final String UPDATE_PASSWORD_SQL = "update t_user set password=? where uid=?";

    @Inject
    private ConnectionProvider connectionProvider;

    @Override
    public int save(String username, String password) {
        return save(username, password, null);
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public int save(final String username, final String password, final Platform platform) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(SAVE_USER_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setLong(3, System.currentTimeMillis());
            ps.setLong(4, System.currentTimeMillis());
            ps.setString(5, platform.name());

            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("User: save wrong");

            }

            DbUtils.commitQuietly(conn);
            return rs.getInt(1);
        } catch (SQLException ex) {
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    /**
     *
     * @param uid
     */
    @Override
    public void delete(int uid) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(DELETE_USER_SQL);
            ps.setInt(1, uid);

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("删除User失败 uid=" + uid);
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            LOG.warn("delete:\nuid={}\n", uid);
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps);
        }
    }

    /**
     *
     * @param uid
     * @return
     */
    @Override
    public boolean updateLastAccessedTime(int uid) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(UPDATE_LAST_ACCESS_SQL);
            ps.setLong(1, System.currentTimeMillis());
            ps.setInt(2, uid);

            boolean a = (ps.executeUpdate() < 1);

            DbUtils.commitQuietly(conn);
            return a;
        } catch (SQLException ex) {
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps);
        }
    }
    
    @Override
    public void updatePassowrd(int uid, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(UPDATE_PASSWORD_SQL);
            ps.setString(1, newPassword);
            ps.setInt(2, uid);

            boolean a = (ps.executeUpdate() < 1);

            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps);
        }
    }


    /**
     *
     * @param uid
     * @return
     */
    @Override
    public User findByUid(final int uid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement(FIND_USER_BY_UID_SQL);
            ps.setInt(1, uid);

            rs = ps.executeQuery();

            User user = null;
            if (rs.next()) {
                user = new User();
                user.setUid(uid);
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setCreationTime(rs.getLong("creationTime"));
                user.setLastAccessedTime(rs.getLong("lastAccessedTime"));
                user.setPlatform(Platform.valueOf(rs.getString("platform")));
            }
            return user;
        } catch (SQLException ex) {
            throw new DbException("User: uid is " + uid, ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    /**
     *
     * @param username
     * @return
     */
    @Override
    public User findByUsername(final String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement(FIND_USER_BY_USERNAME_SQL);
            ps.setString(1, username);

            rs = ps.executeQuery();

            User user = null;
            if (rs.next()) {
                user = new User();
                user.setUid(rs.getInt("uid"));
                user.setUsername(username);
                user.setPassword(rs.getString("password"));
                user.setCreationTime(rs.getLong("creationTime"));
                user.setLastAccessedTime(rs.getLong("lastAccessedTime"));
                user.setPlatform(Platform.valueOf(rs.getString("platform")));
            }

            return user;
        } catch (SQLException ex) {
            throw new DbException("User: username is " + username, ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    /**
     * 查询CDK.
     *
     * @param key
     * @return
     */
    @Override
    public CDKeyObject findByCDKEY(String key) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CDKeyObject cdkObject = new CDKeyObject();
        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement("select * from t_cdkey where cdkey=?");
            ps.setString(1, key);
            rs = ps.executeQuery();

            if (rs.next()) {
                cdkObject.setCdkey(key);
                cdkObject.setBatch(rs.getInt("batch"));
                cdkObject.setPlatform(Platform.valueOf(rs.getString("platform")));
                cdkObject.setItemId(rs.getString("itemId"));
                cdkObject.setBeginTime(rs.getLong("beginTime"));
                cdkObject.setEndTime(rs.getLong("endTime"));
                cdkObject.setState(rs.getInt("state"));
            }

            return cdkObject;
        } catch (SQLException ex) {
            throw new DbException("User: username is " + key, ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    /**
     * CDK
     *
     * @return
     */
//    @Override
//    public void saveCDKEY(CDKeyObject object) {
//        Connection conn = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        try {
//            conn = connectionProvider.getConnection();
//            conn.setAutoCommit(true);
//            ps =
//                    conn.prepareStatement(
//                            "insert into t_cdkey(cdkey,platform,batch,itemId,creationTime,beginTime,endTime,state) values(?,?,?,?,?,?,?,?)",
//                            Statement.RETURN_GENERATED_KEYS);
//            ps.setString(1, object.getCdkey());
//            ps.setString(2, object.getPlatform());
//            ps.setInt(3, object.getBatch());
//            ps.setString(4, object.getItemId());
//            ps.setLong(5, object.getCreationTime());
//            ps.setLong(6, object.getBeginTime());
//            ps.setLong(7, object.getEndTime());
//            ps.setInt(8, object.getState());
//            if (ps.executeUpdate()<0) {
//                throw new SQLException("CDKEY: save wrong");
//            }
//            DbUtils.commitQuietly(conn);
//        } catch (SQLException e) {
//            DbUtils.rollbackQuietly(conn);
//            throw new TyphonException("", e);
//        } finally {
//            DbUtils.closeQuietly(conn, ps, rs);
//        }
//    }
    @Override
    public boolean updateCDKey(String key) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement("update t_cdkey set state=? where cdkey=?");
            ps.setInt(1, 1);
            ps.setString(2, key);

            boolean a = (ps.executeUpdate() < 1);

            DbUtils.commitQuietly(conn);
            return a;
        } catch (SQLException ex) {
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps);
        }
    }

    @Override
    public void savePlayerLog(int uid, int changeValue, String changeType, String description) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps
                    = conn.prepareStatement(
                            "insert into t_plog(uid,changeValue,changeType,description,creationTime) values(?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, uid);
            ps.setInt(2, changeValue);
            ps.setString(3, changeType);
            ps.setString(4, description);
            ps.setLong(5, System.currentTimeMillis() / 1000);
            if (ps.executeUpdate() < 0) {
                throw new SQLException("Plog: save wrong");
            }
            DbUtils.commitQuietly(conn);
        } catch (SQLException e) {
            DbUtils.rollbackQuietly(conn);
//            LOG.warn(
//                    "savePlog:\nuid={},changeValue={},changeType={},describe={},creationTime={}SaveFailure",
//                    uid, changeValue, changeType, describe, System.currentTimeMillis());
            throw new TyphonException("", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }
    
    @Override
    public int getNextTempId() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement("INSERT INTO t_temp_id VALUES();", Statement.RETURN_GENERATED_KEYS);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("getNextTempId()");
            }

            DbUtils.commitQuietly(conn);
            return rs.getInt(1);
        } catch (SQLException ex) {
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }


    private String getTableName() {
        return "t_user";
    }
}
