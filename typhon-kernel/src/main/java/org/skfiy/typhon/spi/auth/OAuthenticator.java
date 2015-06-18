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
package org.skfiy.typhon.spi.auth;

import org.skfiy.typhon.packet.OAuth2;
import org.skfiy.typhon.packet.Platform;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface OAuthenticator {

    /**
     * 认证并返回用户名.
     *
     * @param oauth 认证信息
     * @return 用户名
     */
    UserInfo authentic(OAuth2 oauth);

    /**
     * 认证平台.
     *
     * @return 认证平台
     */
    Platform getPlatform();
}
