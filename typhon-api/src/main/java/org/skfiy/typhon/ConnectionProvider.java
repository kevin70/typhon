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
package org.skfiy.typhon;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * 数据库连接提供者.
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
public interface ConnectionProvider {

    /**
     * 获取一个数据库连接.
     *
     * @return 数据连接实例
     * @throws SQLException 获取数据库连接失败
     */
    Connection getConnection() throws SQLException;

    /**
     * 获取JDBC数据源.
     *
     * @return JDBC数据源
     */
    DataSource getDataSource();
}
