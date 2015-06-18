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
package org.skfiy.typhon.spi.ranking;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.GlobalData;
import org.skfiy.typhon.domain.HeroProperty;
import org.skfiy.typhon.domain.HeroPropertyKeys;
import org.skfiy.typhon.domain.IHeroEntity;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.PveProgress;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.repository.GlobalDataRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.pve.RankingListRival;
import org.skfiy.typhon.spi.society.SocietyProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class AbstractRankingProvider extends AbstractComponent {

    private static final Timer TIMER = new Timer("Ranking-Timer", true);

    private final List<RankingObject> rankingObjects = new ArrayList<>(50);

    @Inject
    protected GlobalDataRepository globalDataReposy;

    @Inject
    protected SessionManager sessionManager;
    @Inject
    protected RoleProvider roleProvider;
    @Inject
    protected SocietyProvider societyProvider;

    @Override
    protected void doInit() {
        rankingObjects.addAll(JSON.parseArray(loadRankingData(), RankingObject.class));

        // 定时保存PVE数据
        TIMER.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                saveRankingData();
            }

        }, Typhons.getLong("typhon.spi.ranking.saveDataFixedRateMs"),
                Typhons.getLong("typhon.spi.ranking.saveDataFixedRateMs"));
        // FIXME 统一的参数设计
    }

    @Override
    protected void doReload() {}

    @Override
    protected void doDestroy() {
        saveRankingData();
    }

    /**
     * 
     * @param player
     */
    public synchronized boolean updateRanking(Player player) {
        boolean bool = false;
        if (player.getNormal().getLevel() < 10) {
            return bool;
        }
        if (rankingObjects.isEmpty()) {
            rankingObjects.add(newRankingObject(player));
            bool=true;
        } else {
            RankingObject rankingObject;
            for (int i = rankingObjects.size() - 1; i >= 0; i--) {
                rankingObject = rankingObjects.get(i);
                if (compare(player, rankingObject)) {
                    rankingObject = newRankingObject(player);
                    removeSpilthRanking(rankingObject.getRid());
                    rankingObjects.add(i, rankingObject);
                } else if (!bool) {
                    if (rankingObjects.size() < 50) {
                        rankingObject = newRankingObject(player);
                        removeSpilthRanking(rankingObject.getRid());
                        rankingObjects.add(rankingObject);
                    }
                    break;
                }
                bool = true;
            }
        }
        return bool;
    }

    public List<RankingListRival> loadLevelRankingList(SingleValue packet) {
        List<RankingListRival> list = new ArrayList<>();
        for (int i = 0; i < rankingObjects.size(); i++) {
            RankingObject pveO = rankingObjects.get(i);
            list.add(loadRankingObject(pveO));
        }
        return list;
    }

    protected RankingListRival loadRankingObject(RankingObject pve0) {
        // FIXME 实现
        List<Object> heros = new ArrayList<>();
        RankingListRival pveRival = new RankingListRival();
        Session session = sessionManager.getSession(pve0.getRid());
        if (session == null) {
            VacantData vacantData = roleProvider.loadVacantData(pve0.getRid());
            pveRival.setName(vacantData.getName());
            pveRival.setSocietyName(vacantData.getSocietyName());
            String[] fightGroup = vacantData.getFightGroups()[vacantData.getLastFidx()];
            for (String id : fightGroup) {
                HeroProperty hero = vacantData.findHeroProperty(id);
                heros.add(newHeroJSONObject(hero));
            }
            pveRival.setHero(heros);

        } else {
            Player player = SessionUtils.getPlayer(session);
            Role role = player.getRole();
            pveRival.setName(role.getName());
            pveRival.setLevel(role.getLevel());

            Normal normal = player.getNormal();
            pveRival.setSocietyName(normal.getSocietyName());
            FightGroup fightGroup = normal.getFightGroup(normal.getLastFidx());
            for (HeroItem heroItem : fightGroup.getHeroItems()) {
                heros.add(newHeroJSONObject(heroItem));
            }
            pveRival.setHero(heros);
        }
        pveRival.setPveProgresses(pve0.getPveProgresses());
        pveRival.setAvatar(pve0.getAvatar());
        pveRival.setAvatarBorder(pve0.getAvatarBorder());
        pveRival.setLevel(pve0.getLevel());
        pveRival.setPowerGuess(pve0.getPowerGuess());
        pveRival.setStar(pve0.getStar());
        pveRival.setHdPveProgresses(pve0.getHdPveProgresses());
        return pveRival;
    }

    private Object newHeroJSONObject(IHeroEntity hero) {
        JSONObject json = new JSONObject();
        json.put(HeroPropertyKeys.ID, hero.getId());
        json.put(HeroPropertyKeys.LEVEL, hero.getLevel());
        json.put(HeroPropertyKeys.STAR, hero.getStar());
        json.put(HeroPropertyKeys.LADDER, hero.getLadder());
        return json;
    }

    /**
     * 
     * @param player
     * @return
     */
    protected RankingObject newRankingObject(Player player) {
        // FIXME
        RankingObject rankingObject = new RankingObject();
        Normal normal = player.getNormal();
        rankingObject.setPveProgresses(normal.getHpveProgresses().size());
        rankingObject.setRid(player.getRole().getRid());
        rankingObject.setAvatar(normal.getAvatar());
        rankingObject.setAvatarBorder(player.getNormal().getAvatarBorder());
        rankingObject.setLevel(normal.getLevel());
        rankingObject.setPowerGuess(returnPowerGuess(player));
        rankingObject.setHdPveProgresses(normal.getHdpveProgresses().size());
        rankingObject.setStar(normal.getPveStarCounts());
        return rankingObject;
    }

    protected void removeSpilthRanking(int rid) {
        RankingObject pveObject;
        for (int i = 0; i < rankingObjects.size(); i++) {
            pveObject = rankingObjects.get(i);

            if (pveObject.getRid() == rid) {
                rankingObjects.remove(i);
                break;
            }
        }
        if (rankingObjects.size() > 49) {
            rankingObjects.remove(rankingObjects.size() - 1);
        }
    }

    private String loadRankingData() {
        GlobalData globalData = globalDataReposy.getGlobalData(getGlobalDataType());
        return globalData.getData();
    }

    private void saveRankingData() {
        GlobalData globalData = new GlobalData();
        globalData.setType(getGlobalDataType());
        globalData.setData(JSON.toJSONString(rankingObjects));
        globalDataReposy.updateGlobalData(globalData);
    }



    public void updateOtherRanking(Player player) {
        Normal normal = player.getNormal();
        int id = player.getRole().getRid();
        for (int i = 0; i < rankingObjects.size(); i++) {
            RankingObject rankingObject = rankingObjects.get(i);
            if (rankingObject.getRid() == id) {
                rankingObject.setAvatar(normal.getAvatar());
                rankingObject.setAvatarBorder(normal.getAvatarBorder());
                rankingObject.setLevel(normal.getLevel());
                rankingObject.setPveProgresses(normal.getHpveProgresses().size());
                rankingObject.setPowerGuess(returnPowerGuess(player));
                rankingObject.setHdPveProgresses(normal.getHdpveProgresses().size());
                rankingObject.setStar(normal.getPveStarCounts());
                break;
            }
        }
    }

    public void levleUpdateOtherRanking(Player player) {
        Normal normal = player.getNormal();
        int id = player.getRole().getRid();
        if (rankingObjects.size() == 0) {
            updateRanking(player);
        } else {
            for (int i = 0; i < rankingObjects.size(); i++) {
                RankingObject rankingObject = rankingObjects.get(i);
                if (rankingObject.getRid() == id) {
                    rankingObject.setAvatar(normal.getAvatar());
                    rankingObject.setAvatarBorder(normal.getAvatarBorder());
                    rankingObject.setLevel(normal.getLevel());
                    rankingObject.setPveProgresses(normal.getHpveProgresses().size());
                    rankingObject.setPowerGuess(returnPowerGuess(player));
                    rankingObject.setHdPveProgresses(normal.getHdpveProgresses().size());
                    rankingObject.setStar(normal.getPveStarCounts());
                    break;
                } else if (i == rankingObjects.size() - 1) {
                    updateRanking(player);
                }
            }
        }
    }

    /**
     * 
     * @return
     */
    protected abstract GlobalData.Type getGlobalDataType();

    /**
     * 
     * @param player
     * @param rankingObject
     * @return
     */
    protected abstract boolean compare(Player player, RankingObject rankingObject);



    protected int returnPowerGuess(Player player) {
        int powerGuess = 0;
        Normal normal = player.getNormal();
        FightGroup fightGroup = normal.getFightGroup(normal.getLastFidx());
        for (HeroItem heroItem : fightGroup.getHeroItems()) {
            powerGuess += heroItem.getPowerGuess();
        }
        return powerGuess;
    }

    protected List<RankingObject> returnRankings() {
        return rankingObjects;
    }
}
