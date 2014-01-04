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

import java.util.Map;
import javax.inject.Inject;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.packet.Auth;
import org.skfiy.typhon.repository.impl.RoleRepositoryImpl;
import org.skfiy.typhon.repository.impl.UserRepositoryImpl;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;

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

    @Override
    public void authentic(Auth auth) {
        prepare(doAuthentic(auth));
    }
    
    /**
     *
     * @param user
     */
    protected void prepare(User user) {
        String lock = (user.getUsername() + "@add-session").intern();
        synchronized (lock) {
            Session session = SessionContext.getSession();
            session.setId(user.getUid());
            
            // **
            Session anotherSession = sessionManager.getSession(user.getUid());
            if (anotherSession != null) {
                anotherSession.close();
                
                for (Map.Entry<String, Object> entry
                        : anotherSession.getAttributes().entrySet()) {
                    session.setAttribute(entry.getKey(), entry.getValue());
                }
            }
            
            session.setAttribute(SessionUtils.ATTR_USER, user);
            sessionManager.addSession(user.getUid(), session);
        }
    }

    /**
     * 
     * @param auth
     * @return 
     */
    protected abstract User doAuthentic(Auth auth);
}
