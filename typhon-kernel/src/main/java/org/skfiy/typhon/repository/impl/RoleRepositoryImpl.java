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

import org.skfiy.typhon.repository.RoleRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.DbException;
import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.repository.ObjectNotFoundException;
import org.skfiy.typhon.util.DbUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class RoleRepositoryImpl implements RoleRepository {

    private final String SAVE_ROLE_SQL = "insert into " + getRoleTableName()
            + "(rid,name,level,creationTime)"
            + " values(?,?,?,?)";
    private final String UPDATE_ROLE_SQL = "update " + getRoleTableName()
            + " t set t.level=?,t.lastAccessedTime=? where t.rid=?";
    private final String DELETE_ROLE_SQL = "delete from " + getRoleTableName() + " where rid=?";
    private final String GET_ROLE_SQL = "select t.name,t.level,t.creationTime,t.lastAccessedTime"
            + " from t_role t where t.rid=?";
    private final String EXISTS_NAME_SQL = "select t.rid from t_role t where t.name=?";
    //=========================================================================================//
    //=========================================================================================//
    //===========================           Role Data             =============================//
    //=========================================================================================//
    //=========================================================================================//
    private final String SAVE_ROLE_DATA_RID_SQL = "insert into " + getRoleDataTableName()
            + "(rid) values(?)";
    private final String UPDATE_ROLE_DATA_SQL = "update " + getRoleDataTableName()
            + " t set t.normalData=?,t.bagData=? where t.rid=?";
    private final String DELETE_ROLE_DATA_SQL = "delete from " + getRoleDataTableName()
            + " where rid=?";
    private final String LOAD_ROLE_DATA_SQL = "select t.normalData,t.bagData from "
            + getRoleDataTableName()
            + " t where t.rid=?";
    
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
            ps.setInt(1, role.getRid());
            ps.setString(2, role.getName());
            ps.setInt(3, role.getLevel());
            ps.setLong(4, System.currentTimeMillis());

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("保存Role失败 rid="
                        + role.getRid()
                        + ", name=" + role.getName());
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
            ps.setInt(1, role.getLevel());
            ps.setLong(2, System.currentTimeMillis());
            ps.setInt(3, role.getRid());
            
            if (ps.executeUpdate() <= 0) {
                throw new SQLException("更新Role失败 rid="
                        + role.getRid() + ", name="
                        + role.getName());
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
     * @return      *
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
                role.setCreationTime(rs.getLong("creationTime"));
                role.setLastAccessedTime(rs.getLong("lastAccessedTime"));
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
    public boolean existsName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement(EXISTS_NAME_SQL);
            ps.setString(1, name);
            rs = ps.executeQuery();
            
            return rs.next();
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
            ps = conn.prepareStatement(UPDATE_ROLE_DATA_SQL);
            ps.setString(1, roleData.getNormalData());
            ps.setString(2, roleData.getBagData());

            //*****
            ps.setInt(3, roleData.getRid());

            if (ps.executeUpdate() <= 0) {
                throw new SQLException("更新RoleData失败 rid=" + roleData.getRid());
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
            conn.setAutoCommit(true);
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
            
            return roleData;
        } catch (SQLException ex) {
            throw new DbException(ex);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
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
                throw new SQLException("删除RoleData失败 rid=" + rid);
            }
        } finally {
            DbUtils.closeQuietly(ps);
        }
    }
    
    private String getRoleTableName() {
        return "t_role";
    }

    private String getRoleDataTableName() {
        return "t_role_data";
    }
}
