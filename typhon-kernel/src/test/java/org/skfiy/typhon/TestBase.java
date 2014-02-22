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

import javax.inject.Inject;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.repository.RoleRepository;
import org.skfiy.typhon.repository.UserRepository;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Guice(moduleFactory = TestModuleFactory.class)
public abstract class TestBase extends TestSupport {

    protected int uid = -1;
    protected int rid = -1;
    
    @Inject
    protected UserRepository userResposy;
    @Inject
    protected RoleRepository roleResposy;
    
    @BeforeClass
    public final void beforeInvoke(ITestContext context) {
        setup();
    }

    @AfterClass
    public final void afterInvoke(ITestContext context) {
        teardown();
    }

    protected void setup() {
        //
    }

    protected void teardown() {
        //
    }

    // **********************************************************//
    /**
     * 
     */
    protected void initUser() {
        if (uid != -1) {
            throw new IllegalStateException("已经有保存成功的User信息未被清理 uid=" + uid);
        }
        uid = userResposy.save(TestConstants.USERNAME, TestConstants.PASSWORD);
    }
    
    /**
     * 
     */
    protected void cleanUser() {
        if (uid != -1) {
            userResposy.delete(uid);
            uid = -1;
        }
    }
    
    /**
     * 
     */
    protected void initRole() {
        if (rid != -1) {
            throw new IllegalStateException("已经有保存成功的Role信息未被清理 rid=" + rid);
        }
        
        initUser();
        
        Role role = new Role();
        role.setRid(uid);
        role.setName(TestConstants.ROLE_NAME);
        role.setEnabled(true);
        roleResposy.save(role);
        rid = uid;
    }
    
    protected void cleanRole() {
        if (rid != -1) {
            roleResposy.delete(rid);
            rid = -1;
        }
        cleanUser();
    }
    // **********************************************************//
}
