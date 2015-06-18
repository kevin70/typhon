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
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.repository.UserRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionConstants;
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
    protected UserRepository userResposy;

    @Override
    public void authentic(Auth auth) {
        prepare(doAuthentic(auth));
    }

    /**
     *
     * @param user
     */
    protected void prepare(User user) {
        Session session = SessionContext.getSession();
        // **
        Session anotherSession = sessionManager.getSession(user.getUid());
        if (anotherSession != null) {

            for (Map.Entry<String, Object> entry
                    : anotherSession.getAttributes().entrySet()) {
                if (SessionConstants.ATTR_PLAYER.equals(entry.getKey())
                        || SessionConstants.ATTR_USER.equals(entry.getKey())
                        || SessionConstants.ATTR_PLAYER_SL_KEY.equals(entry.getKey())) {
                    session.setAttribute(entry.getKey(), entry.getValue());
                }
            }

            //----------------------------------------------------------------------------------
            PacketError error = PacketError.createError(PacketError.Condition.other_online);
            anotherSession.write(error);
            // 取消认证
            anotherSession.setAuthType(null);
            anotherSession.close();
            
            sessionManager.removeSession(user.getUid());
            try {
                SessionUtils.getPlayer(session).setSession(session);
            } catch (RuntimeException e) {
                sessionManager.removeSession(user.getUid());
                session.close();
                throw e;
            }
        }

        session.setAuthType(getAuthType());
        session.setAttribute(SessionUtils.ATTR_USER, user);
        sessionManager.addSession(user.getUid(), session);
    }

    /**
     *
     * @param auth
     * @return
     */
    protected abstract User doAuthentic(Auth auth);

    /**
     *
     * @return
     */
    protected abstract String getAuthType();
}
