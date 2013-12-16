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
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.repository.impl.RoleRepositoryImpl;
import org.skfiy.typhon.repository.impl.UserRepositoryImpl;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class AbstractAuthenticator implements Authenticator {

    @Inject
    protected SessionManager sessionManager;
    @Inject
    protected UserRepositoryImpl userResposy;
    @Inject
    protected RoleRepositoryImpl roleReposy;

    /**
     *
     * @param user
     * @param authType
     */
    protected void prepare(User user, String authType) {
        String lock = (user.getUsername() + "@add-session").intern();

        synchronized (lock) {
            Session session = SessionContext.getSession();
            session.setId(user.getUid());
            session.setAuthType(authType);

            // 已经存在会话
//            Session existingSession = sessionManager.getSession(user.getUid());
//            if (existingSession != null) {
//                for (Map.Entry<String, Object> entry
//                        : existingSession.getAttributeMap().entrySet()) {
//                    session.setAttribute(entry.getKey(), entry.getValue());
//                }
//                // 断开已经存在的Session
//                existingSession.close();
//            }
            sessionManager.addSession(user.getUid(), session);

            // load role
            Role role = roleReposy.get(user.getUid());
            if (role == null) {
                session.write(Namespaces.NS_ROLE, "{}");
            } else {
                
            }
        }
    }

}
