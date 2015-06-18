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
package org.skfiy.typhon.rnsd.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.annotation.Resource;
import org.skfiy.typhon.rnsd.domain.OS;
import org.skfiy.typhon.rnsd.domain.Region;
import org.skfiy.typhon.rnsd.domain.Region.State;
import org.skfiy.typhon.rnsd.repository.RegionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Repository
public class RegionRepositoryImpl implements RegionRepository {

    private final RegionRowMapper REGION_ROW_MAPPER = new RegionRowMapper();

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(Region region) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Region> loadAll() {
        return jdbcTemplate.query("select t.rid,t.name,t.ip,t.port,t.jmxPort,t.os,t.state,t.openningTime,t.creationTime from t_region t",
                REGION_ROW_MAPPER);
    }

    private class RegionRowMapper implements RowMapper<Region> {

        @Override
        public Region mapRow(ResultSet rs, int rowNum) throws SQLException {
            Region region = new Region();
            region.setRid(rs.getInt("rid"));
            region.setName(rs.getString("name"));
            region.setIp(rs.getString("ip"));
            region.setPort(rs.getInt("port"));
            region.setJmxPort(rs.getInt("jmxPort"));

            String osStr = rs.getString("os");
            if (!StringUtils.isEmpty(osStr)) {
                region.setOs(OS.valueOf(rs.getString("os")));
            }

            String stateStr = rs.getString("state");
            if (!StringUtils.isEmpty(stateStr)) {
                region.setState(State.valueOf(stateStr));
            }
            region.setOpenningTime(rs.getLong("openningTime"));
            region.setCreationTime(rs.getLong("creationTime"));
            return region;
        }

    }

}
