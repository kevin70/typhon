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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.cache.Cache;
import javax.cache.Caching;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.dobj.LargessVigor;
import org.skfiy.typhon.dobj.VipRechargingDobj;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.Friend;
import org.skfiy.typhon.domain.HeroProperty;
import org.skfiy.typhon.domain.IHeroEntity;
import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.domain.Invisible;
import org.skfiy.typhon.domain.Mail;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.domain.item.MonthCardObject;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.PacketFriend;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.repository.IncidentRepository;
import org.skfiy.typhon.repository.RoleRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionConstants;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.activity.ActivityProvider;
import org.skfiy.typhon.spi.ranking.UpdateRankingList;
import org.skfiy.typhon.spi.role.ExpLevel;
import org.skfiy.typhon.spi.role.RoleListener;
import org.skfiy.typhon.spi.society.Member;
import org.skfiy.typhon.spi.society.Society;
import org.skfiy.typhon.spi.society.SocietyProvider;
import org.skfiy.typhon.spi.store.MarketStoreProvider;
import org.skfiy.typhon.spi.store.SocietyStoreProvider;
import org.skfiy.typhon.spi.store.WesternStoreProvider;
import org.skfiy.typhon.util.ComponentUtils;
import org.skfiy.typhon.util.FastRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class RoleProvider extends AbstractComponent {

    private static final Logger LOG = LoggerFactory.getLogger(RoleProvider.class);

    private final List<ExpLevel> expLevels = new ArrayList<>();
    private final List<Vip> vips = new ArrayList<>();
    private final List<VipRechargingDobj> vipRechargingDobjs = new ArrayList<>();
    private final List<Integer> buyVigors = new ArrayList<>();
    private final List<LargessVigor> largessVigors = new ArrayList<>();

    private final Random ROLE_NAME_RANDOM = new FastRandom();

    @Resource
    private Set<RoleListener> roleListeners;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private RoleRepository roleReposy;
    @Inject
    private IncidentRepository incidentReposy;
    @Inject
    private UpdateRankingList updateRankingLists;
    @Inject
    private MarketStoreProvider marketStorePvodier;
    @Inject
    private WesternStoreProvider westernStoreProvider;
    @Inject
    private SocietyProvider societyProvider;
    @Inject
    private ActivityProvider activityProvider;
    @Inject
    private IncidentRepository incidentRepository;
    @Inject
    private SocietyStoreProvider societyStoreProvider;
    @Inject
    private TaskManager taskManager;

    @Resource(name = "iPlayerNameValidated")
    private Set<IPlayerNameValidated> playerNameValidates;
    private Cache<Integer, VacantData> roleVacantDataCache;
    private final List<String> prefixes = new ArrayList<>();
    private final List<String> suffixes = new ArrayList<>();
    // 好友模糊查询返回数量
    private int friendsNumber;
    // 充值抽奖
    private int luckeyDrawNumber;

    @Override
    protected void doInit() {
        loadDatas();

        roleVacantDataCache
                = Caching.getCacheManager().getCache(CacheKeys.ROLE_VACANT_DATA_CACHE_KEY);

        JSONArray array = JSONArray.parseArray(ComponentUtils.readDataFile("random_name.json"));
        for (int i = 0; i < array.size(); i++) {

            if (array.getJSONObject(i).containsKey("prefix")) {
                prefixes.add(array.getJSONObject(i).getString("prefix"));
            }
            if (array.getJSONObject(i).containsKey("suffix")) {
                suffixes.add(array.getJSONObject(i).getString("suffix"));
            }
        }
        friendsNumber = Typhons.getInteger("typhon.spi.roleProvider.friendsNumber");
        luckeyDrawNumber = Typhons.getInteger("typhon.spi.activity.luckeyDraw");
    }

    @Override
    protected void doReload() {
    }

    @Override
    protected void doDestroy() {
    }

    /**
     * 获取{@code ExpLevel }实例.
     *
     * @param level 等级
     * @return
     */
    public ExpLevel getExpLevel(int level) {
        if (level < 0 || level > expLevels.size()) {
            throw new ComponentException("Not found level[" + level + "]");
        }

        return expLevels.get(level - 1);
    }

    /**
     *
     * @return
     */
    public ExpLevel getLastExpLevel() {
        return expLevels.get(expLevels.size() - 1);
    }

    /**
     *
     * @param level
     * @return
     */
    public boolean isMaxLevel(int level) {
        return (level == expLevels.size());
    }

    /**
     *
     * @param normal
     * @param exp
     */
    public void pushExp(Normal normal, int exp) {
        if (isMaxLevel(normal.getLevel()) || exp <= 0) {
            return;
        }

        ExpLevel expLevel;
        int oldExp = normal.getExp();
        int newExp = oldExp + exp;
        int newLevel = normal.getLevel();

        while (true) {
            expLevel = getExpLevel(newLevel);
            if (newExp < expLevel.getExp()) {
                break;
            }

            newLevel++;
            normal.setVigor(normal.getVigor() + getExpLevel(newLevel).getPresentVigor());
            if (isMaxLevel(newLevel)) {
                newExp = 0;
                break;
            }

            newExp -= expLevel.getExp();
        }

        if (newLevel > normal.getLevel()) {
            normal.setLevel(newLevel);
            updateRankingLists.updateLevelRanking();
            updateInformation();

            // FIXME 刷新商店
            int minLevel = Typhons.getInteger("typhon.spi.store.marketMinLevel", 30);
            if (newLevel == minLevel || (newLevel > minLevel && (newLevel - minLevel) % 20 == 0)) {
                marketStorePvodier.autoRefreshCommodity(normal.player());
            }

            if (newLevel == 50 || (newLevel > 30 && (newLevel - 50) % 20 == 0)) {
                westernStoreProvider.autoRefreshCommodity(normal.player());
            }

            if (newLevel == 30 || newLevel == 35 || newLevel == 40) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(normal.player().getRole().getCreationTime());
                clearCalendar(cal);
                cal.add(Calendar.DATE, 3);

                if (cal.getTimeInMillis() >= System.currentTimeMillis()) {
                    normal.addUpgradeGifts(new RecordObject(newLevel, 0));
                }
            }
        }
        Player player = SessionUtils.getPlayer();
        if (newLevel >= activityProvider.inviteLevelLimit()
                && !player.getInvisible().isInviteBool()) {

            int uid = player.getInvisible().getInviteUid();
            Session otherSession = sessionManager.getSession(uid);
            if (uid > 0) {
                if (otherSession != null) {
                    Player otherPlayer = SessionUtils.getPlayer(otherSession);
                    Invisible invisible = otherPlayer.getInvisible();
                    invisible.getInvite().add(player.getRole().getRid());
                } else {
                    Incident inc = new Incident();
                    inc.setUid(uid);
                    inc.setEventName(IncidentConstants.INVITE_MANAGER);
                    inc.setData(JSONObject.toJSONString(player.getRole().getRid()));
                    incidentRepository.save(inc);
                }
                player.getInvisible().setInviteBool(true);
            }
        }
        if (normal.getSocietyCommodities().size() <= 0 && newLevel == 32) {
            societyStoreProvider.refreshCommodity(player);
        }
        normal.setExp(newExp);
    }

    /**
     *
     * @param recharging
     */
    public synchronized void recharge(Recharging recharging) {
        // 冲值
        Session otherSession = sessionManager.getSession(recharging.getRid());
        if (otherSession != null) {
            recharge(SessionUtils.getPlayer(otherSession), recharging);
        } else {
            Incident incident = new Incident();
            incident.setEventName(IncidentConstants.EVENT_VIP_RECHARGING);
            incident.setUid(recharging.getRid());
            incident.setData(JSON.toJSONString(recharging));
            incidentReposy.save(incident);
        }
    }

    /**
     *
     * @param player
     * @param recharging
     */
    public void recharge(Player player, Recharging recharging) {
        Normal normal = player.getNormal();

        for (VipRechargingDobj vipRechargingDobj : vipRechargingDobjs) {
            if (vipRechargingDobj.getCash() == recharging.getCash()) {
                int val = vipRechargingDobj.getDiamond() + vipRechargingDobj.getPresent();
                if (!normal.containsVigRechargingFlag(recharging.getCash())) {
                    val += vipRechargingDobj.getFirstExtraPresent();
                    normal.addVipRecharginFlag(recharging.getCash());
                }

                if (vipRechargingDobj.getCash() == 25) {
                    rechargeMonthCard(normal);
                }

//                if (System.currentTimeMillis() >= activityProvider.getVipActivityStar()
//                        && System.currentTimeMillis() < activityProvider.getVipActivityEnd()) {
//                    normal.setVipSavingsActivity(normal.getVipSavingsActivity()
//                            + vipRechargingDobj.getCash());
//
//                    player.getInvisible().setVipActivityStarTime(
//                            activityProvider.getVipActivityStar());
//                }
                if (System.currentTimeMillis() >= activityProvider.getVipDayStar()
                        && System.currentTimeMillis() < activityProvider.getVipDayEnd()) {
                    Calendar calendar = Calendar.getInstance();
                    if (player.getInvisible().getVipDate() == calendar.get(Calendar.DAY_OF_YEAR)) {
                        normal.setVipSavingsDay(normal.getVipSavingsDay()
                                + vipRechargingDobj.getCash());
                    } else {
                        normal.setVipSavingsDay(vipRechargingDobj.getCash());
                        player.getInvisible().setVipDate(calendar.get(Calendar.DAY_OF_YEAR));
                    }
                }

                JSONObject object = new JSONObject();
                object.put("place", "Recharge");
                
                normal.setDiamond(normal.getDiamond() + val);;
                inviteReturnDiamond(player, val);
                normal.setVipSavings(normal.getVipSavings() + vipRechargingDobj.getDiamond());
                // 充值抽奖次数
                normal.setLuckeyDrawCounts(normal.getLuckeyDrawCounts()
                        + (recharging.getCash() / luckeyDrawNumber));
                // 升级VIP
                int newVipLevel = 0;
                for (int i = normal.getVipLevel(); i < vips.size(); i++) {
                    Vip vip = vips.get(i);
                    if (vip.storedMoney <= normal.getVipSavings()) {
                        newVipLevel = i;
                    } else {
                        break;
                    }
                }

                if (normal.getVipLevel() != newVipLevel) {
                    normal.setVipLevel(newVipLevel);

                    Vip vip = getVip(newVipLevel);
                    if (vip.privileged.market_store_enabled) {
                        marketStorePvodier.autoRefreshCommodity(player);
                    }
                    if (vip.privileged.western_store_enabled) {
                        westernStoreProvider.autoRefreshCommodity(player);
                    }
                }
                return;
            }
        }

        throw new ComponentException("No [" + recharging.getCash() + "] cash");
    }

    private void rechargeMonthCard(Normal normal) {
        long expiredTime = normal.getMonthCardObject().getExpiredTime();

        Calendar cal = Calendar.getInstance();
        MonthCardObject monthCard;
        if (expiredTime != 0 && expiredTime > System.currentTimeMillis()) {
            cal.setTimeInMillis(expiredTime);
            monthCard = normal.getMonthCardObject();
        } else {
            monthCard = new MonthCardObject();
        }

        cal.add(Calendar.DATE, 30);
        clearCalendar(cal);

        monthCard.setExpiredTime(cal.getTimeInMillis());

        normal.setMonthCardObject(monthCard);
    }

    public void clearCalendar(Calendar c) {
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private void inviteReturnDiamond(Player player, int val) {
        int uid = player.getInvisible().getInviteUid();
        if (uid > 0) {
            Mail mail = new Mail();
            mail.setTitle("邀请者钻石返还");
            mail.setContent("您所邀请的玩家已充值成功，返还给您钻石");
            mail.setAppendix(Typhons.getProperty("typhon.spi.singleDiamondItemId"));
            mail.setCount((int) (val * 0.2));
            mail.setType(Mail.REPARATION_NOTICE_TYPE);
            sendMail(uid, mail);
        }
    }

    /**
     *
     * @param vip
     * @return
     */
    public Vip getVip(int vip) {
        if (vip >= vips.size()) {
            throw new NotFoundVipException("Not found [" + vip + "] vip.");
        }
        return vips.get(vip);
    }

    /**
     *
     * @param uid
     * @return
     */
    public boolean existsRole(int uid) {
        if (roleVacantDataCache.containsKey(uid)) {
            return true;
        }

        return roleReposy.get(uid) != null;
    }

    /**
     *
     * @param rid
     * @return
     */
    public VacantData loadVacantData(int rid) {
        VacantData data = roleVacantDataCache.get(rid);
        if (data == null) {
            data = roleReposy.loadVacantData(rid);
            roleVacantDataCache.put(rid, data);
        }
        return data;
    }

    /**
     *
     * @param name
     */
    public void create(String name) {
        // save role
        Role role = new Role();
        role.setRid(SessionUtils.getUser().getUid());
        role.setName(name);
        role.setEnabled(true);
        roleReposy.save(role);

        LOG.debug("create role [rid={}, name={}] successful", role.getRid(), role.getName());

        create0(role);
        load0(role);
    }

    /**
     *
     */
    public void preload() {
        Session session = SessionContext.getSession();
        User user = SessionUtils.getUser();

        Role role;
        Player player = (Player) session.getAttribute(SessionUtils.ATTR_PLAYER);
        if (player != null) {
            role = player.getRole();
        } else {
            // 从数据库查询角色信息
            role = roleReposy.get(user.getUid());
            if (role == null) {
                session.write(Namespaces.ROLE, "{}");
                return;
            }
        }

        load0(role);
    }

    /**
     * 购买体力.
     *
     * @param packet 包
     */
    public void buyVigor(Packet packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        Vip vip = getVip(normal.getVipLevel());
        if (normal.getBuyVigorCount() >= vip.privileged.buy_vigor_count) {
            // 没有足够的次数继续购买体力
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not enough buy vigor count");
            player.getSession().write(error);
            return;
        }

        int dep;
        if (normal.getBuyVigorCount() >= buyVigors.size()) {
            dep = buyVigors.get(buyVigors.size() - 1);
        } else {
            dep = buyVigors.get(normal.getBuyVigorCount());
        }
        JSONObject object = new JSONObject();
        object.put("place", "BuyVigor");
        object.put("buyCounts", normal.getBuyVigorCount());
        SessionUtils.decrementDiamond(dep, object.toString());
        normal.setVigor(normal.getVigor() + Typhons.getInteger("typhon.spi.role.buyVigorVal"));
        normal.setBuyVigorCount(normal.getBuyVigorCount() + 1);

        player.getSession().write(Packet.createResult(packet));
    }

    /**
     *
     * @param rid
     * @param mail
     */
    public void sendMail(int rid, Mail mail) {
        Session session = sessionManager.getSession(rid);
        if (session != null) {
            Player player = SessionUtils.getPlayer(session);
            Normal normal = player.getNormal();
            normal.addMail(mail);
        } else {
            // 通过事件机制发送邮件
            Incident inc = new Incident();
            inc.setUid(rid);
            inc.setEventName(IncidentConstants.EVENT_MAIL_NEW);
            inc.setData(JSONObject.toJSONString(mail));
            incidentReposy.save(inc);
        }
    }

    //
    public List<Role> findFriend(String uname, int number) {
        List<Role> roles = roleReposy.findRoles(uname, number);
        return roles;
    }

    /**
     *
     * @param uid
     */
    public void addFriend(int uid) {
        Player player = SessionUtils.getPlayer();
        List<Object> addFriends = new ArrayList<>();
        Session othernSession = sessionManager.getSession(uid);
        int rid = player.getRole().getRid();
        int levelLimit = 0;
        int index = 0;
        if (othernSession != null) {
            Normal normal = SessionUtils.getPlayer(othernSession).getNormal();
            Vip vip = getVip(normal.getVipLevel());
            Collection<Incident> validateIncidents = getFriendMsgSessionMap(othernSession).values();
            levelLimit = vip.privileged.max_friend_limit;
            for (Incident vinc : validateIncidents) {
                Friend friend = JSON.parseObject(vinc.getData(), Friend.class);
                if (friend.getRid() == rid) {
                    addFriends.add(0, vinc.getPid());
                    continue;
                }
                if (validateIncidents.size() - addFriends.size() >= levelLimit + 10) {
                    addFriends.add(vinc.getPid());
                }
            }
            if (validateIncidents.size() >= levelLimit + 10) {
                index = validateIncidents.size() - (levelLimit + 10) + 1;

            } else {
                index = addFriends.size();
            }
            // 删除Session中的消息，通知客户端删除请求.
            JSONObject result = new JSONObject();
            List<Integer> pids = new ArrayList<>();
            for (int i = 0; i < index; i++) {
                getFriendMsgSessionMap(othernSession).remove((int) addFriends.get(i));
                pids.add((int) addFriends.get(i));
            }
            result.put("pid", pids);
            othernSession.write(Namespaces.FRIEND_DELETE_REQUEST, result);
        } else {

            Map<Integer, String> maps
                    = incidentReposy.findPidData(uid, IncidentConstants.EVENT_FRIEND_REQUEST);
            int vipLevel = loadVacantData(uid).getVipLevel();
            Vip vip = getVip(vipLevel);
            levelLimit = vip.privileged.max_friend_limit;

            for (Entry<Integer, String> entry : maps.entrySet()) {
                Friend friend = JSON.parseObject(entry.getValue(), Friend.class);
                if (friend.getRid() == rid) {
                    addFriends.add(0, entry.getKey());
                    continue;
                }
                if (maps.size() - addFriends.size() >= levelLimit + 10) {
                    addFriends.add(entry.getKey());
                }
            }
            if (maps.size() >= levelLimit + 10) {
                index = maps.size() - (levelLimit + 10) + 1;

            } else {
                index = addFriends.size();
            }
        }

        Incident incident = new Incident();
        incident.setUid(uid);
        incident.setEventName(IncidentConstants.EVENT_FRIEND_REQUEST);
        incident.setData(JSONObject.toJSONString(integrationFriend(player)));
        incidentReposy.save(incident);

        if (othernSession != null) {
            Map<Integer, Incident> othernFriendMsgMap = getFriendMsgSessionMap(othernSession);
            othernFriendMsgMap.put(incident.getPid(), incident);

            JSONObject result = new JSONObject();
            result.put("data", incident.getJSONData());
            result.put("pid", incident.getPid());
            othernSession.write(Namespaces.FRIEND_ADD, result);
        }
        // 删除数据库.
        for (int i = 0; i < index; i++) {
            incidentReposy.delete((int) addFriends.get(i));
        }
    }

    /**
     *
     * @param packet
     */
    public void acceptFriend(PacketFriend packet) {
        int pid = packet.getPid();

        Player player = SessionUtils.getPlayer();
        Session session = player.getSession();
        Normal normal = player.getNormal();

        Map<Integer, Incident> friendMsgMap = getFriendMsgSessionMap(session);
        Incident incident = friendMsgMap.get(pid);
        if (incident == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not found friend request[" + pid + "]");
            session.write(error);
            return;
        }
        Friend friend = JSON.parseObject(incident.getData(), Friend.class);

        if (!(checkFriendLimit(player) && checkFriendLimit(friend.getRid()))) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.size_limit);
            error.setText("friend size limit");
            session.write(error);
            return;
        }

        normal.addFriend(friend);

        // 清理数据
        friendMsgMap.remove(pid);
        incidentReposy.delete(pid);

        Session otherSession = sessionManager.getSession(friend.getRid());
        if (otherSession != null) {
            Normal otherNormal = SessionUtils.getPlayer(otherSession).getNormal();
            otherNormal.addFriend(integrationFriend(player));
        } else {
            Incident otherIncident = new Incident();
            otherIncident.setUid(friend.getRid());
            otherIncident.setEventName(IncidentConstants.EVENT_FRIEND_ACCEPTED);
            otherIncident.setData(JSONObject.toJSONString(integrationFriend(player)));
            incidentReposy.save(otherIncident);
        }
        session.write(Packet.createResult(packet));
    }

    /**
     *
     * @param pid
     */
    public void rejectFriend(int pid) {
        Player player = SessionUtils.getPlayer();
        Map<Integer, Incident> friendMsgMap = getFriendMsgSessionMap(player.getSession());
        Incident incident = friendMsgMap.get(pid);
        if (incident == null) {
            LOG.debug("Not found friend message[{}].", pid);
            return;
        }

        friendMsgMap.remove(pid);
        incidentReposy.delete(pid);
    }

    /**
     *
     * @param uid
     */
    public void deleteFriend(int uid) {
        Player player = SessionUtils.getPlayer();
        Role role = player.getRole();

        // 删除已方的好友
        Normal normal = player.getNormal();
        normal.deleteFriend(uid);

        // 删除对方的好友
        Session otherSession = sessionManager.getSession(uid);
        if (otherSession != null) {
            Normal otherNormal = SessionUtils.getPlayer(otherSession).getNormal();
            otherNormal.deleteFriend(role.getRid());
        } else {
            Incident incident = new Incident();
            incident.setUid(uid);
            incident.setEventName(IncidentConstants.EVENT_FRIEND_DELETEED);
            incident.setData(JSONObject.toJSONString(integrationFriend(player)));
            incidentReposy.save(incident);
        }
    }

    /**
     *
     * @param uid
     * @return
     */
    public String primaryHeroId(int uid) {
        Session beSession = sessionManager.getSession(uid);
        String id;
        if (beSession != null) {
            Normal normal = SessionUtils.getPlayer(beSession).getNormal();
            FightGroup fightGroup = normal.getFightGroup(normal.getLastFidx());
            HeroItem heroItem = fightGroup.getHeroItem(fightGroup.getCaptain());
            id = heroItem.getId();
        } else {
            VacantData vacantData = loadVacantData(uid);
            int lastFidx = vacantData.getLastFidx();
            id = loadVacantData(uid).getFightGroups()[lastFidx][vacantData.getCaptain()];
        }
        return id;
    }

    public int findHeroFighting(int uid) {
        Session session = sessionManager.getSession(uid);
        int powerGuessSum = 0;

        if (session != null) {
            Normal normal = SessionUtils.getPlayer(session).getNormal();
            FightGroup fightGroup = normal.getFightGroup(normal.getLastFidx());
            for (HeroItem heroItem : fightGroup.getHeroItems()) {
                powerGuessSum += heroItem.getPowerGuess();
            }
        } else {
            VacantData vacantData = loadVacantData(uid);
            HeroProperty heroProperty;
            for (String itemId : vacantData.getFightGroups()[vacantData.getLastFidx()]) {
                heroProperty = vacantData.findHeroProperty(itemId);
                powerGuessSum += heroProperty.getPowerGuess();
            }
        }
        return powerGuessSum;
    }

    public void showFightGroups(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int uid = (int) packet.getVal();

        // 玩家在线则拿即时数据
        Session beSession = sessionManager.getSession(uid);
        IHeroEntity heroEntity;
        if (beSession != null) {
            Normal beNormal = SessionUtils.getPlayer(beSession).getNormal();
            FightGroup fightGoup = beNormal.getFightGroup(beNormal.getLastFidx());
            heroEntity = fightGoup.getHeroItem(fightGoup.getCaptain());
        } else {
            VacantData vacantData = loadVacantData(uid);
            heroEntity
                    = vacantData
                    .findHeroProperty(vacantData.getFightGroup(vacantData.getLastFidx())[vacantData
                            .getCaptain()]);
        }

        JSONObject beresult = new JSONObject();
        beresult.put("id", packet.getId());
        beresult.put("extraInfo", heroEntity);
        beresult.put("type", Packet.Type.rs);
        player.getSession().write(Namespaces.SHOW_HEROLIST, beresult);
    }

    /**
     *
     * @return
     */
    public String randomName() {
        int prefix = ROLE_NAME_RANDOM.nextInt(prefixes.size());
        int suffix = ROLE_NAME_RANDOM.nextInt(suffixes.size());

        StringBuilder name = new StringBuilder();
        name.append(prefixes.get(prefix));
        name.append(suffixes.get(suffix));
        return name.toString();
    }

    /**
     *
     * @param packet
     */
    public void createName(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int sum = suffixes.size() * prefixes.size();
        int size = playerNameValidates.size();
        int i = 0;
        for (;;) {
            String name = randomName();
            for (IPlayerNameValidated pnv : playerNameValidates) {

                i++;
                if (pnv.validate(name)) {
                    SingleValue result = new SingleValue();
                    Packet.assignResult(packet, result);
                    packet.setVal(name);
                    player.getSession().write(result);
                    break;
                }
                if ((i - sum * size) == 0) {
                    PacketError error
                            = PacketError.createResult(packet, PacketError.Condition.conflict);
                    error.setText("More than random upper limit");
                    player.getSession().write(error);
                    break;
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public String randomUniqueName() {
        int sum = suffixes.size() * prefixes.size();
        int size = playerNameValidates.size();
        int i = 0;
        for (;;) {
            String name = randomName();
            for (IPlayerNameValidated pnv : playerNameValidates) {
                i++;
                if (pnv.validate(name)) {
                    return name;
                }
                if ((i - sum * size) == 0) {
                    throw new IllegalStateException("More than random upper limit");
                }
            }
        }
    }

    /**
     *
     * @param uid
     * @return
     */
    public boolean checkFriendLimit(int uid) {
        Session session = sessionManager.getSession(uid);
        if (session != null) {
            return checkFriendLimit(SessionUtils.getPlayer(session));
        } else {
            VacantData data = loadVacantData(uid);
            return (data.getFriendSize() < getVip(data.getVipLevel()).privileged.max_friend_limit);
        }
    }

    /**
     *
     * @param player
     * @return
     */
    public boolean checkFriendLimit(Player player) {
        Vip vip = getVip(player.getNormal().getVipLevel());
        int limit = vip.privileged.max_friend_limit;
        return (player.getNormal().getFriends().size() < limit);
    }

    public Friend integrationFriend(Player player) {
        Role role = player.getRole();
        Normal normal = player.getNormal();
        Friend friend
                = new Friend(role.getRid(), role.getName(), role.getLevel(),
                        findHeroFighting(role.getRid()), primaryHeroId(role.getRid()),
                        normal.getAvatar(), normal.getAvatarBorder(), normal.getSocietyName());
        return friend;
    }

    /**
     *
     * @param session
     * @return
     */
    public Map<Integer, Incident> getFriendMsgSessionMap(Session session) {
        Map map = (LinkedHashMap) session.getAttribute("__FriendMsg");
        if (map == null) {
            map = new LinkedHashMap<>();
            session.setAttribute("__FriendMsg", map);
        }
        return map;
    }

    /**
     * @param role
     */
    protected void create0(Role role) {
        // fire RoleListener
        for (RoleListener roleListener : roleListeners) {
            roleListener.roleCreated(role);
        }
    }

    /**
     * 
     */
    public void updateInformation() {
        taskManager.execute(new TaskManager.Task() {

            @Override
            public void run() {
                Player player = SessionUtils.getPlayer();
                int rid = player.getRole().getRid();
                for (Friend friend : player.getNormal().getFriends()) {
                    Session besession = sessionManager.getSession(friend.getRid());
                    if (besession == null) {
                        List<String> datas
                                = incidentReposy.findData(friend.getRid(),
                                        IncidentConstants.EVENT_UPDATE_FRIENDS);
                        for (String data : datas) {
                            Friend fr = JSON.parseObject(data, Friend.class);
                            if (fr.getRid() == rid) {
                                int pid = incidentReposy.findByData(data);
                                incidentReposy.delete(pid);
                            }
                        }
                        Incident incident = new Incident();
                        incident.setUid(friend.getRid());
                        incident.setData(JSONObject.toJSONString(integrationFriend(player)));
                        incident.setEventName(IncidentConstants.EVENT_UPDATE_FRIENDS);
                        incidentReposy.save(incident);

                    } else {
                        Player beplayer = SessionUtils.getPlayer(besession);
                        for (Friend befriend : beplayer.getNormal().getFriends()) {
                            if (befriend.getRid() == rid) {
                                updateFriends(befriend, player);
                            }
                        }
                    }
                }
                // 公会
                int sid = player.getNormal().getSocietyId();
                if (sid != 0) {
                    Society society = societyProvider.findBySid(sid);
                    for (Member member : society.getMembers()) {
                        if (member.getRid() == rid) {
                            updateSocietyMember(member, player);
                        }
                    }
                }
            }
        });
    }

    public void updateSocietyMember(Member member, Player player) {
        Normal normal = player.getNormal();
        Role role = player.getRole();
        member.setRid(role.getRid());
        member.setName(role.getName());
        member.setAvatar(normal.getAvatar());
        member.setAvatarBorder(normal.getAvatarBorder());
        member.setLevel(normal.getLevel());
    }

    public void updateFriends(Friend befriend, Player player) {
        Normal normal = player.getNormal();
        Role role = player.getRole();
        if (befriend.getAvatar() != normal.getAvatar()) {
            befriend.setAvatar(normal.getAvatar());
        }
        if (befriend.getAvatarBorder() != normal.getAvatarBorder()) {
            befriend.setAvatarBorder(normal.getAvatarBorder());
        }
        if (befriend.getLevel() != role.getLevel()) {
            befriend.setLevel(role.getLevel());
        }
        if (!befriend.getName().equals(role.getName())) {
            befriend.setName(role.getName());
        }
        if (befriend.getPowerGuessSum() != findHeroFighting(role.getRid())) {
            befriend.setPowerGuessSum(findHeroFighting(role.getRid()));
        }
        if (befriend.getSocietyName() != normal.getSocietyName()) {
            befriend.setSocietyName(normal.getSocietyName());
        }
    }

    /**
     * 玩家基础信息
     */
    public void roleBase(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int rid = (int) packet.getVal();
        JSONObject object = new JSONObject();
        Session session = sessionManager.getSession(rid);
        if (session == null) {
            VacantData vacantData = loadVacantData(rid);
            object.put("name", vacantData.getName());
            object.put("avatar", vacantData.getAvatar());
            object.put("avatarBorder", vacantData.getAvatarBorder());
            object.put("level", vacantData.getLevel());
            object.put("societyName", vacantData.getSocietyName());
        } else {
            Player bePlayer = SessionUtils.getPlayer(session);
            Normal normal = bePlayer.getNormal();
            object.put("name", bePlayer.getRole().getName());
            object.put("avatar", normal.getAvatar());
            object.put("avatarBorder", normal.getAvatarBorder());
            object.put("level", normal.getLevel());
            object.put("societyName", normal.getSocietyName());
        }
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(object);
        player.getSession().write(result);
    }

    /**
     *
     * @param role
     */
    protected void load0(Role role) {
        // 角色已经被禁用无法进入游戏
        if (!role.isEnabled()) {
            Session session = SessionContext.getSession();
            Packet contextPacket
                    = (Packet) session.getAttribute(SessionConstants.ATTR_CONTEXT_PACKET);
            PacketError error
                    = PacketError.createResult(contextPacket, PacketError.Condition.not_enabled_role);
            error.setText("Not enabled role");
            session.write(error);
            return;
        }

        for (RoleListener roleListener : roleListeners) {
            roleListener.roleLoaded(role);
        }

        // 更新最后登录时间
        roleReposy.updateLastLoginedTime(role.getRid());
        role.setLastLoginedTime(System.currentTimeMillis());
    }

    private void loadDatas() {
        List<ExpLevel> expLevelConfig
                = JSON.parseArray(ComponentUtils.readDataFile("exp_level.json"), ExpLevel.class);
        this.expLevels.addAll(expLevelConfig);

        // load vip config
        JSONArray jsonArray = JSON.parseArray(ComponentUtils.readDataFile("vip.json"));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            vips.add(new Vip(json));
        }

        // load "vip_recharging.json" config
        vipRechargingDobjs.addAll(JSON.parseArray(
                ComponentUtils.readDataFile("vip_recharging.json"), VipRechargingDobj.class));

        // load buy vigor config
        List<Integer> buyVigorConfig
                = JSON.parseArray(ComponentUtils.readDataFile("buy_vigor.json"), Integer.class);
        this.buyVigors.addAll(buyVigorConfig);

        // load largess_vigor
        List<LargessVigor> largessVigorConfig
                = JSON.parseArray(ComponentUtils.readDataFile("largess_vigor.json"),
                        LargessVigor.class);
        this.largessVigors.addAll(largessVigorConfig);
    }

    /**
     *
     */
    public void removeSuccor(Normal normal) {
        long time = System.currentTimeMillis();
        int CD = Typhons.getInteger("typhon.spi.Warprovider.CD") * 1000 * 60;
        for (int i = normal.getSuccors().size() - 1; i >= 0; i--) {
            if (time - normal.getSuccors().get(i).getTime() >= CD) {
                normal.getSuccors().remove(i);
            }
        }
    }

    public int returnFriendsNumber() {
        return friendsNumber;
    }

}
