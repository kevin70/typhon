/*
 * Copyright 2015 The Skfiy Open Association.
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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.annotation.Resource;
import org.skfiy.typhon.rnsd.domain.Recharging;
import org.skfiy.typhon.rnsd.domain.Zucks;
import org.skfiy.typhon.rnsd.repository.RechargingRepository;
import org.skfiy.typhon.rnsd.repository.RepositoryException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Repository
public class RechargingRepositoryImpl implements RechargingRepository {

    private static final String SQL_INSERT = "insert into"
            + " t_recharging(tradeId,platform,uid,region,goods,amount,creationTime,status,channel)"
            + " values(?,?,?,?,?,?,?,?,?)";
    private static final String SQL_ZUCKS_INSERT = "insert into t_zucks(zid,os,point,uid) values(?,?,?,?)";

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(final Recharging recharging) {
        int r = jdbcTemplate.update(SQL_INSERT, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                int i = 1;
                ps.setString(i++, recharging.getTradeId());
                ps.setString(i++, recharging.getPlatform());
                ps.setString(i++, recharging.getUid());
                ps.setString(i++, recharging.getRegion());
                ps.setString(i++, recharging.getGoods());
                ps.setInt(i++, recharging.getAmount());
                ps.setLong(i++, recharging.getCreationTime());
                ps.setString(i++, recharging.getStatus());
                ps.setString(i++, recharging.getChannel());
            }
        });

        if (r < 1) {
            throw new RepositoryException(RepositoryException.SIGNAL_INSERTING, "no inserted");
        }
    }

    @Override
    public void save(final Zucks zucks) {
        int r = jdbcTemplate.update(SQL_ZUCKS_INSERT, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                int i = 1;
                ps.setString(i++, zucks.getZid());
                ps.setString(i++, zucks.getOs().name());
                ps.setInt(i++, zucks.getPoint());
                ps.setString(i++, zucks.getUid());
            }
        });

        if (r < 1) {
            throw new RepositoryException(RepositoryException.SIGNAL_INSERTING, "no inserted");
        }
    }

}
