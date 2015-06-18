/*
 * Copyright 2013 The Skfiy Open Association.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.skfiy.typhon.session;

import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.repository.UserRepository;
import org.skfiy.typhon.spi.NoAttributeDefException;
import org.skfiy.typhon.spi.NoSessionDefException;
import org.skfiy.typhon.spi.NotEnoughResourceException;

/**
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public final class SessionUtils extends SessionConstants {

    private SessionUtils() {
        throw new AssertionError("error.");
    }
    
    private static UserRepository userRepository;
    /**
     * 
     * @return
     */
    public static User getUser() {
        return getUser(SessionContext.getSession());
    }
    

    static void setUserRepository(UserRepository userRepository) {
        SessionUtils.userRepository = userRepository;
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
     * @return
     */
    public static boolean isSessionAvailable() {
        return isSessionAvailable(SessionContext.getSession());
    }

    /**
     * 
     * @param session
     * @return
     */
    public static boolean isSessionAvailable(Session session) {
        if (session == null) {
            return false;
        }

        Player player = (Player) session.getAttribute(ATTR_PLAYER);
        return (session.isAvailable() && player != null);
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

    /**
     * 
     * @param val
     * @return
     */
    public static int decrementVigor(int val) {
        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setVigor(normal.getVigor() - val);
        return normal.getVigor();
    }

    /**
     * 
     * @param val
     */
    public static void checkCopper(int val) {
        Player player = SessionUtils.getPlayer();
        if (player.getNormal().getCopper() < val) {
            throw new NotEnoughResourceException("copper: not enough " + val);
        }
    }

    /**
     * 
     * @param val
     */
    public static void incrementCopper(int val) {
        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setCopper(normal.getCopper() + val);
    }

    /**
     * 
     * @param val
     */
    public static void decrementCopper(int val) {
        checkCopper(val);

        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setCopper(normal.getCopper() - val);
    }

    /**
     * 
     * @param val
     */
    public static void checkDiamond(int val) {
        Player player = SessionUtils.getPlayer();
        if (player.getNormal().getDiamond() < val) {
            throw new NotEnoughResourceException("diamond: not enough " + val);
        }
    }

    /**
     * 
     * @param val
     * @param str
     */
    public static void incrementDiamond(int val, String str) {
        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setDiamond(normal.getDiamond() + val);

        normal.setAccDiamond(normal.getAccDiamond() + val);
        userRepository.savePlayerLog(normal.player().getRole().getRid(), val, "diamond", str);
    }

    /**
     * 
     * @param val
     * @param str
     */
    public static void decrementDiamond(int val, String str) {
        checkDiamond(val);
        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setDiamond(normal.getDiamond() - val);
        userRepository.savePlayerLog(normal.player().getRole().getRid(), -val, "diamond", str);
    }
    
    /**
     * 
     * @param val
     */
    public static void checkExploit(int val) {
        Player player = SessionUtils.getPlayer();
        if (player.getNormal().getExploit() < val) {
            throw new NotEnoughResourceException("exploit: not enough " + val);
        }
    }

    /**
     * 
     * @param val
     */
    public static void incrementExploit(int val) {
        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setExploit(normal.getExploit() + val);
    }

    /**
     * 
     * @param val
     */
    public static void decrementExploit(int val) {
        checkExploit(val);

        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setExploit(normal.getExploit() - val);
    }
    
    /**
     * @param val
     */
    public static void checkDargonMoney(int val) {
        Player player = SessionUtils.getPlayer();
        if (player.getNormal().getDargonMoney() < val) {
            throw new NotEnoughResourceException("DargonMoney:not enough" + val);
        }
    }


    /**
     * 
     */
    public static void incrementDargonMoney(int val) {
        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setDargonMoney(normal.getDargonMoney() + val);
    }

    /**
     * @param
     */
    public static void decrementDargonMoney(int val) {
        checkDargonMoney(val);
        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setDargonMoney(normal.getDargonMoney() - val);
    }
    
    
    /**
     * @param val
     */
    public static void checkSocietyMoney(int val) {
        Player player = SessionUtils.getPlayer();
        if (player.getNormal().getSocietyMoney() < val) {
            throw new NotEnoughResourceException("SocietyMoney:not enough" + val);
        }
    }

    /**
     * 
     */
    public static void incrementSocietyMoney(int val) {
        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setSocietyMoney(normal.getSocietyMoney() + val);
    }

    /**
     * @param
     */
    public static void decrementSocietyMoney(int val) {
        checkSocietyMoney(val);
        Normal normal = SessionUtils.getPlayer().getNormal();
        normal.setSocietyMoney(normal.getSocietyMoney() - val);
    }

    private static void checkAttribute(Object obj, String msg) {
        if (obj == null) {
            throw new NoAttributeDefException(msg);
        }
    }
}
