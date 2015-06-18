package org.skfiy.typhon.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.repository.IncidentRepository;
import org.skfiy.typhon.util.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class IncidentRepositoryImpl implements IncidentRepository {

    private static final Logger PLOG =
            LoggerFactory.getLogger(Globals.REPOSITORY_UPDATE_EXCEPION_LOG_PREFIX
                    + IncidentRepositoryImpl.class);

    @Inject
    private ConnectionProvider connectionProvider;

    @Override
    public List<Incident> findByUid(long uid) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        Incident incident;
        List<Incident> list = new ArrayList<>();

        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement("select * from t_event where uid=?");
            ps.setLong(1, uid);
            rs = ps.executeQuery();

            while (rs.next()) {
                incident = new Incident();
                incident.setUid(rs.getInt("uid"));
                incident.setPid(rs.getInt("pid"));
                incident.setEventName(rs.getString("eventname"));
                incident.setData(rs.getString("data"));
                incident.setCreationTime(rs.getLong("creationTime"));
                list.add(incident);
            }
            return list;
        } catch (SQLException e) {
            throw new TyphonException("事务查询失败", e);

        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public boolean findByData(int uid, String eventName, String data) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps =
                    conn.prepareStatement("select pid from t_event where uid=? and eventName=? and data=?");
            ps.setInt(1, uid);
            ps.setString(2, eventName);
            ps.setString(3, data);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new TyphonException("定位查询事务失败", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public int findByData(String data) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int index = 0;
        try {
            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement("select pid from t_event where data=?");
            ps.setString(1, data);
            rs = ps.executeQuery();
            if (rs.next()) {
                index = rs.getInt("pid");
            }
        } catch (SQLException e) {
            throw new TyphonException("定位查询事务失败", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
        return index;
    }

    @Override
    public List<String> findData(int uid, String eventName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String> list = new ArrayList<>();

        try {

            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement("select data from t_event where uid=? and eventName=?");
            ps.setInt(1, uid);
            ps.setString(2, eventName);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("data"));
            }

        } catch (SQLException e) {
            throw new TyphonException("定位查询事务失败", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }

        return list;
    }

    @Override
    public void save(Incident incident) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionProvider.getConnection();
            ps =
                    conn.prepareStatement(
                            "insert into t_event(uid,eventName,data,creationTime) values(?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            ps.setInt(i++, incident.getUid());
            ps.setString(i++, incident.getEventName());
            ps.setString(i++, incident.getData());
            ps.setLong(i++, System.currentTimeMillis());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int a = rs.getInt(1);
                incident.setPid(a);
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException e) {
            DbUtils.rollbackQuietly(conn);
            PLOG.warn("save:\nincident={}\n", JSON.toJSONString(incident));
            throw new TyphonException("", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
    }

    @Override
    public void delete(long id) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement("delete from t_event where pid=?");
            ps.setLong(1, id);
            if (ps.executeUpdate() <= 0) {
                throw new SQLDataException("删除失败 pid=" + id);
            }

            DbUtils.commitQuietly(conn);
        } catch (SQLException e) {
            DbUtils.rollbackQuietly(conn);
            PLOG.warn("delete:\nid={}\n", id);
            throw new TyphonException("");
        } finally {
            DbUtils.closeQuietly(conn, ps);
        }
    }
    
    @Override
    public Map<Integer,String> findPidData(int uid, String eventName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Integer,String> maps = new HashMap<>();

        try {

            conn = connectionProvider.getConnection();
            conn.setAutoCommit(true);
            ps = conn.prepareStatement("select pid, data from t_event where uid=? and eventName=?");
            ps.setInt(1, uid);
            ps.setString(2, eventName);
            rs = ps.executeQuery();

            while (rs.next()) {
                maps.put(rs.getInt("pid"),rs.getString("data"));
            }

        } catch (SQLException e) {
            throw new TyphonException("定位查询事务失败", e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }

        return maps;
    }
}
