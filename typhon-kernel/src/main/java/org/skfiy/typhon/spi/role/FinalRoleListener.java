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
package org.skfiy.typhon.spi.role;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.Friend;
import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.repository.IncidentRepository;
import org.skfiy.typhon.script.ScriptManager;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.Event;
import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.troop.TroopProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.skfiy.typhon.spi.pvp.PvpProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class FinalRoleListener extends AbstractRoleListener {

    @Resource
    private Set<Event<Player>> everydayLoopEvents;
    @Resource
    private Set<IPlayerEvent> playerEvents;
    @Inject
    private ScriptManager scriptManager;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private IncidentRepository incidentRepository;
    @Inject
    private TroopProvider troopProvider;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private PvpProvider pvpProvider;

    @Override
    public void roleCreated(Role role) {
    }

    @Override
    public void roleLoaded(Role role) {
        Player player = role.player();
        
        // 初始化武将配置中的Item对象
        Normal normal = player.getNormal();
        Bag heroBag = player.getHeroBag();
        for (FightGroup fg : normal.getFightGroups()) {
            HeroItem[] heroItems = new HeroItem[fg.getHeroPoses().length];
            for (int i = 0; i < heroItems.length; i++) {
                int pos = fg.getHeroPoses()[i];
                heroItems[i] = (HeroItem) heroBag.findNode(pos).getItem();
            }
            fg.setHeroItems(heroItems);
        }
        
        executeEverydayLoop(player);
        executePlayerEvent(player);
        
        // 设置自己的PVP排名
        normal.setPvpRanking(pvpProvider.getRanking(player.getRole().getRid()));
        sendPlayerInfo(role);
        
        player.getSession().setAttribute("player.send.enabled", Boolean.TRUE);
        troopProvider.calculateTroopProps(player);
        // updateFriendsInfo(player);
        roleProvider.removeSuccor(normal);
    }

    private void executeEverydayLoop(Player player) {
        Calendar lastResetCal = Calendar.getInstance();
        lastResetCal.setTimeInMillis(player.getNormal().getLastResetTime());

        Calendar curCal = Calendar.getInstance();

        if (!DateUtils.isSameDay(lastResetCal, curCal)) {
            for (Event<Player> event : everydayLoopEvents) {
                event.invoke(player);
            }
        }

        player.getNormal().setLastResetTime(System.currentTimeMillis());
    }

    private void sendPlayerInfo(Role role) {
        Session session = SessionContext.getSession();
        Player player = role.player();
        // player.setSession(session);

        // send player
        player.setNs(Namespaces.PLAYER_INFO);
        player.setType(Packet.Type.st);

        String jsonStr = JSON.toJSONString(player,
                (PropertyFilter) scriptManager.getScript("player.PlayerPropertyFilter"),
                SerializerFeature.DisableCircularReferenceDetect);
        session.write(player.getNs(), jsonStr);
    }

    private void executePlayerEvent(Player player) {
        List<Incident> events = incidentRepository.findByUid(player.getRole().getRid());
        IPlayerEvent ipe;
        for (Incident ev : events) {
            ipe = findPlayerEvent(ev.getEventName());
            ipe.invoke(new PlayerEventBean(player, ev));

            if (ipe.isDeletable()) {
                incidentRepository.delete(ev.getPid());
            }
        }
    }
    
    private void updateFriendsInfo(Player player) {
        int powerGuessSum = 0;
        Normal normal = player.getNormal();
        FightGroup fightGroup = normal.getFightGroup(normal.getLastFidx());
        for (HeroItem heroItem : fightGroup.getHeroItems()) {
            powerGuessSum += heroItem.getPowerGuess();
        }

        // 更新好友对方的数据
        List<Friend> friends = player.getNormal().getFriends();
        Session otherSession;
        Normal otherNormal;
        Friend myself;
        for (Friend friend : friends) {
            otherSession = sessionManager.getSession(friend.getRid());
            if (otherSession != null) {
                otherNormal = SessionUtils.getPlayer(otherSession).getNormal();
                myself = otherNormal.findFriend(player.getRole().getRid());
                myself.setLevel(normal.getLevel());
                myself.setPowerGuessSum(powerGuessSum);
            }
        }
    }

    private IPlayerEvent findPlayerEvent(String eventName) {
        for (IPlayerEvent ev : playerEvents) {
            if (ev.getEventName().equals(eventName)) {
                return ev;
            }
        }
        throw new TyphonException("Not found IPlayerEvent[" + eventName + "]");
    }

}
