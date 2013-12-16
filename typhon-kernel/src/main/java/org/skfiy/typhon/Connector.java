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
 * 连接器.该接口提供连接器基础参数设置,如下方法都需在{@link #start()}方法执行之前调用才可生效.
 * <pre>
 * {@link #setHost(java.lang.String)}设置主机IP地址
 * {@link #setPort(int)}设置主机端口
 * {@link #setLogEnabled(boolean)}设置消息记录模式
 * {@link #setConnectionTimeout(long)}设置连接超时时间
 * </pre>
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Connector extends Lifecycle {

    /**
     * 服务提供者.
     *
     * @return {@code Service}实例
     */
    Service getService();

    /**
     * 设置服务提供者.
     *
     * @param service Service实例
     */
    void setService(Service service);

    /**
     * 连接器的主机IP地址.
     *
     * @return 主机IP地址
     */
    String getHost();

    /**
     * 设置连接器主机地址.
     * 如果在执行{@link #start()}该方法未被执行则默认IP为<b>{@code 0.0.0.0}</b>.
     * 
     * @param host IP地址
     */
    void setHost(String host);

    /**
     * 连接器访问端口.
     *
     * @return 端口
     */
    int getPort();

    /**
     * 设置连接器端口. 如果在执行@link #start()}该方法未被执行则默认端口为<b>{@code 7432}</b>.
     *
     * @param port 端口
     */
    void setPort(int port);

    /**
     * 连接是否开启消息记录,默认为{@code true}开启状态.
     * 当值为{@code true}时则需要记录所有收发消息,反之不记录消息.
     *
     * @return {@code true}开启, {@code false}关闭
     */
    boolean isLogEnabled();

    /**
     * 设置日志模式默认为<b>{@code true}</b>开启状态.
     * 如果需要关闭日志记录需要在{@link #start()}方法执行之前设置.
     *
     * @param on {@code true}开启, {@code false}关闭
     */
    void setLogEnabled(boolean enabled);

    /**
     * 连接超时时间.
     *
     * @return 超时时间以毫秒为单位
     */
    long getConnectionTimeout();

    /**
     * 设置连接超时时间. 
     * 如果在执行@link #start()}该方法未被执行则默认超时时间为<b>{@code 36000ms}</b>.
     *
     * @param connectionTimeout 超时时间以毫秒为单位
     */
    void setConnectionTimeout(long connectionTimeout);
}
