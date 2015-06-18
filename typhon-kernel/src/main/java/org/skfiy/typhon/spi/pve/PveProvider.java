/*
 * Copyright 2014 The Skfiy Open Association.
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
package org.skfiy.typhon.spi.pve;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.domain.Lootable.Record;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.PveProgress;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.domain.item.SuccorObject;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.PossibleAtlasloot;
import org.skfiy.typhon.packet.PveCleanPacket;
import org.skfiy.typhon.packet.PvePacket;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.repository.IncidentRepository;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.Vip;
import org.skfiy.typhon.spi.Week;
import org.skfiy.typhon.spi.atlasloot.AtlaslootBean;
import org.skfiy.typhon.spi.atlasloot.AtlaslootProvider;
import org.skfiy.typhon.spi.hero.HeroProvider;
import org.skfiy.typhon.spi.ranking.UpdateRankingList;
import org.skfiy.typhon.spi.store.MarketStoreProvider;
import org.skfiy.typhon.spi.store.WesternStoreProvider;
import org.skfiy.typhon.spi.task.TaskPveProgressProvider;
import org.skfiy.typhon.spi.war.WarProvider;
import org.skfiy.typhon.util.ComponentUtils;
import org.skfiy.typhon.util.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class PveProvider extends AbstractComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PveProvider.class);

    public static final String WAR_INFO_KEY = "typhon.warInfo";

    // 史实篇
    private static final int PVE_SUBJECT_HISTORY = 1;
    // 列传篇
    private static final int PVE_SUBJECT_STORY = 0;
    // 活动篇
    private static final int PVE_SUBJECT_ACTIVITY = 2;
    // 精英模式
    private static final int PVE_MODE_DIFFICULT = 1;

    private final List<Chapter> historyChapters = new ArrayList<>();
    private final List<Chapter> historyDifficultChapters = new ArrayList<>();
    private final List<Chapter> storyChapters = new ArrayList<>();
    private final List<ActivityChapter> activityChapters = new ArrayList<>();
    private JSONArray refreshCost;
    private JSONArray expReturn;

    @Inject
    private ItemProvider itemProvider;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private HeroProvider heroProvider;
    @Inject
    private UpdateRankingList updateRankingList;
    @Inject
    private AtlaslootProvider atlaslootProvider;
    @Inject
    private WarProvider warProvider;
    @Inject
    private MarketStoreProvider marketStoreProvider;
    @Inject
    private WesternStoreProvider westernStoreProvider;
    @Inject
    private TaskPveProgressProvider taskPveProgressProvider;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private IncidentRepository incidentReposy;

    @Override
    protected void doInit() {
        historyChapters.addAll(loadChapterList("chapter_history.json"));
        historyDifficultChapters.addAll(loadChapterList("chapter_difficult_history.json"));
        storyChapters.addAll(loadChapterList("chapter_story.json"));
        activityChapters.addAll(loadActivityChapterList("chapter_activity.json"));
        expReturn = JSON.parseArray(ComponentUtils.readDataFile("exp_return.json"));
        refreshCost = JSON.parseArray(ComponentUtils.readDataFile("chapter_resetcount_cost.json"));
    }

    @Override
    protected void doReload() {
    }

    @Override
    protected void doDestroy() {
    }

    /**
     *
     * @param packet
     */
    public void searchSuccor(Packet packet) {
        warProvider.searchSuccor(packet);
    }

    /**
     *
     * @param packet
     */
    public void loadSuccorData(SingleValue packet) {
        warProvider.loadSuccorData(packet);
    }

    /**
     * 重置副本次数.
     *
     * @param packet
     */
    public void resetPveCounts(PvePacket packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Vip vip = roleProvider.getVip(normal.getVipLevel());
        PveProgress pveProgress;
        int count = 0;
        // s史诗 精英 列传
        if (packet.getSubject() == PVE_SUBJECT_HISTORY) {
            if (packet.getMode() == PVE_MODE_DIFFICULT) {
                pveProgress = normal.findHdpveProgress(packet.getCidx(), packet.getPidx());
                count = pveProgress.getResetCount();
            } else {
                pveProgress = normal.findHpveProgress(packet.getCidx(), packet.getPidx());
                count = pveProgress.getResetCount();
            }
        } else {
            pveProgress = normal.findSpveProgress(packet.getCidx(), packet.getPidx());
            count = pveProgress.getResetCount();
        }

        if (count < vip.privileged.max_pve_buy_count) {
            pveProgress.setCount(0);
            JSONObject object = new JSONObject();
            object.put("place", "ResetPveCounts");
            object.put("resetCounts", count);
            pveProgress.setResetCount(count + 1);
            SessionUtils.decrementDiamond(refreshCost.getIntValue(count), object.toString());
        } else {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("VIP level is not enough");
            player.getSession().write(error);
            return;
        }
        player.getSession().write(Packet.createResult(packet));
    }

    /**
     *
     * @param packet
     */
    public void enter(PvePacket packet) {
        Session session = SessionContext.getSession();
        Player player = SessionUtils.getPlayer(session);
        Normal normal = player.getNormal();

        Part part
                = getPart0(packet.getSubject(), packet.getMode(), packet.getCidx(), packet.getPidx());
        PveProgress pveProgress
                = findPveProgress(packet.getSubject(), packet.getMode(), packet.getCidx(),
                        packet.getPidx());

        if (!check(packet, session, normal, pveProgress, part, 1)) {
            return;
        }

        PveWarInfo pveWarInfo = newPveWarInfo(packet, part);
        pveWarInfo.setWarId(packet.getId());
        SessionUtils.decrementVigor(part.getPrevigor());

        player.getInvisible().setSingleAtlasloots(null);
        player.getInvisible().setPveWarInfo(pveWarInfo);

        // 初始化掉落物品
        List<AtlaslootBean> atlasloots = new ArrayList<>();
        PossibleAtlasloot tmp;
        atlaslootProvider
                .calculateAtlasloot(session, pveProgress, atlasloots, part.getAtlasloots());
        for (AtlaslootBean bean : atlasloots) {
            tmp = new PossibleAtlasloot(bean.getItem().getId(), bean.getCount());
            if (bean.getItem() instanceof HeroItemDobj) {
                Bag heroBag = player.getHeroBag();
                if (heroBag.findNode(tmp.getItemId()) != null) {
                    tmp.setIsSoul(1);
                }
            }
            pveWarInfo.addAtlasloot(tmp);
        }

        // 扣除体力
        session.setAttribute(WAR_INFO_KEY, pveWarInfo);

        // 设置攻击组索引
        normal.setLastFidx(packet.getFgidx());

        // result
        PvePacket result = new PvePacket();
        Packet.assignResult(packet, result);
        result.setAtlasloots(pveWarInfo.getAtlasloots());
        session.write(result);
    }

    private boolean check(PvePacket packet, Session session, Normal normal,
            PveProgress pveProgress, Part part, int count) {
        // 已经存在战斗
        if (session.getAttribute(WAR_INFO_KEY) != null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.bat_request);
            error.setText("exists attack");
            session.write(error);
            return false;
        }

        // PVE进度不足
        boolean noPveEnough = isPveEnough(packet);
        if (!noPveEnough) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.bat_request);
            error.setText("pve not enough");
            session.write(error);
            return false;
        }

        // 等级不满足
        if (normal.getLevel() < part.getMinLevel()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.level_limit);
            error.setText("level limit");
            session.write(error);
            return false;
        }

        // 体力不足
        boolean noVigorEnough
                = normal.getVigor() >= (part.getPrevigor() + part.getPostvigor()) * count;
        if (!noVigorEnough) {
            PacketError error
                    = PacketError.createResult(packet, PacketError.Condition.vigor_not_enough);
            error.setText("vigor not enough");
            session.write(error);
            return false;
        }

        if (packet.getSubject() == PVE_SUBJECT_ACTIVITY) {
            ActivityChapter chapter = (ActivityChapter) part.getParent();
            if (!chapter.containtsWeek(Week.currentDayOfWeek())) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("No activity");
                session.write(error);
                return false;
            }

            if (chapter.getCount() != -1 && pveProgress != null
                    && pveProgress.getCount() >= chapter.getCount()) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("[Activity]-Not enough count");
                session.write(error);
                return false;
            }
        } else {
            // 次数不足
            if (part.getCount() != -1 && pveProgress != null
                    && pveProgress.getCount() * count >= part.getCount()) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Not enough count");
                session.write(error);
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param packet
     */
    public void exit(PvePacket packet) {
        Session session = SessionContext.getSession();
        Player player = SessionUtils.getPlayer();
        PveWarInfo pveWarInfo = (PveWarInfo) session.getAttribute(WAR_INFO_KEY);
        if (pveWarInfo == null) {
            pveWarInfo = player.getInvisible().getPveWarInfo();
        }

        Normal normal = player.getNormal();
        // 不存在战斗
        if (pveWarInfo == null || !pveWarInfo.getWarId().equals(packet.getId())) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.bat_request);
            error.setText("No attack");
            session.write(error);
            return;
        }

        // 战斗结算
        if (packet.getSidx() >= pveWarInfo.getPart().getStepSize() - 1) {
            int aid = packet.getAid();
            int index = 0;
//            Friend f = null;
            int counts = Typhons.getInteger("typhon.spi.DailyVigorFromFriend.TotalTimes");
//            for (Friend friend : normal.getFriends()) {
//                if (friend.getRid() == aid) {
//                    f = friend;
//                    break;
//                }
//            }
            if (aid > 0 ) {
                Session bession = sessionManager.getSession(aid);
                if (bession == null) {
                    VacantData vacantData = roleProvider.loadVacantData(aid);
                    for (RecordObject object : vacantData.getAidReceiveCounts()) {
                        if (object.getState() == 0) {
                            index++;
                        }
                    }
                    if (index < counts) {
                        Incident incident = new Incident();
                        incident.setUid(aid);
                        incident.setEventName(IncidentConstants.ADI_RECEIVE_VIGOR);
                        incidentReposy.save(incident);
                    }
                } else {
                    Player bePlayer = SessionUtils.getPlayer(bession);
                    for (RecordObject object : bePlayer.getNormal().getAidReceiveCounts()) {
                        if (object.getState() == 0) {
                            index++;
                        }
                    }
                    if (index < counts) {
                        bePlayer.getNormal().AddAidReceiveCounts(
                                new RecordObject(
                                        bePlayer.getNormal().getAidReceiveCounts().size() + 1, 0));
                    }
                }
                normal.addSuccors(new SuccorObject(aid, System.currentTimeMillis()));
            }
            taskPveProgressProvider.update(normal, pveWarInfo);
            SessionUtils.decrementVigor(pveWarInfo.getPart().getPostvigor());
            PveProgress pveProgress = finalEstimate(session, pveWarInfo);
            FightGroup fightGroup = normal.getFightGroup(pveWarInfo.getFgidx());
            for (HeroItem hero : fightGroup.getHeroItems()) {
                heroProvider.pushExp(normal.getLevel(), hero, pveWarInfo.getPart().getHeroExp());
            }

            if (pveWarInfo.getSubject() != PVE_SUBJECT_ACTIVITY) {
                
                int oldStar = normal.getPveStarCounts();
                // 成就结算
                if (pveProgress.getFru1() != 1) {
                    pveProgress.setFru1(packet.getFru1());
                }
                if (pveProgress.getFru2() != 1) {
                    pveProgress.setFru2(packet.getFru2());
                }
                if (pveProgress.getFru3() != 1) {
                    pveProgress.setFru3(packet.getFru3());
                }
                int newStar = returnHeroStar(player);
                
                if (oldStar != newStar) {
                    normal.setPveStarCounts(newStar);
                    updateRankingList.updateHeroStarRanking();
                }
            }
        }

        player.getInvisible().setPveWarInfo(null);
        player.getInvisible().setSingleAtlasloots(null);
        session.removeAttribute(WAR_INFO_KEY);

        // result
        SingleValue result = new SingleValue();
        result.setVal(SingleValue.SUCCESS);
        Packet.assignResult(packet, result);
        session.write(result);
    }

    /**
     *
     * @param packet
     */
    public void cleanOut(PveCleanPacket packet) {
        Session session = SessionContext.getSession();
        Player player = SessionUtils.getPlayer(session);
        Normal normal = player.getNormal();
        int count = packet.getCount();
        Vip vip = roleProvider.getVip(normal.getVipLevel());

        if (count > vip.privileged.pve_sweep_counts) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.bat_request);
            error.setText("vip is not through");
            session.write(error);
            return;
        }

        PveProgress pveProgress
                = findPveProgress(packet.getSubject(), packet.getMode(), packet.getCidx(),
                        packet.getPidx());

        if (pveProgress == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.bat_request);
            error.setText("pve is not through");
            session.write(error);
            return;
        }

        Part part = getPart0(packet.getSubject(), packet.getMode(), packet.getCidx(), packet.getPidx());
        int expSum;
        if (!check(packet, session, normal, pveProgress, part, count)) {
            return;
        }

        PveWarInfo pveWarInfo = newPveWarInfo(packet, part);
        player.getInvisible().setSingleAtlasloots(null);
        List<List<PossibleAtlasloot>> allAtlasloots = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            List<AtlaslootBean> atlasloots = new ArrayList<>();
            atlaslootProvider.calculateAtlasloot(session, pveProgress, atlasloots,
                    part.getAtlasloots());
            
            PossibleAtlasloot tmp;
            for (AtlaslootBean bean : atlasloots) {
                tmp = new PossibleAtlasloot(bean.getItem().getId(), bean.getCount());
                if (bean.getItem() instanceof HeroItemDobj) {
                    Bag heroBag = player.getHeroBag();
                    if (heroBag.findNode(tmp.getItemId()) != null) {
                        tmp.setIsSoul(1);
                    }
                }
                
                BagUtils.intoItem(itemProvider.getItem(tmp.getItemId()), tmp.getCount());
                pveWarInfo.addAtlasloot(tmp);
            }

            // 增加副本的挑战次数
            pveProgress.setTotal(pveProgress.getTotal() + 1);
            pveProgress.setCount(pveProgress.getCount() + 1);

            // 记录需要掉落统计的信息
            List<String> singleLoots
                    = (List<String>) SessionUtils.getPlayer(session).getInvisible().getSingleAtlasloots();
            if (singleLoots != null) {
                Record record;
                for (String sid : singleLoots) {
                    record = pveProgress.findRecord(sid);
                    if (record == null) {
                        record = new Record(sid);
                        pveProgress.addRecord(record);
                    }
                    record.setLastTime(pveProgress.getTotal());
                }
            }

            // 刷新特殊商店
            if (RandomUtils.nextDouble() < 0.005) {
                marketStoreProvider.autoRefreshCommodity(normal.player());
            }
            if (RandomUtils.nextDouble() < 0.003) {
                westernStoreProvider.autoRefreshCommodity(normal.player());
            }

            allAtlasloots.add(new ArrayList<>(pveWarInfo.getAtlasloots()));

            pveWarInfo.getAtlasloots().clear();
            SessionUtils.getPlayer(session).getInvisible().setSingleAtlasloots(null);
            
            session.removeAttribute(WAR_INFO_KEY);
        }

        int counts;
        if (pveWarInfo.getSubject() == PVE_SUBJECT_HISTORY) {
            if (pveWarInfo.getMode() == PVE_MODE_DIFFICULT) {
                counts = normal.getDailyTask().getTaskHdpveCounts();
                if (counts >= 0) {
                    normal.getDailyTask().setTaskHdpveCounts(counts + count);
                }
            } else {
                counts = normal.getDailyTask().getTaskHpveCounts();
                if (counts >= 0) {
                    normal.getDailyTask().setTaskHpveCounts(counts + count);
                }
            }
        } else if (pveWarInfo.getSubject() == PVE_SUBJECT_ACTIVITY) {
            counts = normal.getDailyTask().getTaskActivities();
            if (counts >= 0) {
                normal.getDailyTask().setTaskActivities(counts + count);
            }
        } else {
            counts = normal.getDailyTask().getTaskSpveCounts();
            if (counts >= 0) {
                normal.getDailyTask().setTaskSpveCounts(counts + count);
            }
        }

        // FIXME
        SessionUtils.incrementCopper(pveWarInfo.getPart().getCopper() * count);
        roleProvider.pushExp(normal, pveWarInfo.getPart().getExp() * count);
        
        expSum = pveWarInfo.getPart().getHeroExp() * 5;
        // 扣去体力
        SessionUtils.decrementVigor((part.getPrevigor() + part.getPostvigor()) * count);
        List<PossibleAtlasloot> atlasloots = new ArrayList<>();

        // 卡牌经验结算
        // 单次不足最低档次的经验药水，算一个经验药水
        if (expSum < expReturn.getJSONObject(expReturn.size() - 1).getIntValue("exp")) {
            posible(atlasloots, expReturn.getJSONObject(expReturn.size() - 1), 1, count);
        } else {
            for (int i = 0; i < expReturn.size(); i++) {
                JSONObject cur = expReturn.getJSONObject(i);

                int b = expSum / cur.getIntValue("exp");
                if (b <= 0) {
                    continue;
                }

                posible(atlasloots, cur, b, count);
                int a = expSum % cur.getIntValue("exp");

                JSONObject prev = expReturn.getJSONObject(expReturn.size() - 1);
                if (a > prev.getIntValue("exp")) {
                    expSum = a;
                    continue;
                } else if (a <= prev.getIntValue("exp") && a > 0) {
                    posible(atlasloots, prev, 1, count);
                    break;
                }

                if (a == 0) {
                    break;
                }
            }
        }
        
        taskPveProgressProvider.update(normal, pveWarInfo);
        
        PveCleanPacket result = new PveCleanPacket();
        Packet.assignResult(packet, result);
        result.setAtlasloots(atlasloots);
        result.setAllAtlasloots(allAtlasloots);
        session.write(result);
    }

    // 卡牌经验对黄经验药水
    private void posible(List<PossibleAtlasloot> atlasloots, JSONObject object, int b, int count) {
        PossibleAtlasloot possibleAtlasloot = new PossibleAtlasloot();
        boolean bool = true;
        BagUtils.intoItem(itemProvider.getItem(object.getString("#item.id")), b * count);

        for (PossibleAtlasloot p : atlasloots) {
            if (p.getItemId().equals(object.getString("#item.id"))) {
                p.setCount(p.getCount() + b * count);
                bool = false;
                break;
            }
        }
        if (bool) {
            possibleAtlasloot.setItemId(object.getString("#item.id"));
            possibleAtlasloot.setCount(b * count);
            atlasloots.add(possibleAtlasloot);
        }
    }

    /**
     *
     * @param packet
     */
    public void receiveFruitionAward(PvePacket packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        PveProgress pveProgress;
        Part part;
        if (packet.getSubject() == PVE_SUBJECT_HISTORY) {
            if (packet.getMode() == PVE_MODE_DIFFICULT) {
                pveProgress = normal.findHdpveProgress(packet.getCidx(), packet.getPidx());
                part = historyDifficultChapters.get(packet.getCidx()).getPart(packet.getPidx());
            } else {
                pveProgress = normal.findHpveProgress(packet.getCidx(), packet.getPidx());
                part = historyChapters.get(packet.getCidx()).getPart(packet.getPidx());
            }
        } else {
            pveProgress = normal.findSpveProgress(packet.getCidx(), packet.getPidx());
            part = storyChapters.get(packet.getCidx()).getPart(packet.getPidx());
        }

        if (pveProgress == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("No PveProgress");
            player.getSession().write(error);
            return;
        }

        if (packet.getFru1() != 0) {
            if (pveProgress.getHrecd1() != 0) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Fru1: have received");
                player.getSession().write(error);
                return;
            }

            if (pveProgress.getFru1() != 1) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Fru1: no finished");
                player.getSession().write(error);
                return;
            }
            BagUtils.intoItem(part.getFruItem1());
            pveProgress.setHrecd1(1);
        }

        if (packet.getFru2() != 0) {
            if (pveProgress.getHrecd2() != 0) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Fru2: have received");
                player.getSession().write(error);
                return;
            }

            if (pveProgress.getFru2() != 1) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Fru2: no finished");
                player.getSession().write(error);
                return;
            }
            BagUtils.intoItem(part.getFruItem2());
            pveProgress.setHrecd2(1);
        }

        if (packet.getFru3() != 0) {
            if (pveProgress.getHrecd3() != 0) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Fru3: have received");
                player.getSession().write(error);
                return;
            }

            if (pveProgress.getFru3() != 1) {
                PacketError error
                        = PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("Fru3: no finished");
                player.getSession().write(error);
                return;
            }
            BagUtils.intoItem(part.getFruItem3());
            pveProgress.setHrecd3(1);
        }

        player.getSession().write(Packet.createResult(packet));
    }

    public void cleanFruitionAward(PvePacket packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        PveProgress pveProgress;
        int cid = packet.getCidx();
        Chapter chapter = null;
        List<PveProgress> list = new ArrayList<>();
        Part part;
        if (packet.getSubject() == PVE_SUBJECT_HISTORY) {
            if (packet.getMode() == PVE_MODE_DIFFICULT) {
                chapter = historyDifficultChapters.get(cid);
                for (int j = 0; j < chapter.getParts().length; j++) {
                    list.add(normal.findHdpveProgress(cid, j));
                }
            } else {
                chapter = historyChapters.get(cid);
                for (int j = 0; j < chapter.getParts().length; j++) {
                    list.add(normal.findHpveProgress(cid, j));
                }
            }
        } else {
            chapter = storyChapters.get(cid);
            for (int j = 0; j < chapter.getParts().length; j++) {
                list.add(normal.findSpveProgress(cid, j));
            }
        }
        for (int i = 0; i < chapter.getParts().length; i++) {
            part = chapter.getPart(i);
            pveProgress = list.get(i);

            if (pveProgress == null) {
                break;
            }
            if (pveProgress.getHrecd1() == 0 && pveProgress.getFru1() == 1) {
                pveProgress.setHrecd1(1);
                BagUtils.intoItem(part.getFruItem1());
            }
            if (pveProgress.getHrecd2() == 0 && pveProgress.getFru2() == 1) {
                pveProgress.setHrecd2(1);
                BagUtils.intoItem(part.getFruItem2());
            }
            if (pveProgress.getHrecd3() == 0 && pveProgress.getFru3() == 1) {
                pveProgress.setHrecd3(1);
                BagUtils.intoItem(part.getFruItem3());
            }
        }
        player.getSession().write(Packet.createResult(packet));
    }

    private PveProgress finalEstimate(Session session, PveWarInfo pveWarInfo) {
        SessionUtils.incrementCopper(pveWarInfo.getPart().getCopper());

        // 结算经验
        Normal normal = SessionUtils.getPlayer(session).getNormal();
        int counts;
        PveProgress pveProgress;
        if (pveWarInfo.getSubject() == PVE_SUBJECT_HISTORY) {
            if (pveWarInfo.getMode() == PVE_MODE_DIFFICULT) {
                pveProgress = normal.findHdpveProgress(pveWarInfo.getCidx(), pveWarInfo.getPidx());
                if (pveProgress == null) {
                    pveProgress = newPveProgress(pveWarInfo);
                    normal.addHdpveProgress(pveProgress);
                    updateRankingList.updatePveDifficultRanking();
                }

                counts = normal.getDailyTask().getTaskHdpveCounts();
                if (counts >= 0) {
                    normal.getDailyTask().setTaskHdpveCounts(counts + 1);
                }
            } else {
                pveProgress = normal.findHpveProgress(pveWarInfo.getCidx(), pveWarInfo.getPidx());
                if (pveProgress == null) {
                    pveProgress = newPveProgress(pveWarInfo);
                    normal.addHpveProgress(pveProgress);
                    updateRankingList.updatePveRanking();
                }

                counts = normal.getDailyTask().getTaskHpveCounts();
                if (counts >= 0) {
                    normal.getDailyTask().setTaskHpveCounts(counts + 1);
                }
            }
        } else if (pveWarInfo.getSubject() == PVE_SUBJECT_ACTIVITY) {
            pveProgress = normal.findApveProgress(pveWarInfo.getCidx());
            if (pveProgress == null) {
                pveProgress = newPveProgress(pveWarInfo);
                normal.addApveProgress(pveProgress);
            }

            counts = normal.getDailyTask().getTaskActivities();
            if (counts >= 0) {
                normal.getDailyTask().setTaskActivities(counts + 1);
            }
        } else {
            pveProgress = normal.findSpveProgress(pveWarInfo.getCidx(), pveWarInfo.getPidx());
            if (pveProgress == null) {
                pveProgress = newPveProgress(pveWarInfo);
                normal.addSpveProgress(pveProgress);
            }

            counts = normal.getDailyTask().getTaskSpveCounts();
            if (counts >= 0) {
                normal.getDailyTask().setTaskSpveCounts(counts + 1);
            }
        }

        roleProvider.pushExp(normal, pveWarInfo.getPart().getExp());

        // 增加副本的挑战次数
        pveProgress.setTotal(pveProgress.getTotal() + 1);
        pveProgress.setCount(pveProgress.getCount() + 1);

        // 掉落结算
        if (pveWarInfo.getAtlasloots() != null) {
            for (PossibleAtlasloot atlasloot : pveWarInfo.getAtlasloots()) {
                BagUtils.intoItem(itemProvider.getItem(atlasloot.getItemId()), atlasloot.getCount());
            }
        }

        // 记录需要掉落统计的信息
        List<String> singleLoots
                = (List<String>) SessionUtils.getPlayer(session).getInvisible().getSingleAtlasloots();
        if (singleLoots != null) {
            Record record;
            for (String sid : singleLoots) {
                record = pveProgress.findRecord(sid);
                if (record == null) {
                    record = new Record(sid);
                    pveProgress.addRecord(record);
                }
                record.setLastTime(pveProgress.getTotal());
            }
        }

        // 刷新特殊商店
        if (RandomUtils.nextDouble() < 0.005) {
            marketStoreProvider.autoRefreshCommodity(normal.player());
        }
        if (RandomUtils.nextDouble() < 0.003) {
            westernStoreProvider.autoRefreshCommodity(normal.player());
        }
        return pveProgress;
    }

    private PveWarInfo newPveWarInfo(PvePacket packet, Part part) {
        PveWarInfo pveWarInfo = new PveWarInfo();
        pveWarInfo.setSubject(packet.getSubject());
        pveWarInfo.setMode(packet.getMode());
        pveWarInfo.setCidx(packet.getCidx());
        pveWarInfo.setPidx(packet.getPidx());
        pveWarInfo.setFgidx(packet.getFgidx());
        pveWarInfo.setPart(part);
        return pveWarInfo;
    }

    private Part getPart0(int subject, int mode, int cidx, int pidx) {
        if (subject == PVE_SUBJECT_HISTORY) {
            if (mode == PVE_MODE_DIFFICULT) {
                return historyDifficultChapters.get(cidx).getPart(pidx);
            } else {
                return historyChapters.get(cidx).getPart(pidx);
            }
        } else if (subject == PVE_SUBJECT_ACTIVITY) {
            return activityChapters.get(cidx).getPart(pidx);
        } else {
            return storyChapters.get(cidx).getPart(pidx);
        }
    }

    private boolean isPveEnough(PvePacket packet) {
        // 如果是活动篇则不检查PVE进度
        if (packet.getSubject() == PVE_SUBJECT_ACTIVITY) {
            return true;
        }

        // 如果是列传篇则需要检查对应的普通本是否通关
        if (packet.getSubject() == PVE_SUBJECT_STORY) {
            Part part = storyChapters.get(packet.getCidx()).getPart(packet.getPidx());
            Normal normal = SessionUtils.getPlayer().getNormal();
            return (normal.findHpveProgress(part.getHcidx(), part.getHpidx()) != null);
        }

        // 如果是精英本则需要检查对应的普通本是否通关
        if (packet.getMode() == PVE_MODE_DIFFICULT) {
            Normal normal = SessionUtils.getPlayer().getNormal();
            if (packet.getSubject() == PVE_SUBJECT_HISTORY) { // 史实篇
                Chapter chapter = historyChapters.get(packet.getCidx());
                if (normal.findHpveProgress(packet.getCidx(), chapter.getParts().length - 1) == null) {
                    return false;
                }
            }
        }

        if (packet.getCidx() == 0 && packet.getPidx() == 0) {
            return true;
        }

        return (findPrevPveProgress(packet.getSubject(), packet.getMode(), packet.getCidx(),
                packet.getPidx()) != null);
    }

    private PveProgress findPrevPveProgress(int subject, int mode, int cidx, int pidx) {
        if (pidx <= 0) {
            cidx--;
            pidx = getPart0(subject, mode, cidx, pidx).getParent().getParts().length - 1;
        } else {
            pidx--;
        }
        return findPveProgress(subject, mode, cidx, pidx);
    }

    private PveProgress findPveProgress(int subject, int mode, int cidx, int pidx) {
        Normal normal = SessionUtils.getPlayer().getNormal();
        if (subject == PVE_SUBJECT_HISTORY) {
            if (mode == PVE_MODE_DIFFICULT) {
                return normal.findHdpveProgress(cidx, pidx);
            } else {
                return normal.findHpveProgress(cidx, pidx);
            }
        } else if (subject == PVE_SUBJECT_ACTIVITY) {
            return normal.findApveProgress(cidx);
        } else {
            return normal.findSpveProgress(cidx, pidx);
        }
    }

    private List<Chapter> loadChapterList(String fileName) {
        String text = ComponentUtils.readDataFile(fileName);
        JSONArray datas = JSON.parseArray(text);
        List<Chapter> list = new ArrayList<>();

        for (Iterator<Object> it = datas.iterator(); it.hasNext();) {
            JSONObject obj = (JSONObject) it.next();
            Chapter chapter = TypeUtils.castToJavaBean(obj, Chapter.class);

            for (int i = 0; i < chapter.getParts().length; i++) {
                Part part = chapter.getPart(i);
                JSONObject partJson = obj.getJSONArray("parts").getJSONObject(i);

                // 成就奖励 -------------------++++++++++++++++++++++++++++++++++++++++++++
                if (partJson.containsKey("#fru1.item.id")) {
                    String itemId = partJson.getString("#fru1.item.id");
                    if (StringUtils.isNotEmpty(itemId)) {
                        part.setFruItem1(itemProvider.getItem(itemId));
                    }
                }
                if (partJson.containsKey("#fru2.item.id")) {
                    String itemId = partJson.getString("#fru2.item.id");
                    if (StringUtils.isNotEmpty(itemId)) {
                        part.setFruItem2(itemProvider.getItem(itemId));
                    }
                }
                if (partJson.containsKey("#fru3.item.id")) {
                    String itemId = partJson.getString("#fru3.item.id");
                    if (StringUtils.isNotEmpty(itemId)) {
                        part.setFruItem3(itemProvider.getItem(itemId));
                    }
                }
                // 成就奖励 -------------------++++++++++++++++++++++++++++++++++++++++++++

                part.setHcidx(partJson.getIntValue("hcidx"));
                part.setHpidx(partJson.getIntValue("hpidx"));
            }

            list.add(chapter);
        }

        return list;
    }

    private List<ActivityChapter> loadActivityChapterList(String fileName) {
        String text = ComponentUtils.readDataFile(fileName);
        JSONArray datas = JSON.parseArray(text);
        List<ActivityChapter> list = new ArrayList<>();

        for (Iterator<Object> it = datas.iterator(); it.hasNext();) {
            JSONObject obj = (JSONObject) it.next();
            ActivityChapter chapter = TypeUtils.castToJavaBean(obj, ActivityChapter.class);
            list.add(chapter);
        }

        return list;
    }

    private PveProgress newPveProgress(PveWarInfo pveWarInfo) {
        return new PveProgress(pveWarInfo.getCidx(), pveWarInfo.getPidx());
    }
    
    public int returnHeroStar(Player player) {
        Normal normal = player.getNormal();
        return StarSum(normal.getSpveProgresses()) + StarSum(normal.getHdpveProgresses())
                + StarSum(normal.getHpveProgresses());
    }

    private int StarSum(List<PveProgress> list) {
        int count = 0;
        for (PveProgress object : list) {
            if (object.getFru1() == 1) {
                count++;
            }
            if (object.getFru2() == 1) {
                count++;
            }
            if (object.getFru3() == 1) {
                count++;
            }
        }
        return count;
    }


}
