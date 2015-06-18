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
package org.skfiy.typhon.spi;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface ServerSettingKeys {

    /**
     * 服务器初始化时间(单位毫秒).
     */
    String SERVER_INIT_TIME = "server.initTime";
    
    /**
     * 服务器时区ID.
     */
    String SERVER_TIME_ZONE_ID = "server.timeZoneID";
    
    /**
     * 服务器是否初始化PVP机器人数据信息.
     */
    String SERVER_PVP_ROBOT_INITED = "server.pvpRobotInited";
}
