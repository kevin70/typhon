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

import java.io.InputStream;
import java.sql.Connection;
import org.skfiy.typhon.util.DbUtils;
import org.skfiy.util.ResourceUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class DatabaseSchemaTest {

    private SimpleConnectionProvider connectionProvider;

    @BeforeClass
    public void setup() {
        connectionProvider = new SimpleConnectionProvider();
        connectionProvider.init();
    }

    @AfterClass
    public void terndown() {
        connectionProvider.destroy();
    }

    /**
     * Test of executeSQLScript method, of class DatabaseSchema.
     */
    @Test
    public void executeSQLScript() throws Exception {
        Connection conn = connectionProvider.getConnection();
        conn.setAutoCommit(false);
        
        DatabaseSchema databaseSchema = new DatabaseSchema();

        InputStream in = ResourceUtils.getURL("classpath:database/test_1.sql").openStream();
        databaseSchema.executeSQLScript(conn, in);
        in.close();

        in = ResourceUtils.getURL("classpath:database/test_2.sql").openStream();
        databaseSchema.executeSQLScript(conn, in);
        in.close();

        DbUtils.rollbackAndCloseQuietly(conn);
    }
}