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
package org.skfiy.typhon.spi.auth;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.repository.impl.UserRepositoryImpl;
import org.skfiy.typhon.packet.Auth;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class NonAuthenticator extends AbstractAuthenticator {
    
    @Inject
    private UserRepositoryImpl userManager;

    @Override
    protected User doAuthentic(Auth auth) {
        User user = userManager.findByUsername(auth.getUsername());
        if (user == null) {
            int uid = userManager.save(auth.getUsername(), auth.getPassword(), auth.getPlatform());
            user = userManager.findByUid(uid);
        }
        
        userManager.updateLastAccessedTime(user.getUid());
        return user;
    }

    @Override
    protected String getAuthType() {
        return "NON";
    }
    
}
