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
 * Typhon 服务器接口。
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Server extends Lifecycle {

    /**
     * 服务器主机地址。
     *
     * @return 服务器主机地址
     */
    String getHost();

    /**
     * 设置服务器主机地址。
     *
     * @param host 服务器主机地址
     */
    void setHost(String host);

    /**
     * 服务器端口。
     *
     * @return 服务器端口
     */
    int getPort();

    /**
     * 设置服务器端口。
     *
     * @param port 服务器端口
     */
    void setPort(int port);

    /**
     * 服务器停止命令。
     *
     * @return 停止命令
     */
    String getShutdown();

    /**
     * 设置服务器停止命令。
     *
     * @param shutdown 停止命令
     */
    void setShutdown(String shutdown);

    /**
     * 添加一个服务内容。如果Server是活动可用的,应该执行启动服务
     * <pre>
     * service.init();
     * service.start();
     * </pre>.
     *
     * @param service 服务对象
     */
    void addService(Service service);

    /**
     * 移除一个服务内容。如果Service是可用的，应该停止服务
     * <pre>
     * service.stop();
     * service.destroy();
     * </pre>.
     *
     * @param service 服务对象
     */
    void removeService(Service service);

    /**
     * 查询服务内容。
     *
     * @return 服务器管理的所有服务对象
     */
    Service[] findServices();
}
