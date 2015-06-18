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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.typhon.DbException;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.repository.ObjectNotFoundException;
import org.skfiy.typhon.repository.RoleRepository;
import org.skfiy.typhon.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class RoleRepositoryImpl implements RoleRepository {

    private static final Logger PLOG = LoggerFactory
            .getLogger(Globals.REPOSITORY_UPDATE_EXCEPION_LOG_PREFIX
                    + RoleRepositoryImpl.class.getSimpleName());

    private final String SAVE_ROLE_SQL = "insert into " + getRoleTableName()
            + "(rid,name,level,enabled,creationTime,lastLoginedTime,diamond)"
            + " values(?,?,?,?,?,?,?)";
    private final String UPDATE_ROLE_SQL = "update " + getRoleTableName()
            + " t set t.level=?,t.lastAccessedTime=?,t.diamond=? where t.rid=?";
    private final String UPDATE_ROLE_NAME_SQL = "update " + getRoleTableName()
            + " t set t.name=? where t.rid=?";
    private final String UPDATE_ROLE_DIAMOND_SQL = "update " + getRoleTableName()
            + " t set t.diamond=? where t.rid=?";
    private final String UPDATE_ROLE_LAST_LOGINED_TIME_SQL = "update " + getRoleTableName()
            + " t set t.lastLoginedTime=? where t.rid=?";
    private final String DELETE_ROLE_SQL = "delete from " + getRoleTableName() + " where rid=?";
    private final String GET_ROLE_SQL =
            "select t.name,t.level,t.enabled,t.creationTime,t.lastAccessedTime,t.lastLoginedTime,t.diamond"
                    + " from t_role t where t.rid=?";
    private final String EXISTS_NAME_SQL = "select t.rid from t_role t where t.name=?";
    //=========================================================================================//
    //=========================================================================================//
    //===========================           Role Data             =============================//
    //=========================================================================================//
    //=========================================================================================//
    private final String SAVE_ROLE_DATA_RID_SQL = "insert into " + getRoleDataTableName()
            + "(rid) values(?)";
    private final String UPDATE_ROLE_DATA_SQL =
            "update "
                    + getRoleDataTableName()
                    + " t set t.normalData=?,t.bagData=?,t.heroBagData=?,t.invisibleData=?,t.vacantData=? where t.rid=?";
    private final String DELETE_ROLE_DATA_SQL = "delete from " + getRoleDataTableName()
            + " where rid=?";
    private final String LOAD_ROLE_DATA_SQL =
            "select t.normalData,t.bagData,t.heroBagData,t.invisibleData from "
                    + getRoleDataTableName() + " t where t.rid=?";
    private final String LOAD_VACANT_DATA =
            "select t_role.rid,t_role.level,t_role.name,t_role_data.vacantData from "
                    + getRoleTableName() + "," + getRoleDataTableName()
                    + " where t_role.rid=t_role_data.rid and t_role_data.rid=?";

    @Inject
    private ConnectionProvider connectionProvider;

    /**
     *
     * @param role
     */
    @Override
    public void save(Role role) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {

            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(SAVE_ROLE_SQL);
            int i = 1;
            ps.setInt(i++, role.getRid());
            ps.setString(i++, role.getName());
            ps.setInt(i++, role.getLevel());
            ps.setBoolean(i++, role.isEnabled());
            ps.setLong(i++, System.currentTimeMillis());
            ps.setLong(i++, System.currentTimeMillis());
            ps.setInt(i++, role.getDiamond());

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("保存Role失败 rid=" + role.getRid() + ", name=" + role.getName());
            }

            // 在t_role_data表中插入一条数据
            // 之后只需要修改t_role_data表即可
            saveRoleData(role.getRid(), conn);

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
     * @param role
     */
    @Override
    public void update(Role role) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(UPDATE_ROLE_SQL);
            int i = 1;
            ps.setInt(i++, role.getLevel());
            ps.setLong(i++, System.currentTimeMillis());
            ps.setInt(i++, role.getDiamond());
            ps.setInt(i++, role.getRid());

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("更新Role失败 rid=" + role.getRid() + ", name=" + role.getName());
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            PLOG.warn("update:\nrole={}\n", JSON.toJSONString(role));
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps);
        }
    }

    @Override
    public void updateRoleName(int rid, String newName) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareCall(UPDATE_ROLE_NAME_SQL);
            int i = 1;
            ps.setString(i++, newName);
            ps.setLong(i++, rid);

            if (ps.executeUpdate() < 1) {
                throw new SQLException("更新RoleName失败 rid=" + rid + ", newName=" + newName);
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            PLOG.warn("updateRoleName:\nrid={},newName={}\n", rid, newName);
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps);
        }
    }

    @Override
    public void updateDiamond(int rid, int diamond) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(UPDATE_ROLE_DIAMOND_SQL);
            int i = 1;
            ps.setInt(i++, diamond);
            ps.setInt(i++, rid);

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("更新Role失败 rid=" + rid);
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            PLOG.warn("updateDiamond:\nrid={}, diamond={}\n", rid, diamond);
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps);
        }
    }

    @Override
    public void updateLastLoginedTime(int rid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(UPDATE_ROLE_LAST_LOGINED_TIME_SQL);
            int i = 1;
            ps.setLong(i++, System.currentTimeMillis());
            ps.setInt(i++, rid);

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("更新Role失败(lastLoginedTime) rid=" + rid);
            }

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
     * @param rid
     */
    @Override
    public void delete(int rid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(DELETE_ROLE_SQL);
            ps.setInt(1, rid);

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("删除Role失败 rid=" + rid);
            }

            // 删除关联的RoleData数据
            deleteRoleData(rid, conn);

            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            PLOG.warn("delete:\nrid={}\n", rid);
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            // close
            DbUtils.closeQuietly(conn, ps);
        }
    }

    /**
     *
     * @param rid
     * @return *
     */
    @Override
    public Role get(int rid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Role role = null;

        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement(GET_ROLE_SQL);
            ps.setInt(1, rid);

            rs = ps.executeQuery();
            if (rs.next()) {
                role = new Role();
                role.setRid(rid);
                role.setName(rs.getString("name"));
                role.setLevel(rs.getInt("level"));
                role.setEnabled(rs.getBoolean("enabled"));
                role.setCreationTime(rs.getLong("creationTime"));
                role.setLastAccessedTime(rs.getLong("lastAccessedTime"));
                role.setLastLoginedTime(rs.getLong("lastLoginedTime"));
                role.setDiamond(rs.getInt("diamond"));
            }

            return role;
        } catch (SQLException ex) {
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public int existsName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement(EXISTS_NAME_SQL);
            ps.setString(1, name);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("rid");
            }

            return -1;
        } catch (SQLException ex) {
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    //=========================================================================================//
    //=========================================================================================//
    //===========================           Role Data             =============================//
    //=========================================================================================//
    //=========================================================================================//
    @Override
    public void update(RoleData roleData) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = connectionProvider.getConnection();
            int i = 1;
            ps = conn.prepareStatement(UPDATE_ROLE_DATA_SQL);
            ps.setString(i++, roleData.getNormalData());
            ps.setString(i++, roleData.getBagData());
            ps.setString(i++, roleData.getHeroBagData());
            ps.setString(i++, roleData.getInvisibleData());
            ps.setString(i++, roleData.getVacantData());

            //*****
            ps.setInt(i++, roleData.getRid());

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("更新RoleData失败 rid=" + roleData.getRid());
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            PLOG.warn("deleteRoleData:\nroleData={}", JSON.toJSONString(roleData));
            DbUtils.rollbackQuietly(conn);
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps);
        }
    }

    /**
     * 根据Role ID查询{@code RoleData }. 如果指定的rid没有查询到对应的{@code RoleData }
     * 则将会收到一个{@link ObjectNotFoundException }.
     *
     * @param rid Role 标识
     * @return 一个合法的{@code RoleData }
     */
    @Override
    public RoleData loadRoleData(int rid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(LOAD_ROLE_DATA_SQL);
            ps.setInt(1, rid);

            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new ObjectNotFoundException("没有发现对应的RoleData数据 rid=" + rid);
            }

            RoleData roleData = new RoleData();
            roleData.setRid(rid);
            roleData.setNormalData(rs.getString("normalData"));
            roleData.setBagData(rs.getString("bagData"));
            roleData.setHeroBagData(rs.getString("heroBagData"));
            roleData.setInvisibleData(rs.getString("invisibleData"));

            return roleData;
        } catch (SQLException ex) {
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public VacantData loadVacantData(int rid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        VacantData vacantData = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement(LOAD_VACANT_DATA);
            ps.setInt(1, rid);
            rs = ps.executeQuery();
            if (rs.next()) {
                vacantData = JSON.parseObject(rs.getString("vacantData"), VacantData.class);
                vacantData.setRid(rid);
                vacantData.setName(rs.getString("name"));
                vacantData.setLevel(rs.getInt("level"));
            }
        } catch (SQLException ex) {
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }

        return vacantData;
    }

    private void saveRoleData(int rid, Connection conn) throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(SAVE_ROLE_DATA_RID_SQL);
            ps.setInt(1, rid);
            if (ps.executeUpdate() <= 0) {
                throw new SQLException("保存RoleData失败 rid=" + rid);
            }
        } finally {
            DbUtils.closeQuietly(ps);
        }
    }

    private void deleteRoleData(int rid, Connection conn) throws SQLException {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(DELETE_ROLE_DATA_SQL);
            ps.setInt(1, rid);

            if (ps.executeUpdate() <= 0) {
                PLOG.warn("deleteRoleData:\nrid={}", rid);
                throw new SQLException("删除RoleData失败 rid=" + rid);
            }
        } finally {
            DbUtils.closeQuietly(ps);
        }
    }

    @Override
    public List<Role> findRoles(String name,int number) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            List<Role> list = new ArrayList<>();
            conn = connectionProvider.getConnection();
            
            int uid = 0;
            try {
                uid = Integer.parseInt(name);
            } catch (NumberFormatException e) {
            }
            
            StringBuilder sql = new StringBuilder(
                            "SELECT rid,name,level FROM t_role WHERE name LIKE ?");
            if (uid > 0) {
                sql.append(" or rid=?");
            }
            sql.append(" LIMIT 0,?");

            ps = conn.prepareStatement(sql.toString());

            int i = 1;
            ps.setString(i++, "%" + name + "%");
            if (uid > 0) {
                ps.setInt(i++, uid);
            }
            
            ps.setInt(i++, number);
            rs = ps.executeQuery();
            
            Role role;
            while (rs.next()) {
                role = new Role();
                role.setRid(rs.getInt("rid"));
                role.setName(rs.getString("name"));
                role.setLevel(rs.getInt("level"));
                list.add(role);
            }
            
            DbUtils.commitQuietly(conn);
            return list;
        } catch (SQLException e) {
            throw new DbException("搜索好友失败", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }

    }

    private String getRoleTableName() {
        return "t_role";
    }

    private String getRoleDataTableName() {
        return "t_role_data";
    }
}
