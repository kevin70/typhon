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

import javax.inject.Singleton;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.packet.Auth;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionUtils;

/**
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
@Singleton
public class DefaultAuthenticator extends AbstractAuthenticator {

    @Override
    public void authentic(Auth auth) {
        User user = userResposy.findByUsername(auth.getUsername());
        if (user == null) {
            throw new UserNotFoundException("not found [" + auth.getUsername() + "] user");
        }

        if (!user.getPassword().equals(auth.getPassword())) {
            throw new PasswordNotMatchedException("user [" + auth.getUsername() + "]");
        }

        userResposy.updateLastAccessedTime(user.getUid());
        
        Session session = SessionContext.getSession();
        session.setAttribute(SessionUtils.ATTR_USER, user);
        session.setAttribute(SessionUtils.TMP_ATTR_AUTH_PACKET, auth);
        
        prepare(user, "DEFAULT");
    }
}
