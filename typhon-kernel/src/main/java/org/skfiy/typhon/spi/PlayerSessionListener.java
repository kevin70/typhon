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
package org.skfiy.typhon.spi;

import org.skfiy.typhon.session.SessionUtils;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.repository.impl.RoleRepositoryImpl;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionListener;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class PlayerSessionListener implements SessionListener {

    @Inject
    private RoleRepositoryImpl roleReposy;
    @Inject
    private RoleProvider roleProvider;
//    @Inject
//    private Set<RoleDatable> roleDatables;

    @Override
    public void sessionCreated(Session session) {
        Player player = (Player) session.getAttribute(SessionUtils.ATTR_PLAYER);
        if (player != null) {
            roleProvider.onlineRole(session);
            return;
        }
        
        
    }

    @Override
    public void sessionDestroyed(Session session) {
        Player player = (Player) session.getAttribute(SessionUtils.ATTR_PLAYER);
        if (player == null) {
            return;
        }
        roleProvider.offlineRole(session);
    }
}
