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
package org.skfiy.typhon.action;

import javax.inject.Inject;
import org.skfiy.typhon.Response;
import org.skfiy.typhon.TestConstants;
import org.skfiy.typhon.TestProtocolBase;
import org.skfiy.typhon.packet.Auth;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.repository.impl.UserRepositoryImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class AuthenticationActionTest extends TestProtocolBase {

    @Inject
    private UserRepositoryImpl userResposy;
    
    @Test
    public void authentic() {
        int uid = userResposy.save(TestConstants.USERNAME, TestConstants.PASSWORD);
        
        Auth auth = new Auth();
        auth.setNs(Namespaces.NS_AUTH);
        auth.setId("a7B2");
        auth.setUsername(TestConstants.USERNAME);
        auth.setPassword(TestConstants.PASSWORD);
        
        // send msg
        offer(auth);
        
        // 收到响应
        Response resp = poll();
        Assert.assertEquals(Namespaces.NS_USER_INFO, resp.getNs());
        
        userResposy.delete(uid);
    }
}