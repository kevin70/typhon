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
package org.skfiy.typhon.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.util.ClassUtils;
import org.skfiy.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class SimpleConnectionProvider extends AbstractComponent implements ConnectionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleConnectionProvider.class);
    
    private Class driverClass;
    private String jdbcUrl;
    private String username;
    private String password;

    @Override
    public void init() {
        // load jdbc.properties
        Properties jdbcProps = new Properties();
        InputStream in = null;
        try {

            in = ResourceUtils.getURL("classpath:jdbc.properties").openStream();
            jdbcProps.load(in);
        } catch (IOException ex) {
            /**
             * FIXME 待修复
             */
            LOG.error("", ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }

        // settings jdbc properties...
        driverClass = ClassUtils.resolveClassName(jdbcProps.getProperty("jdbc.driverClass"),
                getClass().getClassLoader());
        jdbcUrl = jdbcProps.getProperty("jdbc.url");
        username = jdbcProps.getProperty("jdbc.username");
        password = jdbcProps.getProperty("jdbc.password");
    }

    @Override
    public void destroy() {
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    @Override
    public DataSource getDataSource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
