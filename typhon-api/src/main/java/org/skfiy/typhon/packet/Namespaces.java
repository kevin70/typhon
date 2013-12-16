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
package org.skfiy.typhon.packet;

/**
 * Packet 命名空间定义.
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
public interface Namespaces {

    /**
     * 
     */
    String NS_ERROR = "error";
    
    /**
     * 普通用户认证命名空间.
     */
    String NS_AUTH = "auth";
    
    /**
     * OAuth2 认证命名空间.
     */
    String NS_OAUTH2 = "oauth2";
    
    /**
     * 登录返回User信息命名空间.
     */
    String NS_USER_INFO = "user-info";
    
    /**
     * 登录返回的Role信息.
     */
    String NS_ROLE = "role";
    
    /**
     * 创建角色.
     */
    String NS_ROLE_CREATE= "role-create";
    
    /**
     * 玩家信息.
     */
    String NS_PLAYER_INF = "player-info";
}
