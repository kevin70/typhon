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
package org.skfiy.typhon.component.auth;

import javax.inject.Inject;
import org.skfiy.typhon.spi.auth.UserNotFoundException;
import org.skfiy.typhon.spi.auth.DefaultAuthenticator;
import org.skfiy.typhon.TestComponentBase;
import org.skfiy.typhon.TestConstants;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.packet.Auth;
import org.skfiy.typhon.repository.impl.UserRepositoryImpl;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.auth.PasswordNotMatchedException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class DefaultAuthenticatorTest extends TestComponentBase {

    @Inject
    private UserRepositoryImpl userResposy;
    private DefaultAuthenticator authenticatr;

    @Override
    protected void setup() {
        authenticatr = new DefaultAuthenticator();
        CONTAINER.injectMembers(authenticatr);
    }

    @Test
    public void authenticNormal() {
        String username = TestConstants.USERNAME;
        int uid = userResposy.save(username, TestConstants.PASSWORD);
        System.out.println("-------------------------------" + uid);
        Auth auth = new Auth();
        auth.setUsername(username);
        auth.setPassword(TestConstants.PASSWORD);
//        User u = userResposy.findByUsername(username);
        authenticatr.authentic(auth);

        // clean data
        userResposy.delete(uid);
    }

    /**
     * 验证不存在的用户.
     */
    @Test
    public void authenticNoUser() {
        try {
            Auth auth = getAuth();
            authenticatr.authentic(auth);
            Assert.fail("found user: uid="
                    + SessionUtils.getUser().getUid()
                    + ", username=" + SessionUtils.getUser().getUsername());
        } catch (UserNotFoundException e) {
        }
    }

    /**
     * 错误的密码.
     */
    @Test
    public void authenticPasswordError() {
        int uid = userResposy.save(TestConstants.USERNAME, TestConstants.PASSWORD + "x");
        System.out.println("authentic2-----------------------------------------------" + uid);
        try {
            Auth auth = new Auth();
            auth.setUsername(TestConstants.USERNAME);
            auth.setPassword(TestConstants.PASSWORD);
            authenticatr.authentic(auth);
            Assert.fail("password matched: " + auth.getUsername());
        } catch (PasswordNotMatchedException ex) {
        }
        // clean data
        userResposy.delete(uid);
    }
 
   private Auth getAuth() {
        Auth auth = new Auth();
        auth.setUsername(TestConstants.USERNAME);
        auth.setPassword(TestConstants.PASSWORD);
        return auth;
    }
}
