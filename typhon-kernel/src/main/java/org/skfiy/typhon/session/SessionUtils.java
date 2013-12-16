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
package org.skfiy.typhon.session;

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.spi.NoAttributeDefException;
import org.skfiy.typhon.spi.NoSessionDefException;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public final class SessionUtils extends SessionConstants {

    private SessionUtils() {
        throw new AssertionError("error.");
    }

    /**
     *
     * @return
     */
    public static User getUser() {
        return getUser(SessionContext.getSession());
    }

    /**
     *
     * @param session
     * @return
     */
    public static User getUser(Session session) {
        checkSession(session);

        User user = (User) session.getAttribute(ATTR_USER);
        checkAttribute(user, "no [" + ATTR_USER + "] attribute define");
        return user;
    }

    /**
     *
     * @return
     */
    public static Player getPlayer() {
        return getPlayer(SessionContext.getSession());
    }

    /**
     *
     * @param session
     * @return
     */
    public static Player getPlayer(Session session) {
        checkSession(session);

        Player player = (Player) session.getAttribute(ATTR_PLAYER);
        checkAttribute(player, "no [" + ATTR_PLAYER + "] attribute define");
        return player;
    }

    /**
     *
     * @param session
     * @throws NoSessionDefException
     */
    public static void checkSession(Session session) {
        if (session == null) {
            throw new NoSessionDefException("no session define.");
        }
    }

    private static void checkAttribute(Object obj, String msg) {
        if (obj == null) {
            throw new NoAttributeDefException(msg);
        }
    }
}
