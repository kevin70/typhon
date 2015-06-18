/*
 * Copyright 2014 The Skfiy Open Association.
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
package org.skfiy.typhon.spi.pvp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.ConnectionProvider;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.GlobalData;
import org.skfiy.typhon.domain.HeroProperty;
import org.skfiy.typhon.domain.HeroPropertyKeys;
import org.skfiy.typhon.domain.IHeroEntity;
import org.skfiy.typhon.domain.ITroop;
import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.domain.Mail;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.PvpReport;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.Troop;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.packet.GeneralPacket;
import org.skfiy.typhon.packet.MultipleValue;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.PacketNotice;
import org.skfiy.typhon.packet.PvpPacket;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.repository.GlobalDataRepository;
import org.skfiy.typhon.repository.IncidentRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ConfigurationLoader;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.NoticeBoardProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.ServerSettingKeys;
import org.skfiy.typhon.spi.Vip;
import org.skfiy.typhon.spi.pvp.PvpRobot.Hero;
import org.skfiy.typhon.spi.society.SocietyProvider;
import org.skfiy.typhon.spi.war.Direction;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.Terrain;
import org.skfiy.typhon.spi.war.WarInfo;
import org.skfiy.typhon.spi.war.WarProvider;
import org.skfiy.typhon.spi.war.WarReport;
import org.skfiy.typhon.spi.war.WarReport.Effect;
import org.skfiy.typhon.util.ComponentUtils;
import org.skfiy.typhon.util.DbUtils;
import org.skfiy.typhon.util.FastRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.math.util.MathUtils;
import org.skfiy.typhon.DbException;
import org.skfiy.typhon.util.RandomUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PvpProvider extends AbstractComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PvpProvider.class);

    private final Timer TIMER = new Timer("PVP-Timer", true);

    private final Random RANK_RANDOM = new FastRandom();

    private final Map<Integer, PvpRobot> pvpRobots = new HashMap<>();
    // new
    private final List<PvpObject> pvpObjectList = new CopyOnWriteArrayList<>();
    private final Map<Integer, Integer> pvpRankingMappings = new ConcurrentHashMap<>();

    private final List<PvpAward> pvpAwards = new ArrayList<>();
    private final List<PvpHighestRank> pvpHighestRanks = new ArrayList<>();
    private final List<Integer> pvpBuyCounts = new ArrayList<>();

    private int maxPvpHigestRanking;
    private Calendar settlementCal;

    @Inject
    private ConfigurationLoader configurationLoader;
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private WarProvider warProvider;
    @Inject
    private GlobalDataRepository globalDataReposy;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private IncidentRepository incidentReposy;
    @Inject
    private ConnectionProvider connectionProvider;
    @Inject
    private NoticeBoardProvider noticeBoardProvider;
    @Inject
    private SocietyProvider societyProvider;

    @Override
    protected void doInit() {
        // PVP奖励
        String content = ComponentUtils.readDataFile("pvp_awards.json");
        JSONArray awards = JSON.parseArray(content);
        for (int i = 0; i < awards.size(); i++) {
            JSONObject json = awards.getJSONObject(i);
            pvpAwards.add(new PvpAward(json.getIntValue("beginRanking"), itemProvider.getItem(json
                    .getString("#item.id")), json.getIntValue("count")));
        }
        Collections.sort(pvpAwards);

        // PVP最高排名奖励
        content = ComponentUtils.readDataFile("pvp_highest_ranks.json");
        JSONArray ranks = JSON.parseArray(content);
        for (int i = 0; i < ranks.size(); i++) {
            JSONObject json = ranks.getJSONObject(i);
            pvpHighestRanks.add(new PvpHighestRank(json.getIntValue("beginRanking"), json
                    .getIntValue("endRanking"), json.getIntValue("count")));
        }
        maxPvpHigestRanking = pvpHighestRanks.get(pvpHighestRanks.size() - 1).endRanking;

        // PVP刷新CD时间
        content = ComponentUtils.readDataFile("pvp_buy_count.json");
        JSONArray counts = JSON.parseArray(content);
        for (int i = 0; i < counts.size(); i++) {
            pvpBuyCounts.add(counts.getIntValue(i));
        }

        // PVP机器人
        content = ComponentUtils.readDataFile("pvp_robots.json");
        List<PvpRobot> robots = JSON.parseArray(content, PvpRobot.class);
        for (int i = 1; i <= robots.size(); i++) {
            pvpRobots.put(-i, robots.get(i - 1));
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            settlementCal = Calendar.getInstance();
            settlementCal.setTime(sdf.parse(Typhons.getProperty("typhon.spi.pvp.settlementTime")));
        } catch (ParseException e) {
            throw new ComponentException("Pvp: settlementTime["
                    + Typhons.getProperty("typhon.spi.pvp.settlementTime") + "]", e);
        }

        // 初始化机器人
        if (!configurationLoader.getServerBoolean(ServerSettingKeys.SERVER_PVP_ROBOT_INITED)) {
            for (int i = 1; i <= robots.size(); i++) {
                PvpObject po = new PvpObject();
                po.setRid(-i);
                po.setRobot(true);

                pvpObjectList.add(po);
            }
            configurationLoader.setServerProperty(ServerSettingKeys.SERVER_PVP_ROBOT_INITED, true);
        } else {
            // 加载PVP数据
            GlobalData pvpData = globalDataReposy.getGlobalData(GlobalData.Type.pvp_data);
            pvpObjectList.addAll(JSON.parseArray(pvpData.getData(), PvpObject.class));

            int ranking = 1;
            for (PvpObject po : pvpObjectList) {
                if (!po.isRobot()) {
                    pvpRankingMappings.put(po.getRid(), ranking);
                }
                ranking++;
            }

//            for (Map.Entry<String, Object> entry : JSON.parseObject(pvpData.getData()).entrySet()) {
//                pvpObjects.put(entry.getKey(),
//                        JSON.toJavaObject((JSON) entry.getValue(), PvpObject.class));
//                
//            }
        }

        // 定时器
        Calendar curCal = Calendar.getInstance();
        int hour = settlementCal.get(Calendar.HOUR_OF_DAY);
        int minute = settlementCal.get(Calendar.MINUTE);
        int second = settlementCal.get(Calendar.SECOND);

        Calendar nextCal = Calendar.getInstance();
        nextCal.set(Calendar.HOUR_OF_DAY, hour);
        nextCal.set(Calendar.MINUTE, minute);
        nextCal.set(Calendar.SECOND, second);
        nextCal.set(Calendar.MILLISECOND, 0);

        if (curCal.get(Calendar.HOUR_OF_DAY) >= hour && curCal.get(Calendar.MINUTE) >= minute
                && curCal.get(Calendar.SECOND) >= second) {
            nextCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        long delay = nextCal.getTimeInMillis() - curCal.getTimeInMillis();

        TIMER.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // 发送奖励
                long curTime = System.currentTimeMillis();

                PvpAward pa;
                Mail mail;
                int ranking = 1;

                for (PvpObject po : pvpObjectList) {
                    if (po.isRobot()) {
                        continue;
                    }

                    pa = findPvpAward(ranking);

                    mail = new Mail();
                    mail.setTitle("竞技场每日奖励");
                    mail.setContent(String.format("今日您在竞技场排行%1d名，获得", ranking));
                    mail.setAppendix(pa.item.getId());
                    mail.setCount(pa.count);
                    mail.setType(Mail.REWARD_NOTICE_TYPE);
                    mail.setCreationTime(curTime);
                    roleProvider.sendMail(po.getRid(), mail);

                    ranking++;
                }
            }

            PvpAward findPvpAward(int ranking) {
                for (PvpAward pa : pvpAwards) {
                    if (ranking >= pa.beginRanking) {
                        return pa;
                    }
                }
                return pvpAwards.get(pvpAwards.size() - 1);
            }
        }, delay, 24 * 60 * 60 * 1000);

        // 定时保存PVP数据
        TIMER.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                savePvpData();
            }
        }, Typhons.getLong("typhon.spi.pvp.saveDataFixedRateMs"),
                Typhons.getLong("typhon.spi.pvp.saveDataFixedRateMs"));
    }

    @Override
    protected void doReload() {
    }

    @Override
    protected void doDestroy() {
        savePvpData();
    }

    /**
     *
     * @param packet
     */
    public void buyCount(Packet packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Vip vip = roleProvider.getVip(normal.getVipLevel());

        if (normal.getPvpBuyCount() >= vip.privileged.max_pvp_buy_count) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not enough pvpBuyCount");
            player.getSession().write(error);
            return;
        }

        int c = normal.getPvpBuyCount();
        if (c >= pvpBuyCounts.size()) {
            c = pvpBuyCounts.size() - 1;
        }
        JSONObject object = new JSONObject();
        object.put("place", "PvPBuyCounts");
        object.put("buyCounts", c);
        SessionUtils.decrementDiamond(pvpBuyCounts.get(c), object.toString());
        normal.setPvpCount(0);
        normal.setPvpBuyCount(normal.getPvpBuyCount() + 1);

        player.getSession().write(Packet.createResult(packet));
    }

    public void refreshCd(Packet packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        if (normal.getPvpCd() > 0) {
            JSONObject object = new JSONObject();
            object.put("place", "PvpCD");
            SessionUtils.decrementDiamond(Typhons.getInteger("typhon.spi.pvp.refreshCdDiamond"), object.toString());
            normal.setPvpCd(0);
        }

        player.getSession().write(Packet.createResult(packet));
    }

    /**
     *
     * @param player
     * @param r
     * @param index
     * @return
     */
    public DargonPvpRival loadRoleId(Player player, int r, double index) {
        PvpObject po = pvpObjectList.get(r - 1);
        //FIXME
        if (po == null) {
            po = new PvpObject();
            po.setRid(-RandomUtils.nextInt(pvpRobots.size() - 1));
        }

        DargonPvpRival dargonPvpRival = new DargonPvpRival();
        dargonPvpRival.setRid(po.getRid());
        dargonPvpRival.setRanking(r);
        List<Hero> list = new ArrayList<>();
        PvpRobot pr = pvpRobots.get(po.getRid());
        if (po.isRobot()) {
            dargonPvpRival.setName(pr.getName());
            dargonPvpRival.setLevel(pr.getLevel());
            dargonPvpRival.setPowerGuess(pr.getPowerGuess());
            dargonPvpRival.setHonour(pr.getHonour());
            for (PvpRobot.Hero hero : pr.getHeros()) {
                list.add(newFightObject(hero, index));
            }
        } else {
            Session session = sessionManager.getSession(po.getRid());
            int powerGuess = 0;
            String societyName = null;
            if (session != null) {
                Player otherPlayer = SessionUtils.getPlayer(session);
                Normal normal = otherPlayer.getNormal();
                FightGroup fightGroup = normal.getFightGroup(FightGroup.PVP_FG_IDX);
                Role role = otherPlayer.getRole();
                dargonPvpRival.setName(role.getName());
                dargonPvpRival.setLevel(role.getLevel());
                dargonPvpRival.setAvatar(otherPlayer.getNormal().getAvatar());
                dargonPvpRival.setAvatarBorder(otherPlayer.getNormal().getAvatarBorder());
                societyName = normal.getSocietyName();
                Troop troop;
                HeroItem hero;
                for (int i = 0; i < fightGroup.getHeroItems().length; i++) {
                    hero = fightGroup.getHeroItem(i);
                    troop = normal.getTroop(ITroop.Type.valueOf(i));
                    list.add(newFightObject(i, troop, hero, index));
                    powerGuess += hero.getPowerGuess();
                }

            } else {
                VacantData vacantData = roleProvider.loadVacantData(po.getRid());
                String[] fgids = vacantData.getFightGroup(FightGroup.PVP_FG_IDX);
                String hid;
                HeroProperty hero;
                Troop troop;
                dargonPvpRival.setName(vacantData.getName());
                dargonPvpRival.setLevel(vacantData.getLevel());
                dargonPvpRival.setAvatar(vacantData.getAvatar());
                dargonPvpRival.setAvatarBorder(vacantData.getAvatarBorder());
                societyName = vacantData.getSocietyName();
                for (int i = 0; i < fgids.length; i++) {
                    hid = fgids[i];
                    hero = vacantData.findHeroProperty(hid);
                    troop = vacantData.getTroop(ITroop.Type.valueOf(i));
                    list.add(newFightObject(i, troop, hero, index));
                    powerGuess += hero.getPowerGuess();
                }
            }
            dargonPvpRival.setSocietyName(societyName);
            dargonPvpRival.setPowerGuess(powerGuess);
        }
        dargonPvpRival.setHeros(list);
        return dargonPvpRival;
    }

    /**
     * 查询PVP排行榜信息.
     *
     * @return 前50名玩家信息
     */
    public List<PvpRival> loadRankingList() {
        List<PvpRival> list = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            list.add(loadRival(i));
        }

        return list;
    }

    /**
     *
     * @param packet
     */
    public void searchRivals(Packet packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        // 首次进入竞技场
        if (normal.getPvpRanking() <= 0) {
            initPlayerRanking(player);
        } else {
            // 如果对应的排名不是自己则重新初始化排名
            PvpObject pvpObject = pvpObjectList.get(normal.getPvpRanking() - 1);
            if (pvpObject == null || pvpObject.getRid() != player.getRole().getRid()) {
                int oldPvpRanking = normal.getPvpRanking();

                initPlayerRanking(player);

                LOG.error("PvpRankingError -> (user:{}, oldPvpRanking:{}, newPvpRanking:{})",
                        player.getRole().getName(), oldPvpRanking, normal.getPvpRanking());
            }
        }

        int pvpRanking = normal.getPvpRanking();
        MultipleValue result = MultipleValue.createResult(packet);
        // 查询对手
        if (pvpRanking > 3) {
            result.addVal(firstRival(pvpRanking));
        }

        if (pvpRanking > 2) {
            result.addVal(secondRival(pvpRanking));
        }

        if (pvpRanking > 1) {
            result.addVal(thirdRival(pvpRanking));
        }
        player.getSession().write(result);
    }

    /**
     *
     * @param packet
     */
    public void chooseRival(PvpPacket packet) {
        synchronized (Integer.toString(packet.getRanking()).intern()) {
            Player player = SessionUtils.getPlayer();
            PvpObject po = pvpObjectList.get(packet.getRanking() - 1);

            if (po.getRid() != packet.getRivalRid()) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.pvp_ranking_changed);
                error.setText("Pvp ranking changed");
                player.getSession().write(error);
                return;
            }

            Normal normal = player.getNormal();

            // 是否有PVP挑战次数
            if (normal.getPvpCount() >= Typhons.getInteger("typhon.spi.pvp.maxCount")) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Not enough pvpCount");
                player.getSession().write(error);
                return;
            }

            if (normal.getPvpCd() != 0
                    && normal.getPvpCd() + Typhons.getLong("typhon.spi.pvp.chilldownMs") > System
                    .currentTimeMillis()) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Pvp:chilldown");
                player.getSession().write(error);
                return;
            }

            int counts = normal.getDailyTask().getTaskPvpCounts();
            if (counts >= 0) {
                normal.getDailyTask().setTaskPvpCounts(counts + 1);
            }

            SingleValue result = SingleValue.createResult(packet, reckon(packet, po, normal));
            result.setNs(packet.getNs());
            player.getSession().write(result);
        }
    }

    /**
     *
     * @param packet
     */
    public void playback(SingleValue packet) {
        SingleValue result = SingleValue.createResult(packet, loadWarReport((int) packet.getVal()));
        result.setNs(packet.getNs());
        SessionContext.getSession().write(result);
        LOG.debug(JSON.toJSONString(result));
    }

    /**
     *
     * @param rid
     * @return
     */
    public int getRanking(int rid) {
        Integer ranking = pvpRankingMappings.get(rid);
        return (ranking == null) ? 0 : ranking;
    }

    /**
     *
     */
    private void savePvpData() {
        // 保存PVP数据
        GlobalData globalData = new GlobalData();
        globalData.setType(GlobalData.Type.pvp_data);
        globalData.setData(JSON.toJSONString(pvpObjectList));
        globalDataReposy.updateGlobalData(globalData);
    }

    /**
     *
     * @param packet
     * @param po
     * @param normal
     * @return
     */
    private WarReport reckon(PvpPacket packet, PvpObject po, Normal normal) {
        // 战斗信息
        WarInfo warInfo = new WarInfo();
        warInfo.setTerrain(Terrain.None);

        // S方
        FightGroup f = normal.getFightGroup(packet.getFgidx());
        initAttacker(normal, f, warInfo);
        warInfo.getAttackerEntity().setSuccor(
                warProvider.loadSuccorFightObject(normal.player().getRole().getRid(),
                        packet.getFgidx(),
                        warInfo.getAttackerEntity().getFightObject(f.getCaptain())));

        // N方
        initDefender(po, warInfo);
        warInfo.getDefenderEntity().setSuccor(
                warProvider.loadSuccorFightObject(po.getRid(), FightGroup.PVP_FG_IDX, warInfo
                        .getDefenderEntity().getFightObject(FightGroup.PRIMARY_POS)));

        WarReport warReport = warProvider.finalAttack(warInfo);
        if (warReport.getEffect() == WarReport.Effect.W) {
            swapRanking(normal, packet.getRanking());
            giveHighRankAward(normal);

            // 公告
            if (packet.getRanking() <= Typhons.getInteger("typhon.spi.pvp.noticeMaxRanking", 50)) {
                PacketNotice notice = new PacketNotice();
                notice.setNtype(PacketNotice.PVP_RANKING_TYPE);
                notice.setName(normal.player().getRole().getName());
                notice.setAnnex1(getRoleName(po.getRid()));
                notice.setAnnex2(packet.getRanking());

                noticeBoardProvider.announce(notice);
            }
        }

        normal.setPvpCount(normal.getPvpCount() + 1);
        normal.setPvpCd(System.currentTimeMillis());

        // 保存战报数据
        int wid = saveWarReport(warReport);
        // 自己方战报
        PvpReport pvpReport = new PvpReport();
        pvpReport.setWid(wid);
        pvpReport.setName(warInfo.getDefenderEntity().getRoleName());
        pvpReport.setLevel(warInfo.getDefenderEntity().getLevel());
        pvpReport.setCreationTime(System.currentTimeMillis());
        pvpReport.setEffect(warReport.getEffect().name());
        pvpReport.setRobot(po.isRobot());

        Session beSession = null;
        if (!po.isRobot()) {
            beSession = sessionManager.getSession(po.getRid());
            if (beSession != null) {
                Normal beNormal = SessionUtils.getPlayer(beSession).getNormal();
                pvpReport.setAvatar(beNormal.getAvatar());
                pvpReport.setAvatarBorder(beNormal.getAvatarBorder());
                pvpReport.setHero(newHeroJSONObject(warInfo.getDefenderEntity().getFightObject(
                        beNormal.getFightGroup(beNormal.getLastFidx()).getCaptain())));
            } else {
                VacantData vacantData = roleProvider.loadVacantData(po.getRid());
                pvpReport.setAvatar(vacantData.getAvatar());
                pvpReport.setAvatarBorder(vacantData.getAvatarBorder());
                pvpReport.setHero(newHeroJSONObject(warInfo.getDefenderEntity().getFightObject(
                        vacantData.getCaptain())));
            }
        } else {
            pvpReport.setHero(newHeroJSONObject(pvpRobots.get(po.getRid()).getHero(
                    FightGroup.PRIMARY_POS)));
        }

        normal.addPvpReport(pvpReport);

        if (warReport.getEffect() == Effect.W) {
            normal.setPvpWinCounts(normal.getPvpWinCounts() + 1);
        }

        if (!po.isRobot()) {
            // 对方战报
            pvpReport = new PvpReport();
            pvpReport.setWid(wid);
            pvpReport.setName(warInfo.getAttackerEntity().getRoleName());
            pvpReport.setLevel(warInfo.getAttackerEntity().getLevel());
            pvpReport.setHero(newHeroJSONObject(warInfo.getAttackerEntity().getFightObject(
                    normal.getFightGroup(normal.getLastFidx()).getCaptain())));
            pvpReport.setCreationTime(System.currentTimeMillis());
            if (warReport.getEffect() == Effect.D) {
                pvpReport.setEffect(Effect.W.name());
            } else {
                pvpReport.setEffect(Effect.D.name());
            }

            pvpReport.setAvatar(normal.getAvatar());
            pvpReport.setAvatarBorder(normal.getAvatarBorder());

            if (beSession != null) {
                Normal beNormal = SessionUtils.getPlayer(beSession).getNormal();
                beNormal.addPvpReport(pvpReport);
            } else {
                // 添加一个事件
                Incident inc = new Incident();
                inc.setUid(po.getRid());
                inc.setEventName(IncidentConstants.EVENT_PVP_REPORT);
                inc.setData(JSONObject.toJSONString(pvpReport));
                incidentReposy.save(inc);
            }
        }

        return warReport;
    }

    /**
     *
     * @param normal
     * @param br
     */
    private synchronized void swapRanking(Normal normal, int beRanking) {
        int ranking = normal.getPvpRanking();

        PvpObject temp = pvpObjectList.get(beRanking - 1);
        pvpObjectList.set(beRanking - 1, pvpObjectList.get(ranking - 1));
        pvpObjectList.set(ranking - 1, temp);

        normal.setPvpRanking(beRanking);

        // 设置名次
        pvpRankingMappings.put(normal.player().getRole().getRid(), beRanking);
        if (!temp.isRobot()) {
            pvpRankingMappings.put(temp.getRid(), ranking);
        }

        if (temp.getRid() > 0) {
            // 如果对方在线则设置对方的排名
            Session beSession = sessionManager.getSession(temp.getRid());
            if (beSession != null) {
                Normal beNormal = SessionUtils.getPlayer(beSession).getNormal();
                beNormal.setPvpRanking(ranking);
            } else {
                // add event
                Incident incident = new Incident();
                incident.setUid(temp.getRid());
                incident.setEventName(IncidentConstants.EVENT_PVP_RANKING_CHANGED);
                incident.setData(Integer.toString(ranking));
                incidentReposy.save(incident);
            }
        }
    }

    private void giveHighRankAward(Normal normal) {
        int lastRanking;
        if (normal.getPvpHighRanking() <= 0) {
            lastRanking = maxPvpHigestRanking;
        } else {
            lastRanking = normal.getPvpHighRanking() - 1;
        }

        // 名次不符合要求
        if (normal.getPvpRanking() > lastRanking || normal.getPvpRanking() > maxPvpHigestRanking) {
            return;
        }

        // 获得的钻石
        int diamond = 0;
        int i = pvpHighestRanks.size() - 1;
        PvpHighestRank highestRank;
        for (;;) {
            highestRank = pvpHighestRanks.get(i);

            // 在排名区间内
            if (highestRank.beginRanking <= lastRanking && highestRank.endRanking >= lastRanking) {
                if (normal.getPvpRanking() >= highestRank.beginRanking) {
                    diamond += (lastRanking - normal.getPvpRanking() + 1) * highestRank.count;
                    lastRanking = normal.getPvpRanking();
                } else {
                    diamond += (lastRanking - highestRank.beginRanking + 1) * highestRank.count;
                    lastRanking = highestRank.beginRanking - 1;
                }
            }

            // 如果最后排名小于等于当前排名则退出循环
            if (i <= 0 || normal.getPvpRanking() > highestRank.endRanking) {
                break;
            }
            i--;
        }

        if (diamond > 100) {
            diamond = (int) MathUtils.round(diamond / 100D, 0);
        } else {
            diamond = 1;
        }

        // 发送钻石
        Mail mail = new Mail();
        mail.setTitle("最高排名奖励");
        mail.setContent(String.format("您在竞技场获得%1d名（历史最高，提升%2d名），获得", normal.getPvpRanking(),
                normal.getPvpHighRanking() - normal.getPvpRanking()));
        mail.setAppendix(Typhons.getProperty("typhon.spi.singleDiamondItemId"));
        mail.setCount(diamond);
        mail.setType(Mail.REWARD_NOTICE_TYPE);
        roleProvider.sendMail(normal.player().getRole().getRid(), mail);

        // 设置最高排名
        normal.setPvpHighRanking(normal.getPvpRanking());
    }

    /**
     * 初始化玩家PVP排名信息.
     *
     * @param player
     */
    private synchronized void initPlayerRanking(Player player) {
        Integer pvpRanking = pvpRankingMappings.get(player.getRole().getRid());
        if (pvpRanking != null) {
            player.getNormal().setPvpRanking(pvpRanking);
            LOG.error("pvp ranking error, rid: {}, ranking: {}.", player.getRole().getRid(), pvpRanking);
            return;
        }
        
        PvpObject po = new PvpObject();
        po.setRid(player.getRole().getRid());
        int ranking = pvpObjectList.size() + 1;

        player.getNormal().setPvpRanking(ranking);

        // 设置最高排名
        if (player.getNormal().getPvpHighRanking() <= 0) {
            player.getNormal().setPvpHighRanking(player.getNormal().getPvpRanking());
        }
        pvpObjectList.add(po);
        
        pvpRankingMappings.put(player.getRole().getRid(), ranking);
    }

    // ===========================获取对手信息========================================================
    private PvpRival firstRival(int ranking) {
        int min = Math.max((int) (ranking / 3D - ranking / 30D), 0);
        int max = Math.max(ranking / 2 - 1, 1);
        int x = max - min;
        if (x > 0) {
            x = RANK_RANDOM.nextInt(x + 1);
        }

        int r = min + x;
        return loadRival(r);
    }

    private PvpRival secondRival(int ranking) {
        int min = ranking / 2;
        int max = Math.max((int) (ranking / 50D * 49 - 1), 1);
        int x = max - min;
        if (x > 0) {
            x = RANK_RANDOM.nextInt(x + 1);
        }

        int r = min + x;
        return loadRival(r);
    }

    private PvpRival thirdRival(int ranking) {
        int min = (int) (ranking / 50D * 49);
        int max = ranking - 1;
        int x = max - min;
        if (x > 0) {
            x = RANK_RANDOM.nextInt(x + 1);
        }

        int r = min + x;
        return loadRival(r);
    }

    /**
     *
     * @param packet
     */
    public void showHero(GeneralPacket packet) {
        Player player = SessionUtils.getPlayer();
        int rid = 0;
        if (packet.getRid() == null) {
            //判断是不是排行榜查询玩家信息
            rid = pvpObjectList.get((int) packet.getVal() - 1).getRid();
        } else {
            rid = (int) packet.getRid();
        }
        int index = (int) packet.getGeneral();
        List<Object> heros = new ArrayList<>();
        //机器人
        if (rid <= 0) {
            PvpRobot pr = pvpRobots.get(rid);
            for (int i = 0; i < pr.getHeros().size(); i++) {
                PvpRobot.Hero hero = pr.getHero(i);
                heros.add(newHeroJSONObject(hero));
            }
        } else {
            //玩家不在线
            Session session = sessionManager.getSession(rid);
            if (session == null) {
                VacantData vacantData = roleProvider.loadVacantData(rid);
                String[] fightGroup = vacantData.getFightGroup(index);
                for (String heroId : fightGroup) {
                    HeroProperty hero = vacantData.findHeroProperty(heroId);
                    heros.add(newHeroJSONObject(hero));
                }
            } else {
                //在线
                Player beplayer = SessionUtils.getPlayer(session);
                FightGroup fightGroup = beplayer.getNormal().getFightGroup(index);
                for (HeroItem hero : fightGroup.getHeroItems()) {
                    heros.add(newHeroJSONObject(hero));
                }
            }
        }
        JSONObject result = new JSONObject();
        result.put("val", heros);
        result.put("id", packet.getId());
        result.put("type", Packet.Type.rs);
        player.getSession().write(Namespaces.HERO_PROPERTIES, result);
    }

    /**
     *
     * @param r 排名
     * @return
     */
    private PvpRival loadRival(int r) {
        PvpRival rs = new PvpRival();

        PvpObject po = pvpObjectList.get(r - 1);
        rs.setRid(po.getRid());
        rs.setRanking(r);

        // 机器人
        if (po.isRobot()) {
            PvpRobot pr = pvpRobots.get(po.getRid());
            rs.setName(pr.getName());
            rs.setLevel(pr.getLevel());
            rs.setPowerGuess(pr.getPowerGuess());
            rs.setHonour(pr.getHonour());

            // 武将信息
            PvpRobot.Hero hero = pr.getHero(FightGroup.PRIMARY_POS);
            rs.setHero(newHeroJSONObject(hero));
        } else {
            Session session = sessionManager.getSession(po.getRid());
            String societyName = null;
            if (session == null) { // 不在线
                VacantData vacantData = roleProvider.loadVacantData(po.getRid());
                rs.setName(vacantData.getName());
                rs.setLevel(vacantData.getLevel());
                rs.setAvatar(vacantData.getAvatar());
                rs.setAvatarBorder(vacantData.getAvatarBorder());
                societyName = vacantData.getSocietyName();
                String[] fightGroup = vacantData.getFightGroup(FightGroup.PVP_FG_IDX);
                int powerGuess = 0;
                for (String id : fightGroup) {
                    powerGuess += vacantData.findHeroProperty(id).getPowerGuess();
                }
                rs.setPowerGuess(powerGuess);
                rs.setSocietyName(societyName);
                HeroProperty hero
                        = vacantData.findHeroProperty(fightGroup[vacantData.getCaptain()]);
                rs.setHero(newHeroJSONObject(hero));
            } else { // 在线
                Player player = SessionUtils.getPlayer(session);
                Role role = player.getRole();
                rs.setName(role.getName());
                rs.setLevel(role.getLevel());
                rs.setAvatar(player.getNormal().getAvatar());
                rs.setAvatarBorder(player.getNormal().getAvatarBorder());
                Normal normal = player.getNormal();
                int powerGuess = 0;
                societyName = normal.getSocietyName();
                FightGroup fightGroup = normal.getFightGroup(FightGroup.PVP_FG_IDX);
                for (HeroItem heroItem : fightGroup.getHeroItems()) {
                    powerGuess += heroItem.getPowerGuess();
                }
                rs.setPowerGuess(powerGuess);
                rs.setSocietyName(societyName);
                HeroItem hero = fightGroup.getHeroItem(fightGroup.getCaptain());
                rs.setHero(newHeroJSONObject(hero));
            }
        }
        return rs;
    }

    private JSONObject newHeroJSONObject(PvpRobot.Hero hero) {
        JSONObject json = new JSONObject();
        json.put(HeroPropertyKeys.ID, hero.getIid());
        json.put(HeroPropertyKeys.LEVEL, hero.getLevel());
        json.put(HeroPropertyKeys.STAR, hero.getStar());
        json.put(HeroPropertyKeys.LADDER, hero.getLadder());
        return json;
    }

    public JSONObject newHeroJSONObject(IHeroEntity hero) {
        JSONObject json = new JSONObject();
        json.put(HeroPropertyKeys.ID, hero.getId());
        json.put(HeroPropertyKeys.LEVEL, hero.getLevel());
        json.put(HeroPropertyKeys.STAR, hero.getStar());
        json.put(HeroPropertyKeys.LADDER, hero.getLadder());
        return json;
    }

    private JSONObject newHeroJSONObject(FightObject hero) {
        JSONObject json = new JSONObject();
        json.put(HeroPropertyKeys.ID, hero.getHeroId());
        json.put(HeroPropertyKeys.LEVEL, hero.getLevel());
        json.put(HeroPropertyKeys.STAR, hero.getStar());
        json.put(HeroPropertyKeys.LADDER, hero.getLadder());
        return json;
    }

    // ===========================获取对手信息========================================================
    private void initAttacker(Normal normal, FightGroup fightGroup, WarInfo warInfo) {
        List<FightObject> fightObjects = new ArrayList<>();

        Troop troop;
        HeroItem hero;
        int powerGuess = 0;
        for (int i = 0; i < fightGroup.getHeroItems().length; i++) {
            hero = fightGroup.getHeroItem(i);
            troop = normal.getTroop(ITroop.Type.valueOf(i));
            fightObjects.add(warProvider.newFightObject(i, troop, hero));

            powerGuess += hero.getPowerGuess();
        }

        WarInfo.Entity attackerEntity = new WarInfo.Entity(Direction.S, fightObjects);
        attackerEntity.setFindAttackGoal(PvpFindAttackGoal.INSTANCE);
        attackerEntity.setCaptain(fightGroup.getCaptain());

        attackerEntity.setRid(normal.player().getRole().getRid());
        attackerEntity.setRoleName(normal.player().getRole().getName());
        attackerEntity.setLevel(normal.getLevel());
        attackerEntity.setSocietyName(normal.getSocietyName());

        attackerEntity.setPowerGuess(powerGuess);
        warInfo.setAttackerEntity(attackerEntity);

        // 主将位武将信息
        //        return newHeroJSONObject(fightGroup.getHeroItem(FightGroup.PRIMARY_POS));
    }

    private void initDefender(PvpObject po, WarInfo warInfo) {
        List<FightObject> fightObjects = new ArrayList<>();
        WarInfo.Entity defenderEntity = new WarInfo.Entity(Direction.N, fightObjects);

        if (po.isRobot()) {
            PvpRobot pr = pvpRobots.get(po.getRid());
            int i = 0;
            for (PvpRobot.Hero hero : pr.getHeros()) {
                fightObjects.add(newFightObject(i++, hero));
            }

            defenderEntity.setRid(po.getRid());
            defenderEntity.setRoleName(pr.getName());
            defenderEntity.setLevel(pr.getLevel());

            defenderEntity.setPowerGuess(pr.getPowerGuess());
        } else {
            Session session = sessionManager.getSession(po.getRid());
            int powerGuess = 0;

            if (session != null) {
                Player p = SessionUtils.getPlayer(session);
                Normal normal = p.getNormal();
                FightGroup fightGroup = normal.getFightGroup(FightGroup.PVP_FG_IDX);

                Troop troop;
                HeroItem hero;
                for (int i = 0; i < fightGroup.getHeroItems().length; i++) {
                    hero = fightGroup.getHeroItem(i);
                    troop = normal.getTroop(ITroop.Type.valueOf(i));
                    fightObjects.add(warProvider.newFightObject(i, troop, hero));

                    powerGuess += hero.getPowerGuess();
                }

                defenderEntity.setCaptain(fightGroup.getCaptain());

                defenderEntity.setRid(p.getRole().getRid());
                defenderEntity.setRoleName(p.getRole().getName());
                defenderEntity.setLevel(normal.getLevel());
                defenderEntity.setSocietyName(normal.getSocietyName());
            } else {
                VacantData vacantData = roleProvider.loadVacantData(po.getRid());

                String[] fgids = vacantData.getFightGroup(FightGroup.PVP_FG_IDX);
                String hid;
                HeroProperty hero;
                Troop troop;
                for (int i = 0; i < fgids.length; i++) {
                    hid = fgids[i];
                    hero = vacantData.findHeroProperty(hid);
                    troop = vacantData.getTroop(ITroop.Type.valueOf(i));
                    fightObjects.add(warProvider.newFightObject(i, troop, hero));

                    powerGuess += hero.getPowerGuess();
                }

                defenderEntity.setRid(vacantData.getRid());
                defenderEntity.setRoleName(vacantData.getName());
                defenderEntity.setLevel(vacantData.getLevel());
                defenderEntity.setSocietyName(vacantData.getSocietyName());
            }

            defenderEntity.setPowerGuess(powerGuess);
        }

        // ==================== 防守队默认3格怒气
        for (FightObject fo : fightObjects) {
            fo.setFury(Typhons.getInteger("typhon.spi.pvp.defenderDefaultFury", 3));
        }
        defenderEntity.setFindAttackGoal(PvpFindAttackGoal.INSTANCE);
        warInfo.setDefenderEntity(defenderEntity);
    }

    private int saveWarReport(WarReport warReport) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = 0;

        try {
            conn = connectionProvider.getConnection();
            ps
                    = conn.prepareStatement(
                            "insert into t_pvp_report(data,creationTime) values(?,?)",
                            Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, JSON.toJSONString(warReport));
            ps.setLong(2, System.currentTimeMillis());

            if (ps.executeUpdate() > 0) {
                rs = ps.getGeneratedKeys();
                rs.next();
                id = rs.getInt(1);
            }
            DbUtils.commitQuietly(conn);

        } catch (SQLException e) {
            DbUtils.rollbackQuietly(conn);
            throw new DbException(e);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
        return id;
    }

    private WarReport loadWarReport(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        WarReport warReport = null;

        try {
            conn = connectionProvider.getConnection();
            ps = conn.prepareStatement("select t.data from t_pvp_report t where t.id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                warReport = JSON.parseObject(rs.getString("data"), WarReport.class);
            }
            DbUtils.commitQuietly(conn);
        } catch (SQLException ex) {
            DbUtils.rollbackQuietly(conn);
        } finally {
            DbUtils.closeQuietly(conn, ps, rs);
        }
        return warReport;
    }

    /**
     *
     * @return
     */
    Collection<PvpRobot> getPvpRobots() {
        return pvpRobots.values();
    }

    /**
     *
     * @param rid
     * @return
     */
    private String getRoleName(int rid) {
        if (rid <= 0) {
            return pvpRobots.get(rid).getName();
        }

        Session session = sessionManager.getSession(rid);
        if (session != null) {
            return SessionUtils.getPlayer(session).getRole().getName();
        }

        VacantData vacantData = roleProvider.loadVacantData(rid);
        return vacantData.getName();
    }

    // =============================================================================================
    private FightObject newFightObject(int idx, PvpRobot.Hero hero) {
        HeroItemDobj heroItemDobj = itemProvider.getItem(hero.getIid());
        FightObject fo
                = new FightObject(idx, hero.getLevel(), hero.getLadder(), hero.getStar(),
                        heroItemDobj);

        fo.setBaseAtk(hero.getAtk());
        fo.setMaxDef(hero.getDef());

        fo.setBaseMatk(hero.getMatk());
        fo.setMaxMdef(hero.getMdef());
        fo.setMaxHp(hero.getHp());

        fo.setMaxCritRate(hero.getCritRate());
        fo.setMaxDecritRate(hero.getDecritRate());
        fo.setMaxCritMagn(hero.getCritMagn());

        fo.setMaxParryRate(hero.getParryRate());
        fo.setMaxDeparryRate(hero.getDeparryRate());
        fo.setMaxParryValue(hero.getParryValue());

        fo.setShots(warProvider.getHeroItemDobjShots(heroItemDobj, hero.getLadder()));
        return fo;
    }

    private Hero newFightObject(PvpRobot.Hero hero, double index) {
        Hero he = new Hero();
        he.setIid(hero.getIid());
        he.setLadder(hero.getLadder());
        he.setStar(hero.getStar());
        he.setLevel(hero.getLevel());
        he.setAtk((int) (hero.getAtk() * index));
        he.setDef((int) (hero.getDef()));
        he.setMatk((int) (hero.getMatk() * index));
        he.setMdef((int) (hero.getMdef()));
        he.setHp((int) (hero.getHp() * index));

        he.setCritRate(hero.getCritRate());
        he.setDecritRate(hero.getDecritRate());
        he.setCritMagn(hero.getCritMagn());

        he.setParryRate(hero.getParryRate());
        he.setDeparryRate(hero.getDeparryRate());
        he.setParryValue(hero.getParryValue());

        return he;
    }

    private Hero newFightObject(int pos, Troop troop, IHeroEntity hero, double factor) {
        Hero he = new Hero();

        he.setIid(hero.getId());
        he.setLadder(hero.getLadder());
        he.setStar(hero.getStar());
        he.setLevel(hero.getLevel());
        he.setAtk((int) (warProvider.getAtk(troop, hero) * factor));
        he.setDef((int) warProvider.getDef(troop, hero));
        he.setMatk((int) (warProvider.getMatk(troop, hero) * factor));
        he.setMdef((int) warProvider.getMdef(troop, hero));
        he.setHp((int) (warProvider.getHp(troop, hero) * factor));

        he.setCritRate(warProvider.getCritRate(troop, hero));
        he.setDecritRate(warProvider.getDecritRate(troop, hero));
        he.setCritMagn(warProvider.getCritMagn(troop, hero));

        he.setParryRate(warProvider.getParryRate(troop, hero));
        he.setDeparryRate(warProvider.getDeparryRate(troop, hero));
        he.setParryValue(warProvider.getParryValue(troop, hero));
        return he;
    }
}
