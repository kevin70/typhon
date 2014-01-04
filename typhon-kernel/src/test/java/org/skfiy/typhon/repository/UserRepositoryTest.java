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
package org.skfiy.typhon.repository;

import org.skfiy.typhon.repository.impl.UserRepositoryImpl;
import javax.inject.Inject;
import org.skfiy.typhon.TestBase;
import org.skfiy.typhon.TestConstants;
import org.skfiy.typhon.domain.User;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class UserRepositoryTest extends TestBase {

    @Inject
    private UserRepositoryImpl userReposy;

    @Test
    public void save() {
        int uid = initUserData();
        // clean data
        cleanUserData(uid);
    }

    @Test
    public void delete() {
        int uid = initUserData();
        userReposy.delete(uid);
    }
    
    @Test
    public void updateLastAccessedTime() {
        int uid = initUserData();
        userReposy.updateLastAccessedTime(uid);
        
        // clean data
        cleanUserData(uid);
    }
    
    @Test
    public void findByUid() {
        int uid = initUserData();
        User user = userReposy.findByUid(uid);
        Assert.assertEquals(uid, user.getUid());
        
        // clean data
        cleanUserData(uid);
    }
    
    @Test
    public void findByUsername() {
        // init data
        String username = TestConstants.USERNAME;
        int uid = userReposy.save(username, TestConstants.PASSWORD);
        
        User user = userReposy.findByUsername(username);
        Assert.assertNotNull(user);
        
        // clean data
        cleanUserData(uid);
    }
    
    //
    public int initUserData() {
        return userReposy.save(TestConstants.USERNAME, TestConstants.PASSWORD);
    }

    public void cleanUserData(int uid) {
        userReposy.delete(uid);
    }
}
