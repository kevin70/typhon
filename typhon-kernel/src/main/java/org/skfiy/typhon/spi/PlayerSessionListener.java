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

import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionConstants;
import org.skfiy.typhon.session.SessionListener;
import org.skfiy.typhon.spi.role.RoleListener;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class PlayerSessionListener implements SessionListener {

    @Inject
    private Set<RoleListener> roleListeners;
    
    @Override
    public void sessionCreated(Session session) {

    }

    @Override
    public void sessionDestroyed(Session session) {
        Player player = (Player) session.getAttribute(SessionConstants.ATTR_PLAYER);
        if (player == null) {
            return;
        }
        
        for (RoleListener listener : roleListeners) {
            listener.roleUnloaded(player.getRole());
        }
    }
}
