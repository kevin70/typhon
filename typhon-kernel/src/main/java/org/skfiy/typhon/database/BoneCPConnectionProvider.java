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

import com.jolbox.bonecp.BoneCPDataSource;
import java.io.InputStream;
import java.util.Properties;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.util.ResourceUtils;

/**
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
@Singleton
public class BoneCPConnectionProvider extends BoneCPDataSource implements
        ConnectionProvider {

    @Override
    public void init() {
        try {
            Class.forName(getDriverClass());
        } catch (Exception ex) {
            throw new ComponentException("init bonecp wrong", ex);
        }
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void destroy() {
        try {
            super.close();
        } catch (Exception e) {
            // nothing
        }
    }

    @Override
    public DataSource getDataSource() {
        return this;
    }

    @Override
    public void setProperties(Properties props) throws Exception {
        super.setProperties(props);

        // load jdbc.properties
        Properties jdbcProps = new Properties();
        InputStream in = null;
        try {

            in = ResourceUtils.getURL("classpath:jdbc.properties").openStream();
            jdbcProps.load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        // settings jdbc properties...
        setDriverClass(jdbcProps.getProperty("jdbc.driverClass"));
        setJdbcUrl(jdbcProps.getProperty("jdbc.url"));
        setUsername(jdbcProps.getProperty("jdbc.username"));
        setPassword(jdbcProps.getProperty("jdbc.password"));
    }
}
