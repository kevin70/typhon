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

/**
 * 服务提供接口.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Service extends Lifecycle {

    /**
     * 当前服务的管理者.
     *
     * @return {@code Server }对象
     */
    Server getServer();

    /**
     * 设置当前服务的管理者.
     *
     * @param server {@code Server }对象
     */
    void setServer(Server server);

    /**
     * 当前服务的名称.
     *
     * @return 字符串值
     */
    String getName();

    /**
     * 设置当前服务的名称
     *
     * @param name 字符串值
     */
    void setName(String name);

    /**
     * 添加服务的连接器. 如果当前服务状态为可用,{@code Connector }会被直接启动.
     *
     * @param connector 连接器
     */
    void addConnector(Connector connector);

    /**
     * 移除服务的连接器. 该操作会自动停止{@code Connector }.
     *
     * @param connector 连接器
     */
    void removeConnector(Connector connector);

    /**
     * 获取所有被管理的连接器.
     *
     * @return 连接器集合
     */
    Connector[] findConnectors();
}
