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

import org.skfiy.typhon.repository.impl.RoleRepositoryImpl;
import org.skfiy.typhon.repository.impl.UserRepositoryImpl;
import javax.inject.Inject;
import org.skfiy.typhon.TestBase;
import org.skfiy.typhon.TestConstants;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.RoleData;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class RoleRepositoryTest extends TestBase {

    @Inject
    private UserRepositoryImpl userReposy;
    @Inject
    private RoleRepositoryImpl roleReposy;

    @Test
    public void save() {
        int rid = initRole();
        cleanRole(rid);
    }
    
    @Test
    public void delete() {
        int rid = initRole();
        cleanRole(rid);
    }
    
    @Test
    public void update() {
        int rid = initRole();
        
        Role role = new Role();
        role.setRid(rid);
        role.setLevel(12);
        
        roleReposy.update(role);
        // clean
        cleanRole(rid);
    }
    
    @Test
    public void get() {
        int rid = initRole();
        Role role = roleReposy.get(rid);
        Assert.assertEquals(rid, role.getRid());
        
        // clean
        cleanRole(rid);
    }
    
    @Test
    public void existsName() {
        int rid = initRole();
        boolean r = roleReposy.existsName(TestConstants.ROLE_NAME);
        Assert.assertTrue(r);
        
        // clean
        cleanRole(rid);
    }
    
    @Test
    public void updateRoleData() {
        int rid = initRole();
        RoleData roleData = new RoleData();
        roleData.setRid(rid);
        roleData.setNormalData("NormalData");
        roleData.setKnapsackData("KnapsackData");
        roleReposy.update(roleData);
        
        // clean
        cleanRole(rid);
    }
    
    @Test
    public void loadRoleData() {
        int rid = initRole();
        RoleData roleData = new RoleData();
        roleData.setRid(rid);
        roleData.setNormalData("NormalData");
        roleData.setKnapsackData("KnapsackData");
        roleReposy.update(roleData);
        
        RoleData newRoleData = roleReposy.loadRoleData(rid);
        Assert.assertEquals(roleData.getNormalData(), newRoleData.getNormalData());
        Assert.assertEquals(roleData.getKnapsackData(), newRoleData.getKnapsackData());
        // clean
        cleanRole(rid);
    }
    
    // init datas
    private int initUser() {
        return userReposy.save(TestConstants.USERNAME, TestConstants.PASSWORD);
    }

    private void cleanUser(int uid) {
        userReposy.delete(uid);
    }

    private int initRole() {
        int uid = initUser();

        Role role = new Role();
        role.setRid(uid);
        role.setName(TestConstants.ROLE_NAME);
        role.setLevel(1);
        roleReposy.save(role);
        return uid;
    }

    private void cleanRole(int rid) {
        roleReposy.delete(rid);
        cleanUser(rid);
    }
}
