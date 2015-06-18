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
package org.skfiy.typhon.rnsd.domain;

/**
 * 服务器实体对象.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Region {

    private int rid;
    private String name;
    private String ip;
    private int port;
    private int jmxPort;
    private OS os;
    private State state;
    private long openningTime;
    private long creationTime;

    /**
     * 获取服务区的ID.
     *
     * @return 服务区的ID
     */
    public int getRid() {
        return rid;
    }

    /**
     * 设置服务区的ID.
     *
     * @param rid 服务区的ID
     */
    public void setRid(int rid) {
        this.rid = rid;
    }

    /**
     * 获取服务区名称.
     *
     * @return 服务区名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置服务区名称.
     *
     * @param name 服务区名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取服务器区IP.
     *
     * @return 服务区IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置服务器区IP.
     *
     * @param ip 服务区IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取服务区端口.
     *
     * @return 服务区端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置服务区端口.
     *
     * @param port 服务区端口
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 获取服务器区管理端口.
     *
     * @return 服务器区管理端口
     */
    public int getJmxPort() {
        return jmxPort;
    }

    /**
     * 设置服务区管理端口.
     *
     * @param jmxPort 服务器区管理端口
     */
    public void setJmxPort(int jmxPort) {
        this.jmxPort = jmxPort;
    }

    /**
     * 获取系统类型.
     *
     * @return 系统类型
     */
    public OS getOs() {
        return os;
    }

    /**
     * 设置系统类型.
     *
     * @param os 系统类型
     */
    public void setOs(OS os) {
        this.os = os;
    }

    /**
     * 获取服务区状态.
     *
     * @return 服务区状态
     */
    public State getState() {
        return state;
    }

    /**
     * 设置服务区状态.
     *
     * @param state 服务区状态
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * 获取服务区开启时间.
     *
     * @return 服务区开启时间
     */
    public long getOpenningTime() {
        return openningTime;
    }

    /**
     * 设置服务区开启时间
     *
     * @param openningTime 服务区开启时间
     */
    public void setOpenningTime(long openningTime) {
        this.openningTime = openningTime;
    }

    /**
     * 获取服务器区记录创建时间.
     *
     * @return 服务器区记录创建时间
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * 设置服务器区记录创建时间
     *
     * @param creationTime 服务器区记录创建时间
     */
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * 服务器区状态枚举.
     */
    public enum State {

        /**
         * 新的.
         */
        NEW,
        /**
         * 繁忙的.
         */
        BUSY,
        /**
         * 闲置的.
         */
        IDLE,
        /**
         * 不可用的.
         */
        UNAVAILABLE
    }

}
