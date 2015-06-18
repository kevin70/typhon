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
package org.skfiy.typhon.spi.war;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.inject.Inject;
import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.modeler.ManagedBean;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.dobj.BSaSkill;
import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.dobj.JoinSkill;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.Friend;
import org.skfiy.typhon.domain.HeroPropertyKeys;
import org.skfiy.typhon.domain.IHeroEntity;
import org.skfiy.typhon.domain.ITroop;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Troop;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.domain.item.IFightItem;
import org.skfiy.typhon.domain.item.IFightItem.Shot;
import org.skfiy.typhon.domain.item.Race;
import org.skfiy.typhon.domain.item.SuccorObject;
import org.skfiy.typhon.packet.MultipleValue;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketSuccor;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.script.ScriptManager;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.CacheKeys;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.hero.HeroProvider;
import org.skfiy.typhon.spi.society.Society;
import org.skfiy.typhon.spi.society.SocietyProvider;
import org.skfiy.typhon.spi.war.WarCombo.Point;
import org.skfiy.typhon.util.ComponentUtils;
import org.skfiy.typhon.util.FastRandom;
import org.skfiy.typhon.util.MBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.skfiy.typhon.domain.IHeroEntity.Rabbet;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class WarProvider extends AbstractComponent {

    private static final Logger LOG = LoggerFactory.getLogger(WarProvider.class);

    private static final int ROW_1ST = 0;
    private static final int ROW_2ND = 1;
    private static final int ROW_3RD = 2;

    private static final double MIN_FACTOR = 0.95;
    private final Random HOLD_POINT_RANDOM = new FastRandom();
    private final Random FACTOR_RANDOM = new FastRandom();

    private static final FastRandom HERO_ID_RANDOM = new FastRandom();

    private ObjectName oname;

    // 初始数据配置
    private RaceFactorData[] raceFactors;
    private TerrainFactorData[] terrainFactors;
    private double[] furyFactors;
    private final Map<String, BSaSkill> bsaSkills = new HashMap<>();
    private final Map<Integer, Map<String, JoinSkill>> joinSkills = new HashMap<>();
    private final List<Map<Shot, Double>> combosRatioFactors = new ArrayList<>(15);

    @Inject
    private ScriptManager scriptManager;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private HeroProvider heroProvider;
    @Inject
    private SocietyProvider societyProvider;

    private Cache<Integer, VacantData> roleVacantDataCache;

    @Override
    protected void doInit() {
        loadRaceWarFactor();
        loadTerrainWarFactor();
        loadFuryWarFactor();
        loadCombosRatioFactor();
        loadBSaSkillConfig(); // 加载必杀配置
        loadJoinSkillConfig(); // 加载合体技配置

        // register mbean
        ManagedBean managedBean = MBeanUtils.findManagedBean(getClass());
        MBeanUtils.registerComponent(this, managedBean);

        roleVacantDataCache
                = Caching.getCacheManager().getCache(CacheKeys.ROLE_VACANT_DATA_CACHE_KEY);
    }

    @Override
    protected void doReload() {
        // doInit();
    }

    @Override
    protected void doDestroy() {
        if (oname != null) {
            MBeanUtils.REGISTRY.unregisterComponent(oname);
        }
    }

    /**
     *
     * @param packet
     */
    public void searchSuccor(Packet packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        MultipleValue result = MultipleValue.createResult(packet);

        List<Friend> friends = normal.getFriends();
        Collections.shuffle(friends);

        int fc = Typhons.getInteger("typhon.spi.war.commendatoryFriendCount", 10);
        int fl = fc - 5;
        if (friends.size() < fl) {
            fl = friends.size();
        }

        int levelLimit = Typhons.getInteger("typhon.spi.war.commendatoryFriendLevelLimit", 5);
        int minLevelLimit = normal.getLevel() - levelLimit;
        int maxLevelLimit = normal.getLevel() + levelLimit;

        for (Friend friend : friends) {
            if (result.getVals().size() >= fl) {
                break;
            }
            if (chectSuccor(normal, friend.getRid())) {
                continue;
            }
            if (reset(friend.getRid(), minLevelLimit, maxLevelLimit)) {
                result.addVal(newPacketSuccor(friend.getRid(), null));
            }
        }

        Iterator<Cache.Entry<Integer, VacantData>> it = roleVacantDataCache.iterator();
        Cache.Entry<Integer, VacantData> entry;
        while (it.hasNext()) {
            if (result.getVals().size() >= fc) {
                break;
            }

            entry = it.next();
            VacantData vacantData = entry.getValue();
            if (vacantData == null || chectSuccor(normal, vacantData.getRid())) {
                continue;
            }

            int level
                    = vacantData.findHeroProperty(
                            vacantData.getFightGroup(vacantData.getLastFidx())[vacantData
                            .getCaptain()]).getLevel();
            if (vacantData.getRid() == player.getRole().getRid()
                    || level < minLevelLimit || level > maxLevelLimit
                    || normal.findFriend(vacantData.getRid()) != null) {
                continue;
            }

            result.addVal(newPacketSuccor(vacantData.getRid(), vacantData));
        }

        HeroItem primaryHero
                = normal.getFightGroup(normal.getLastFidx()).getHeroItem(FightGroup.PRIMARY_POS);
        // 系统默认生成
        for (int i = result.getVals().size(); i < fc; i++) {
            PacketSuccor succor = new PacketSuccor();
            succor.setName(randomName((List) result.getVals()));
            succor.setIid(randomSuccorHeroId());
            succor.setLevel(normal.getLevel());
            succor.setStar(primaryHero.getStar().name());
            succor.setPowerGuess((int) (primaryHero.getPowerGuess() * (1 + HERO_ID_RANDOM
                    .nextInt(10) / 10)));
            succor.setPlayerLevel(normal.getLevel());
            succor.setLadder(primaryHero.getLadder());
            result.addVal(succor);
        }

        SessionContext.getSession().write(result);
    }

    private boolean chectSuccor(Normal normal, int rid) {
        boolean bool = false;
        int CD = Typhons.getInteger("typhon.spi.Warprovider.CD") * 60 * 1000;
        for (SuccorObject object : normal.getSuccors()) {
            if (object.getRid() == rid && (System.currentTimeMillis() - object.getTime()) < CD) {
                bool = true;
            }
        }
        return bool;
    }

    private boolean reset(int rid, int minLevelLimit, int maxLevelLimit) {
        Session otherSession = sessionManager.getSession(rid);
        boolean bool = false;
        int level;
        if (otherSession != null) {
            Player player = SessionUtils.getPlayer(otherSession);
            Normal benormal = player.getNormal();
            FightGroup fightGroup = benormal.getFightGroup(benormal.getLastFidx());
            level = fightGroup.getHeroItems()[fightGroup.getCaptain()].getLevel();
        } else {
            VacantData vacantData = roleProvider.loadVacantData(rid);
            level
                    = vacantData.findHeroProperty(
                            vacantData.getFightGroup(vacantData.getLastFidx())[vacantData
                            .getCaptain()]).getLevel();
        }
        if (level >= minLevelLimit && level <= maxLevelLimit) {
            bool = true;
        }
        return bool;
    }

    /**
     * 加载好友数据.
     *
     * @param packet 协议包
     */
    public void loadSuccorData(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        HeroItem primaryHero
                = normal.getFightGroup(normal.getLastFidx()).getHeroItems()[FightGroup.PRIMARY_POS];

        int rid = (int) packet.getVal();
        JSONObject json;
        if (rid <= 0) {
            json = buildSuccorData(primaryHero);
        } else {
            IHeroEntity heroEntity;
            Session beSession = sessionManager.getSession(rid);

            if (beSession != null) {
                Normal beNormal = SessionUtils.getPlayer(beSession).getNormal();
                FightGroup fightGroup = beNormal.getFightGroup(beNormal.getLastFidx());
                heroEntity = fightGroup.getHeroItem(fightGroup.getCaptain());
            } else {
                VacantData vacantData = roleProvider.loadVacantData(rid);
                heroEntity = vacantData.getPrimaryHero();
            }

            if (primaryHero.getExtraAtk() > heroEntity.getExtraAtk()) {
                json = buildSuccorData(primaryHero);
            } else {
                json = new JSONObject();
                
                int tong = heroEntity.getExtraTong();
                int wu = heroEntity.getExtraWu();
                int zhi = heroEntity.getExtraZhi();
                
                int atk = heroEntity.getExtraAtk();
                int def = heroEntity.getExtraDef();
                int matk = heroEntity.getExtraMatk();
                int mdef = heroEntity.getExtraMdef();
                int hp = heroEntity.getExtraHp();
                
                int critRate = heroEntity.getExtraCritRate();
                int decritRate = heroEntity.getExtraDecritRate();
                int critMagn = heroEntity.getExtraCritMagn();
                
                int parryRate = heroEntity.getExtraParryRate();
                int deparryRate = heroEntity.getExtraDeparryRate();
                int parryValue = heroEntity.getExtraParryValue();
                
                if (heroEntity.getRabbets() != null) {
                    for (Rabbet rabbet : heroEntity.getRabbets()) {
                        tong += rabbet.getTong();
                        wu += rabbet.getWu();
                        zhi += rabbet.getZhi();

                        atk += rabbet.getAtk();
                        def += rabbet.getDef();
                        matk += rabbet.getMatk();
                        mdef += rabbet.getMdef();
                        hp += rabbet.getHp();

                        critRate += rabbet.getCritRate();
                        decritRate += rabbet.getDecritRate();
                        critMagn += rabbet.getCritMagn();

                        parryRate += rabbet.getParryRate();
                        deparryRate += rabbet.getDeparryRate();
                        parryValue += rabbet.getParryValue();
                    }
                }
                
                json.put("extraTong", tong);
                json.put("extraWu", wu);
                json.put("extraZhi", zhi);

                json.put("extraAtk", atk);
                json.put("extraDef", def);
                json.put("extraMatk", matk);
                json.put("extraMdef", mdef);
                json.put("extraHp", hp);
                
                json.put("extraCritRate", critRate);
                json.put("extraDecritRate", decritRate);
                json.put("extraCritMagn", critMagn);
                json.put("extraParryRate", parryRate);
                json.put("extraDeparryRate", deparryRate);
                json.put("extraParryValue", parryValue);
            }
            json.put("star", heroEntity.getStar());
        }

        json.put("id", packet.getId());
        json.put("type", Packet.Type.rs);
        player.getSession().write(packet.getNs(), json);
    }

    /**
     *
     * @param lab
     * @param troop
     * @param hero
     * @return
     */
    public FightObject newFightObject(int lab, Troop troop, IHeroEntity hero) {
        HeroItemDobj heroItemDobj = itemProvider.getItem(hero.getId());

        FightObject fo
                = new FightObject(lab, hero.getLevel(), hero.getLadder(), hero.getStar(),
                        heroItemDobj);

        fo.setBaseAtk(getAtk(troop, hero));
        fo.setMaxDef(getDef(troop, hero));

        fo.setBaseMatk(getMatk(troop, hero));
        fo.setMaxMdef(getMdef(troop, hero));
        fo.setMaxHp((int) (getHp(troop, hero) * Typhons.getDouble("typhon.spi.pvp.hpMagn", 1.5)));

        fo.setMaxCritRate(getCritRate(troop, hero));
        fo.setMaxDecritRate(getDecritRate(troop, hero));
        fo.setMaxCritMagn(getCritMagn(troop, hero));

        fo.setMaxParryRate(getParryRate(troop, hero));
        fo.setMaxDeparryRate(getDeparryRate(troop, hero));
        fo.setMaxParryValue(getParryValue(troop, hero));

        fo.setShots(getHeroItemDobjShots(heroItemDobj, hero.getLadder()));
        return fo;
    }

    /**
     *
     * @param heroItemDobj
     * @param ladder
     * @return
     */
    public Shot[] getHeroItemDobjShots(HeroItemDobj heroItemDobj, int ladder) {
        if (ladder < 2) {
            return heroItemDobj.getShots1();
        } else if (ladder < 4) {
            return heroItemDobj.getShots2();
        } else if (ladder < 7) {
            return heroItemDobj.getShots3();
        } else if (ladder < 11) {
            return heroItemDobj.getShots4();
        } else {
            return heroItemDobj.getShots5();
        }
    }

    // ===========================Begin 获取援军信息==================================================
    public FightObject loadSuccorFightObject(int rid, int fgidx, FightObject primaryFobj) {
        if (rid <= 0) { // 机器人
            int atk = (int) (primaryFobj.getAtk() * 1.1);
            int def = (int) (primaryFobj.getDef() * 1.1);
            int matk = (int) (primaryFobj.getMatk() * 1.1);
            int mdef = (int) (primaryFobj.getMdef() * 1.1);
            int hp = (int) (primaryFobj.getHp() * 1.1);
            int critRate = (int) (primaryFobj.getCritRate() * 1.1);
            int decritRate = (int) (primaryFobj.getDecritRate() * 1.1);
            int critMagn = (int) (primaryFobj.getCritMagn() * 1.1);
            int parryRate = (int) (primaryFobj.getParryRate() * 1.1);
            int deparryRate = (int) (primaryFobj.getDeparryRate() * 1.1);
            int parryValue = (int) (primaryFobj.getParryValue() * 1.1);

            FightObject fo
                    = new FightObject(9, primaryFobj.getLevel(), primaryFobj.getLadder(),
                            primaryFobj.getStar(),
                            (HeroItemDobj) itemProvider.getItem(randomSuccorHeroId()));
            fo.setBaseAtk(atk);
            fo.setMaxDef(def);

            fo.setBaseMatk(matk);
            fo.setMaxMdef(mdef);
            fo.setMaxHp(hp);

            fo.setMaxCritRate(critRate);
            fo.setMaxDecritRate(decritRate);
            fo.setMaxCritMagn(critMagn);

            fo.setMaxParryRate(parryRate);
            fo.setMaxDeparryRate(deparryRate);
            fo.setMaxParryValue(parryValue);
            return fo;
        } else {
            IHeroEntity heroEntity;
            ITroop itroop;
            HeroItemDobj heroItemDobj;

            Session session = sessionManager.getSession(rid);
            if (session != null) {
                Normal normal = SessionUtils.getPlayer(session).getNormal();
                FightGroup fightGroup = normal.getFightGroup(fgidx);
                heroEntity
                        = (HeroItem) normal.player().getHeroBag().findNode(fightGroup.getSuccor())
                        .getItem();
                itroop = normal;
            } else {
                VacantData vacantData = roleProvider.loadVacantData(rid);
                heroEntity = vacantData.findHeroProperty(vacantData.getPvpSuccorIid());
                itroop = vacantData;
            }

            heroItemDobj = (HeroItemDobj) itemProvider.getItem(heroEntity.getId());
            FightObject fo
                    = new FightObject(9, heroEntity.getLevel(), heroEntity.getLadder(),
                            heroEntity.getStar(), heroItemDobj);
            int atk = 0;
            int def = 0;
            int matk = 0;
            int mdef = 0;
            int hp = 0;
            int critRate = 0;
            int decritRate = 0;
            int critMagn = 0;
            int parryRate = 0;
            int deparryRate = 0;
            int parryValue = 0;

            for (int i = 0; i < ITroop.MAX_TROOP_SIZE; i++) {
                Troop troop = itroop.getTroop(ITroop.Type.valueOf(i));
                atk += troop.getAtk() + troop.getAcePack().getAtk();
                def += troop.getDef() + troop.getAcePack().getDef();
                matk += troop.getMatk() + troop.getAcePack().getMatk();
                mdef += troop.getMdef() + troop.getAcePack().getMdef();
                hp += troop.getHp() + troop.getAcePack().getHp();
                critRate += troop.getCritRate() + troop.getAcePack().getCritRate();
                decritRate += troop.getDecritRate() + troop.getAcePack().getDecritRate();
                critMagn += troop.getCritMagn() + troop.getAcePack().getCritMagn();
                parryRate += troop.getParryRate() + troop.getAcePack().getParryRate();
                deparryRate += troop.getDeparryRate() + troop.getAcePack().getDeparryRate();
                parryValue += troop.getParryValue() + troop.getAcePack().getParryValue();
            }

            fo.setBaseAtk(heroProvider.getAtk(heroEntity) + atk / ITroop.MAX_TROOP_SIZE);
            fo.setMaxDef(heroProvider.getDef(heroEntity) + def / ITroop.MAX_TROOP_SIZE);

            fo.setBaseMatk(heroProvider.getMatk(heroEntity) + matk / ITroop.MAX_TROOP_SIZE);
            fo.setMaxMdef(heroProvider.getMdef(heroEntity) + mdef / ITroop.MAX_TROOP_SIZE);
            fo.setMaxHp(heroProvider.getHp(heroEntity) + hp / ITroop.MAX_TROOP_SIZE);

            fo.setMaxCritRate(heroProvider.getCritRate(heroEntity) + critRate
                    / ITroop.MAX_TROOP_SIZE / 500);
            fo.setMaxDecritRate(heroProvider.getDecritRate(heroEntity) + decritRate
                    / ITroop.MAX_TROOP_SIZE / 500);
            fo.setMaxCritMagn(heroProvider.getCritMagn(heroEntity) + critMagn
                    / ITroop.MAX_TROOP_SIZE / 100);

            fo.setMaxParryRate(heroProvider.getParryRate(heroEntity) + parryRate
                    / ITroop.MAX_TROOP_SIZE / 500);
            fo.setMaxDeparryRate(heroProvider.getDeparryRate(heroEntity) + deparryRate
                    / ITroop.MAX_TROOP_SIZE / 600);
            fo.setMaxParryValue(heroProvider.getParryValue(heroEntity) + parryValue
                    / ITroop.MAX_TROOP_SIZE);

            return fo;
        }
    }

    // ===========================End 获取援军信息====================================================
    //=============================== Begin ========================================================
    public int getAtk(Troop troop, IHeroEntity heroEntity) {
        int atk = heroProvider.getAtk(heroEntity);

        if (troop != null) {
            atk += troop.getAtk() + troop.getAcePack().getAtk();
        }
        return (int) atk;
    }

    public int getDef(Troop troop, IHeroEntity heroEntity) {
        int def = heroProvider.getDef(heroEntity);

        if (troop != null) {
            def += troop.getDef() + troop.getAcePack().getDef();
        }
        return (int) def;
    }

    public int getMatk(Troop troop, IHeroEntity heroEntity) {
        int matk = heroProvider.getMatk(heroEntity);

        if (troop != null) {
            matk += troop.getMatk() + troop.getAcePack().getMatk();
        }
        return (int) matk;
    }

    public int getMdef(Troop troop, IHeroEntity heroEntity) {
        int mdef = heroProvider.getMdef(heroEntity);

        if (troop != null) {
            mdef += troop.getMdef() + troop.getAcePack().getMdef();
        }
        return (int) mdef;
    }

    public int getHp(Troop troop, IHeroEntity heroEntity) {
        int hp = heroProvider.getHp(heroEntity);

        if (troop != null) {
            hp += troop.getHp() + troop.getAcePack().getHp();
        }
        return hp;
    }

    public double getCritRate(Troop troop, IHeroEntity heroEntity) {
        double critRate = heroProvider.getCritRate(heroEntity);

        if (troop != null) {
            critRate += troop.getCritRate() + troop.getAcePack().getCritRate() / 500;
        }
        return critRate;
    }

    public double getDecritRate(Troop troop, IHeroEntity heroEntity) {
        double decritRate = heroProvider.getDecritRate(heroEntity);

        if (troop != null) {
            decritRate += troop.getDecritRate() + troop.getAcePack().getDecritRate() / 500;
        }
        return decritRate;
    }

    public double getCritMagn(Troop troop, IHeroEntity heroEntity) {
        double critMagn = heroProvider.getCritMagn(heroEntity);

        if (troop != null) {
            critMagn += troop.getCritMagn() + troop.getAcePack().getCritMagn() / 100;
        }
        return critMagn;
    }

    public double getParryRate(Troop troop, IHeroEntity heroEntity) {
        double parryRate = heroProvider.getParryRate(heroEntity);

        if (troop != null) {
            parryRate += troop.getParryRate() + troop.getAcePack().getParryRate() / 500;
        }
        return parryRate;
    }

    public double getDeparryRate(Troop troop, IHeroEntity heroEntity) {
        double deparryRate = heroProvider.getDeparryRate(heroEntity);

        if (troop != null) {
            deparryRate += troop.getDeparryRate() + troop.getAcePack().getDeparryRate() / 600;
        }
        return deparryRate;
    }

    public double getParryValue(Troop troop, IHeroEntity heroEntity) {
        double parryValue = heroProvider.getParryValue(heroEntity);

        if (troop != null) {
            parryValue += troop.getParryValue();
            parryValue *= (1 + troop.getAcePack().getParryValue() / 100);
        }
        return parryValue;
    }

    //=============================== End ==========================================================
    /**
     * 获得怒气所能够增加的伤害.
     *
     * @param c 怒气值
     * @return 怒气伤害系数
     */
    public double getFuryFactor(int c) {
        double r = 1;
        if (c >= 0 && c <= 10) {
            r = furyFactors[c];
        }
        return r;
    }

    /**
     * 获得兵种相克因数.
     *
     * @param a 攻击方
     * @param b 防御方
     * @return 相克因数
     */
    public double getRaceFactor(Race a, Race b) {
        for (RaceFactorData data : raceFactors) {
            if (a == data.Name) {
                switch (b) {
                    case Ce:
                        return data.Ce;
                    case Bu:
                        return data.Bu;
                    case Qi:
                        return data.Qi;
                    case Gong:
                        return data.Gong;
                    case Che:
                        return data.Che;
                }
            }
        }
        return 1;
    }

    /**
     * 获取兵种与地形的相克因数.
     *
     * @param r 兵种
     * @param t 地形
     * @return 因数
     */
    public double getTerrainFactor(Race r, Terrain t) {
        for (TerrainFactorData data : terrainFactors) {
            if (r == data.Name) {
                switch (t) {
                    case SLu:
                        return data.SLu;
                    case SLing:
                        return data.SLing;
                    case PYuan:
                        return data.PYuan;
                    case SDi:
                        return data.SDi;
                    case CGuan:
                        return data.CGuan;
                }
            }
        }
        return 1;
    }

    /**
     * 战斗之前的装备工作.
     *
     * @param warInfo
     */
    public void prepare(WarInfo warInfo) {
        // 释放被动技能
        desorbLedaerSkill(warInfo, warInfo.getAttackerEntity());
        desorbLedaerSkill(warInfo, warInfo.getDefenderEntity());
    }

    /**
     *
     * @param warInfo
     * @return
     */
    public WarReport finalAttack(WarInfo warInfo) {
        prepare(warInfo);

        WarReport warReport = new WarReport();
        warReport.setAttackerEntity(newWarReportEntity(warInfo.getAttackerEntity()));
        warReport.setDefenderEntity(newWarReportEntity(warInfo.getDefenderEntity()));

        // CACHE
        warInfo.setWarReport(warReport);

        WarReport.Round round;
        WarReport.Effect effect = WarReport.Effect.C;

        for (;;) {
            round = new WarReport.Round();
            if (warInfo.getNextDire() == Direction.S) {
                warInfo.setNextDire(Direction.N);
                effect
                        = attackX(warInfo, Direction.S, round, round.getSdetails(),
                                round.getSbufDetails());
            }

            if (effect == WarReport.Effect.C && warInfo.getNextDire() == Direction.N) {
                warInfo.setNextDire(Direction.S);
                effect
                        = attackX(warInfo, Direction.N, round, round.getNdetails(),
                                round.getNbufDetails());

                if (effect != WarReport.Effect.C) {
                    effect
                            = (effect == WarReport.Effect.W ? WarReport.Effect.D : WarReport.Effect.W);
                }
            }

            // FIXME 测试数据
            round.setSjson(JSON.toJSON(warInfo.getAttackerEntity()));
            round.setNjson(JSON.toJSON(warInfo.getDefenderEntity()));

            warReport.addRound(round);

            // 如果10回合未结束战斗, 算攻击方输
            if (warInfo.getRound() >= Typhons.getInteger("typhon.spi.pvp.allRounds", 10)) {
                effect = WarReport.Effect.D;
            }

            warInfo.setRound(warInfo.getRound() + 1);
            // 战斗结束
            if (effect != WarReport.Effect.C) {
                warReport.setEffect(effect);
                break;
            }
        }

        return warReport;
    }

    public void attack(Shot shot, WarInfo warInfo, FightObject aobj, WarInfo.Entity attackerEntity,
            WarInfo.Entity defenderEntity, Direction dire, List<Object> outputs) {

        if (aobj.getStatus() == FightObject.Status.CONFUSION) {
            List<FightObject> goals = new ArrayList<>();
            goals.add(defenderEntity.findFightGoal(true));

            outputs.add(_GJi(warInfo, aobj, goals));
            return;
        }

        switch (shot) {
            case GJi: {
                int num = 1;
                if (aobj.getRace() == Race.Gong) {
                    num = 2;
                }
                outputs.add(_GJi(warInfo, aobj, defenderEntity.findFightGoals(num)));
                break;
            }
            case BSa: {
                Script script = scriptManager.getScript("war.bs." + aobj.getBsaSkill());
                MultiAttackResult mar
                        = (MultiAttackResult) script.invoke(null, new BSaWapper(attackerEntity,
                                        defenderEntity, aobj, bsaSkills.get(aobj.getBsaSkill()), warInfo));
                if (mar != null) {
                    AttackEntry sae = new AttackEntry();
                    sae.setLab(aobj.getLab());
                    mar.setSource(sae);
                    mar.setShot(shot);
                    outputs.add(mar);
                }
                break;
            }
            case FYu: {
                outputs.add(fyuAttack(warInfo, attackerEntity.getDire(), aobj));
                break;
            }
            case JCe: {
                int num = 1;
                if (aobj.getRace() == Race.Gong) {
                    num = 2;
                }
                outputs.add(_JCe(warInfo, aobj, defenderEntity.findFightGoals(num)));
                break;
            }
            case QXi: {
                FightObject goal = defenderEntity.findFightGoal();
                outputs.add(_QXi(warInfo, aobj, goal));
                break;
            }
            case Q7: {
                if (aobj.getFury() >= aobj.getMaxFury()) {
                    Script script = scriptManager.getScript("war.bs." + aobj.getBsaSkill());
                    MultiAttackResult mar
                            = (MultiAttackResult) script.invoke(null, new BSaWapper(attackerEntity,
                                            defenderEntity, aobj, bsaSkills.get(aobj.getBsaSkill()), warInfo));
                    if (mar != null) {
                        AttackEntry sae = new AttackEntry();
                        sae.setLab(aobj.getLab());
                        mar.setSource(sae);
                        mar.setShot(Shot.Q7);
                        outputs.add(mar);
                    }
                } else {
                    aobj.incrementFury(1);
                    aobj.removeAllDebuff();

                    // result
                    AttackEntry source = new AttackEntry();
                    source.setLab(aobj.getLab());

                    AttackResult ar = new AttackResult();
                    ar.setShot(shot);
                    ar.setSource(source);

                    outputs.add(ar);
                }
                break;
            }
            case YHu: {
                // 输出参数
                AttackEntry source = new AttackEntry();
                source.setLab(aobj.getLab());

                int num = 1;
                if (attackerEntity.getSuccor().getRace() == Race.Gong) {
                    num = 2;
                }
                AttackResult ar
                        = _YHu(warInfo, attackerEntity.getSuccor(),
                                defenderEntity.findFightGoals(num));
                ar.setSource(source);
                outputs.add(ar);
                break;
            }
        }
    }

    /**
     *
     * @param warInfo
     * @param dire
     * @param aobj
     * @return
     */
    public Object fyuAttack(WarInfo warInfo, Direction dire, FightObject aobj) {
        Script script = scriptManager.getScript("war.BufferSkillScriptFactory");
        BufferSkill bufferSkill
                = (BufferSkill) script.invoke(null, new Object[]{warInfo, dire, aobj, Shot.FYu});
        return bufferSkill.onBefore();
    }

    /**
     *
     * @param warInfo
     * @param dire
     * @param combo
     * @param bobjs
     * @param j
     * @param joinSkill
     * @return
     */
    public ComboResult skillComboAoeAttack(WarInfo warInfo, Direction dire, WarCombo combo,
            List<FightObject> bobjs, double j, JoinSkill joinSkill) {
        double factor = getAttackFactor(); // 攻击系数
        double x = calculateSkillComboX(warInfo, combo, false);
        double f1;
        double s;
        double r;

        double dehp;

        double _cr = 0;
        double _cm = 0;
        for (FightObject fo : combo.getFightObjects()) {
            _cr += fo.getCritRate();
            _cm += fo.getCritMagn();
        }
        _cr /= combo.getFightObjectSize();
        _cm /= combo.getFightObjectSize();

        ComboResult cr = new ComboResult();
        cr.setShot(combo.getShot());
        cr.setCount(combo.getComboCount());

        double e_m = 1;
        // 4,5人必杀combo不乘以1.5的系统
        if (!"none".equals(joinSkill.getJointArea()) && joinSkill.getAttackerCntNeeded() > 3) {
            e_m = 1.5;
        }

        for (FightObject bobj : bobjs) {
            f1 = getFuryFactor(bobj.getFury()); // 守方怒气系数
            s = 1; // 兵种相克系数
            r = 1; // 暴击倍率

            dehp = Math.max((factor * (x - (bobj.getDef() + bobj.getMdef()) / 2 * f1) * s * r * j), x / 10);
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} = <{} * ({} - {} * {}) * {} * {} * {}> -> <_cr(暴击率): {}, _pr(暴伤): {}, r: {}>",
                        dehp, factor, x, bobj.getDef(), f1, s, r, _cr, _cm, r);
            }

            if (!joinSkill.getJointArea().equals(bobj.getArea().name())) {
                dehp *= e_m;
            }

            int hp = (int) dehp;
            bobj.decrementHp(hp);

            AttackEntry ae = new AttackEntry();
            ae.setLab(bobj.getLab());
            ae.setVal(hp);
            cr.addTarget(ae);
        }
        return cr;
    }

    /**
     *
     * @param fightObjects
     * @param holdPoints
     * @return
     */
    public Collection<WarCombo> calculateCombo(List<FightObject> fightObjects, int[] holdPoints) {
        Shot[][] table = toShotTable(fightObjects, holdPoints);
        Map<Shot, WarCombo> combos = new HashMap<>();

        Shot shot, temp;
        int x1, y1, x2, y2, x3, y3;
        for (int i = 0; i < table.length; i++) {
            Shot[] shots = table[i];
            for (int j = 0; j < 3; j++) { // 第一条线
                shot = shots[j];
                x1 = i;
                y1 = j;

                if (i == 0) {
                    x2 = x1 + 1;
                    y2 = y1 + 1;

                    x3 = x2 + 1;
                    y3 = y2 + 1;
                    temp = isCombo(shot, table[x2][y2], table[x3][y3]);
                    if (temp != null) {
                        plusComboPoint(combos, temp, x1, y1, fightObjects.get(y1));
                        plusComboPoint(combos, temp, x2, y2, fightObjects.get(y2));
                        plusComboPoint(combos, temp, x3, y3, fightObjects.get(y3));
                    }
                } else if (i == 2) { // 第三条线
                    x2 = x1 - 1;
                    y2 = y1 + 1;

                    x3 = x2 - 1;
                    y3 = y2 + 1;
                    temp = isCombo(shot, table[x2][y2], table[x3][y3]);
                    if (temp != null) {
                        plusComboPoint(combos, temp, x1, y1, fightObjects.get(y1));
                        plusComboPoint(combos, temp, x2, y2, fightObjects.get(y2));
                        plusComboPoint(combos, temp, x3, y3, fightObjects.get(y3));
                    }
                }

                // 计算直线
                x3 = x2 = x1;
                y2 = y1 + 1;
                y3 = y2 + 1;

                temp = isCombo(shot, table[x2][y2], table[x3][y3]);
                if (temp != null) {
                    plusComboPoint(combos, temp, x1, y1, fightObjects.get(y1));
                    plusComboPoint(combos, temp, x2, y2, fightObjects.get(y2));
                    plusComboPoint(combos, temp, x3, y3, fightObjects.get(y3));

                    if (temp == Shot.Q7) {
                        if (j == 0) {
                            temp = table[i][3];
                            if (temp != Shot.Miss && temp != Shot.Q7) {
                                plusComboPoint(combos, temp, x1, y1, fightObjects.get(y1));
                                plusComboPoint(combos, temp, x2, y2, fightObjects.get(y2));
                                plusComboPoint(combos, temp, x3, y3, fightObjects.get(y3));
                            } else if (temp == Shot.Q7) {
                                temp = table[i][4];
                                if (temp != Shot.Miss && temp != Shot.Q7) {
                                    // 1combo
                                    plusComboPoint(combos, temp, x1, y1, fightObjects.get(y1));
                                    plusComboPoint(combos, temp, x2, y2, fightObjects.get(y2));
                                    plusComboPoint(combos, temp, x3, y3, fightObjects.get(y3));
                                }
                            }
                        } else if (j == 1) {
                            // First
                            temp = table[i][0];
                            if (temp != Shot.Miss && temp != Shot.Q7) {
                                plusComboPoint(combos, temp, i, j, fightObjects.get(j));
                                plusComboPoint(combos, temp, i, j + 1, fightObjects.get(j + 1));
                                plusComboPoint(combos, temp, i, j + 2, fightObjects.get(j + 2));
                            }

                            // Last
                            temp = table[i][4];
                            if (temp != Shot.Miss && temp != Shot.Q7) {
                                plusComboPoint(combos, temp, i, j + 1, fightObjects.get(y1));
                                plusComboPoint(combos, temp, i, j + 2, fightObjects.get(y2));
                                plusComboPoint(combos, temp, i, j + 3, fightObjects.get(y3));
                            }
                            //====================================================//
                        } else if (j == 2 && table[i][1] != Shot.Miss) {
                            temp = table[i][1];
                            if (temp == Shot.Q7) {
                                temp = table[i][0];
                            }

                            if (temp != Shot.Miss && temp != Shot.Q7) {
                                plusComboPoint(combos, temp, i, 2, fightObjects.get(2));
                                plusComboPoint(combos, temp, i, 3, fightObjects.get(3));
                                plusComboPoint(combos, temp, i, 4, fightObjects.get(4));
                            }
                        }
                    }
                } // 如果为7的连锁

            }
        }

        if (LOG.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            for (WarCombo combo : combos.values()) {
                sb.append("\n").append(StringUtils.leftPad(combo.getShot().name(), 4));
                sb.append("(")
                        .append(StringUtils.leftPad(String.valueOf(combo.getComboCount()), 2))
                        .append(")");
                sb.append(": {");
                for (Point point : combo.getPoints()) {
                    sb.append(StringUtils.leftPad(table[point.getX()][point.getY()].name(), 4));
                    sb.append("[").append(point.getX()).append(", ").append(point.getY())
                            .append("] ");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("}");
            }
            LOG.debug(sb.toString());
        }

        // 按照攻击要求排序
        List<WarCombo> warCombos = new ArrayList<>(combos.values());
        Collections.sort(warCombos);
        return warCombos;
    }

    /**
     *
     * @param s1
     * @param s2
     * @param s3
     * @return
     */
    protected Shot isCombo(IFightItem.Shot s1, IFightItem.Shot s2, IFightItem.Shot s3) {
        IFightItem.Shot rs = s1.equals(s2);
        if (rs != null) {
            return rs.equals(s3);
        }
        return null;
    }

    /**
     *
     * @param fo
     * @param p
     * @return
     */
    protected Shot getPrevShot(FightObject fo, int p) {
        return getShot(fo, p - 1);
    }

    /**
     *
     * @param fo
     * @param p
     * @return
     */
    protected Shot getNextShot(FightObject fo, int p) {
        return getShot(fo, p + 1);
    }

    /**
     *
     * @param fo
     * @param p
     * @return
     */
    protected Shot getShot(FightObject fo, int p) {
        Shot[] shots = fo.getShots();
        if (p < 0) {
            return shots[shots.length + p];
        } else if (p >= shots.length) {
            return shots[p - shots.length];
        }
        return shots[p];
    }

    /**
     *
     * @param critRate
     * @param decritRate
     * @param critMagn
     * @return
     */
    public double getCritMagn(double critRate, double decritRate, double critMagn) {
        if (getCritRate() <= critRate - decritRate) {
            return critMagn;
        }
        return 1D;
    }

    /**
     *
     * @param afo
     * @param bfo
     * @return
     */
    protected double getCritMagn(FightObject afo, FightObject bfo) {
        // 减去对方的韧性
        if (getCritRate() <= afo.getCritRate() - bfo.getDecritRate()) {
            return afo.getCritMagn();
        }
        return 1F;
    }

    /**
     *
     * @param parryRate
     * @param deparryRate
     * @param parryValue
     * @return
     */
    public double getParryValue(double parryRate, double deparryRate, double parryValue) {
        if (getCritRate() <= parryRate - deparryRate) {
            return parryValue;
        }
        return 0D;
    }

    /**
     *
     * @param afo
     * @param bfo
     * @return
     */
    protected double getParryValue(FightObject afo, FightObject bfo) {
        // 减去对方的抗性
        if (getCritRate() <= afo.getParryRate() - bfo.getDeparryRate()) {
            return afo.getParryValue();
        }
        return 0F;
    }

    /**
     *
     * @param terrain
     * @param afo
     * @param bfo
     * @param atk
     * @param def
     * @param j
     * @return
     */
    public int attack1(Terrain terrain, FightObject afo, FightObject bfo, int atk, int def, double j) {
        double factor = getAttackFactor(); // 攻击系数
        double p1 = getTerrainFactor(afo.getRace(), terrain); // 地形相克系数
        double f = getFuryFactor(afo.getFury()); // 攻方怒气系数
        double f1 = getFuryFactor(bfo.getFury()); // 守方怒气系数
        double r = getCritMagn(afo, bfo); // 暴击倍率
        double g1 = getParryValue(bfo, afo); // 格挡伤害

        double dehp
                = factor * (atk * p1 * f - def * f1) * r * j
                * Typhons.getDouble("typhon.spi.pvp.damageMagn", 1) - g1;
        int a = (int) (Math.max(dehp, atk / 10) - g1);
        int hp = Math.max(a, 1);
        if (LOG.isDebugEnabled()) {
            LOG.debug("a:{}->b:{} - {}={} * ({} * {} * {} - {} * {}) * {} -> {}", afo.getHeroId(),
                    bfo.getHeroId(), hp, factor, atk, p1, f, def, f1, r, j, g1);
        }
        return hp;
    }

    /**
     * 对防守方造成攻击伤害,并返回详细信息.
     *
     * @param terrain 地形
     * @param afo 攻击方
     * @param bfo 防守方
     * @param atk 攻击力
     * @param def 防御力
     * @param j 额外系数
     * @return 造成伤害的详细信息
     */
    public AttackEntry attack0(Terrain terrain, FightObject afo, FightObject bfo, int atk, int def,
            double j) {
        double p1 = getTerrainFactor(afo.getRace(), terrain); // 地形相克系数
        double f = getFuryFactor(afo.getFury()); // 攻方怒气系数
        double f1 = getFuryFactor(bfo.getFury()); // 守方怒气系数
        double r = getCritMagn(afo, bfo); // 暴击倍率
        double g1 = getParryValue(bfo, afo); // 格挡伤害

        AttackEntry ae = attack0(atk, def, p1, f, f1, r, j, g1);
        ae.setLab(bfo.getLab());
        return ae;
    }

    /**
     * 计算攻击造成伤害. 返回攻击血量, 暴击, 格挡信息.
     *
     * @param atk 攻击力
     * @param def 防御力
     * @param p1 地形相克系数
     * @param f 攻方怒气系数
     * @param f1 守方怒气系数
     * @param r 暴击倍率
     * @param j 攻击额外系数
     * @param g1 格挡伤害
     * @return 返回攻击血量, 暴击, 格挡信息
     */
    public AttackEntry attack0(int atk, int def, double p1, double f, double f1, double r,
            double j, double g1) {
        double factor = getAttackFactor(); // 攻击系数
        double dehp
                = factor * (atk * p1 * f - def * f1) * r * j
                * Typhons.getDouble("typhon.spi.pvp.damageMagn", 1);
        int a = (int) (Math.max(dehp, atk / 10) - g1);
        int hp = Math.max(a, 1);

        if (LOG.isDebugEnabled()) {
            LOG.debug("{}={} * ({} * {} * {} - {} * {}) * {} * {} -> {}", hp, factor, atk, p1, f,
                    def, f1, r, j, g1);
        }

        AttackEntry ae = new AttackEntry();
        ae.setVal(hp);
        ae.crited(r);
        ae.parried(g1);
        return ae;
    }

    private AttackResult _GJi(WarInfo warInfo, FightObject aobj, List<FightObject> goals) {
        // 输出参数
        AttackEntry source = new AttackEntry();
        source.setLab(aobj.getLab());

        AttackResult ar = new AttackResult();
        int atk = aobj.getAtk();
        if (aobj.getStatus() == FightObject.Status.CONFUSION) {
            atk = Math.max(aobj.getAtk(), aobj.getMatk());
        }

        for (FightObject goal : goals) {
            AttackEntry ae = attack0(warInfo.getTerrain(), aobj, goal, atk, goal.getDef(), 1);
            goal.decrementHp((int) ae.getVal());

            ae.setLab(goal.getLab());
            ar.setShot(Shot.GJi);
            ar.setSource(source);
            ar.addTarget(ae);
        }
        return ar;
    }

    private AttackResult _JCe(WarInfo warInfo, FightObject aobj, List<FightObject> goals) {
        // 输出参数
        AttackEntry source = new AttackEntry();
        source.setLab(aobj.getLab());

        AttackResult ar = new AttackResult();

        for (FightObject goal : goals) {
            AttackEntry ae
                    = attack0(warInfo.getTerrain(), aobj, goal, aobj.getMatk(), goal.getMdef(), 1);
            goal.decrementHp((int) ae.getVal());

            ae.setLab(goal.getLab());

            ar.setShot(Shot.JCe);
            ar.setSource(source);
            ar.addTarget(ae);
        }
        return ar;
    }

    private AttackResult _QXi(WarInfo warInfo, FightObject aobj, FightObject goal) {
        double j = 0.6;
        // 如果对方怒气为0则系数乘2
        if (goal.getFury() <= 0) {
            j *= 2;
        }

        int atk;
        int def;
        if (aobj.getAtk() > aobj.getMatk()) {
            atk = aobj.getAtk();
            def = goal.getDef();
        } else {
            atk = aobj.getMatk();
            def = goal.getMdef();
        }

        AttackEntry ae = attack0(warInfo.getTerrain(), aobj, goal, atk, def, j);
        goal.decrementHp((int) ae.getVal());
        goal.decrementFury(1);

        ae.setLab(goal.getLab());

        // 输出参数
        AttackEntry source = new AttackEntry();
        source.setLab(aobj.getLab());

        AttackResult ar = new AttackResult();
        ar.setShot(Shot.QXi);
        ar.setSource(source);
        ar.addTarget(ae);
        return ar;
    }

    private AttackResult _YHu(WarInfo warInfo, FightObject aobj, List<FightObject> goals) {
        AttackResult ar = new AttackResult();
        ar.setShot(Shot.YHu);

        int atk;
        int def;

        for (FightObject goal : goals) {
            if (aobj.getAtk() > aobj.getMatk()) {
                atk = aobj.getAtk();
                def = goal.getDef();
            } else {
                atk = aobj.getMatk();
                def = goal.getMdef();
            }

            AttackEntry ae = attack0(warInfo.getTerrain(), aobj, goal, atk, def, 1);
            goal.decrementHp((int) ae.getVal());

            ae.setLab(goal.getLab());
            ar.addTarget(ae);
        }
        return ar;
    }

    //    private 
    //======================== Begin ===============================================================
    private void desorbLedaerSkill(WarInfo warInfo, WarInfo.Entity entity) {
        FightObject fo = entity.getFightObject(entity.getCaptain());
        if (fo.getLadderSkill() != null) {
            Script script = scriptManager.getScript(("war.cs." + fo.getLadderSkill()).intern());

            // 释放自己的队长技能
            List<FightObject> list = new ArrayList<>(entity.getFightObjects());
            list.add(entity.getSuccor());
            script.invoke(null, list);

            // 释放好友的队长技能
            script
                    = scriptManager.getScript("war.cs."
                            + entity.getSuccor().getLadderSkill().intern());
            script.invoke(null, list);
        }
    }

    private WarReport.Effect attackX(WarInfo warInfo, Direction dire, WarReport.Round round,
            List<Object> details, List<Object> bufDetails) {
        int[] holdPoints = randomHoldPoints(); // 拉霸停止点
        switch (dire) {
            case N:
                round.setNholdPoints(holdPoints);
                break;
            case S:
                round.setSholdPoints(holdPoints);
                break;
        }

        return attack(holdPoints, warInfo, dire, details, bufDetails);
    }

    public WarReport.Effect attack(int[] holdPoints, WarInfo warInfo, Direction dire,
            List<Object> details, List<Object> bufDetails) {

        WarInfo.Entity attackerEntity;
        WarInfo.Entity defenderEntity;
        switch (dire) {
            case S:
                attackerEntity = warInfo.getAttackerEntity();
                defenderEntity = warInfo.getDefenderEntity();
                break;
            default:
                attackerEntity = warInfo.getDefenderEntity();
                defenderEntity = warInfo.getAttackerEntity();
        }

        // 增加出手次数
        attackerEntity.incrementAtkCount();

        // 执行BufferSkill
        Object bufferSkillRs;
        for (FightObject fo : attackerEntity.getFightObjects()) {
            if (!fo.isDead() && fo.getBufferSkills().size() > 0) {
                for (Object bs : fo.getBufferSkills().toArray()) {
                    bufferSkillRs = ((BufferSkill) bs).onAfter();
                    if (bufferSkillRs != null) {
                        bufDetails.add(bufferSkillRs);
                    }
                }
            }
        }

        // BUFFER
        if (attackerEntity.isOver()) {
            return WarReport.Effect.D;
        }

        Collection<WarCombo> combos;
        FightObject source;
        FightObject goal;

        for (int i = 0; i < holdPoints.length; i++) {
            source = attackerEntity.getFightObject(i);
            if (source.isDead() || source.getStatus() == FightObject.Status.SLEEPING) {
                continue;
            }

            attack(source.getShot(holdPoints[i]), warInfo, source, attackerEntity, defenderEntity,
                    dire, details);

            if (attackerEntity.isOver()) {
                return WarReport.Effect.D;
            }

            if (defenderEntity.isOver()) {
                return WarReport.Effect.W;
            }
        }

        combos = calculateCombo(attackerEntity.getFightObjects(), holdPoints);
        for (WarCombo warCombo : combos) {
            switch (warCombo.getShot()) {
                case GJi: {
                    double j
                            = combosRatioFactors.get(warCombo.getComboCount() - 1).get(
                                    warCombo.getShot());
                    goal = defenderEntity.findFightGoal();

                    AttackEntry ae = new AttackEntry();
                    ae.setLab(goal.getLab());

                    int sumHp = 0;
                    for (FightObject fobj : warCombo.getFightObjects()) {
                        sumHp
                                += attack1(warInfo.getTerrain(), fobj, goal, fobj.getAtk(),
                                        goal.getDef(), 1);
                    }

                    int hp = (int) (sumHp * j);
                    goal.decrementHp(hp);
                    ae.setVal(hp);

                    // result
                    ComboResult cr = new ComboResult();
                    cr.setCount(warCombo.getComboCount());
                    cr.setShot(Shot.GJi);
                    cr.addTarget(ae);
                    details.add(cr);
                    break;
                }
                case FYu: {
                    ComboResult cr = new ComboResult();
                    cr.setCount(warCombo.getComboCount());
                    cr.setShot(Shot.FYu);

                    double j
                            = combosRatioFactors.get(warCombo.getComboCount() - 1).get(
                                    warCombo.getShot());
                    int hp;
                    for (FightObject fo : attackerEntity.getFightObjects()) {
                        if (fo.isDead()) {
                            continue;
                        }

                        hp = (int) (fo.getMaxHp() * j);
                        fo.incrementHp(hp);

                        AttackEntry ae = new AttackEntry();
                        ae.setLab(fo.getLab());
                        ae.setVal(hp);
                        cr.addTarget(ae);
                    }

                    details.add(cr);
                    break;
                }
                case JCe: {
                    double j
                            = combosRatioFactors.get(warCombo.getComboCount() - 1).get(
                                    warCombo.getShot());
                    goal = defenderEntity.findFightGoal();

                    AttackEntry ae = new AttackEntry();
                    ae.setLab(goal.getLab());

                    int sumHp = 0;
                    for (FightObject fobj : warCombo.getFightObjects()) {
                        sumHp
                                += attack1(warInfo.getTerrain(), fobj, goal, fobj.getMatk(),
                                        goal.getMdef(), 1);
                    }

                    int hp = (int) (sumHp * j);
                    goal.decrementHp(hp);
                    ae.setVal(hp);

                    ComboResult cr = new ComboResult();
                    cr.setCount(warCombo.getComboCount());
                    cr.setShot(warCombo.getShot());
                    cr.addTarget(ae);
                    details.add(cr);
                    break;
                }
                case QXi: {
                    double j
                            = combosRatioFactors.get(warCombo.getComboCount() - 1).get(
                                    warCombo.getShot());
                    goal = defenderEntity.findFightGoal();

                    AttackEntry ae = new AttackEntry();
                    ae.setLab(goal.getLab());

                    int atk;
                    int def;
                    int sumHp = 0;
                    for (FightObject fobj : warCombo.getFightObjects()) {
                        if (fobj.getAtk() > fobj.getMatk()) {
                            atk = fobj.getAtk();
                            def = goal.getDef();
                        } else {
                            atk = fobj.getMatk();
                            def = goal.getMdef();
                        }

                        sumHp += attack1(warInfo.getTerrain(), fobj, goal, atk, def, 1);
                    }

                    int hp = (int) (sumHp * j);
                    goal.decrementHp(hp);
                    goal.decrementFury(1);

                    ae.setVal(hp);

                    // result
                    ComboResult cr = new ComboResult();
                    cr.setCount(warCombo.getComboCount());
                    cr.setShot(Shot.QXi);
                    cr.addTarget(ae);
                    details.add(cr);
                    break;
                }
                case BSa: {
                    double j = combosRatioFactors.get(warCombo.getComboCount() - 1).get(
                            warCombo.getShot());
                    JoinSkill js = joinSkills.get(warCombo.getFightObjectSize()).get(
                            warCombo.getAreaString());
                    ComboResult cr = skillComboAoeAttack(warInfo, dire, warCombo,
                            defenderEntity.findFightGoals(js.getNumOfTargets()),
                            js.getFactor() * j, js);

                    if (!"none".equals(js.getJointArea()) && js.getAttackerCntNeeded() >= 4) {
                        for (FightObject fo : attackerEntity.getFightObjects()) {
                            if (fo.isDead()) {
                                continue;
                            }

                            if (js.getAttackerCntNeeded() == 4) {
                                new Shu4SkillBuffer(warInfo, dire, fo).onBefore();
                            } else if (js.getAttackerCntNeeded() == 5) {
                                new Shu5SkillBuffer(warInfo, dire, fo).onBefore();
                            }
                        }
                    }
                    details.add(cr);
                    break;
                }
                case YHu: {
                    double j
                            = combosRatioFactors.get(warCombo.getComboCount() - 1).get(
                                    warCombo.getShot());
                    FightObject succor = attackerEntity.getSuccor();
                    Script script = scriptManager.getScript("war.bs." + succor.getBsaSkill());

                    ComboResult cr = new ComboResult();
                    cr.setCount(warCombo.getComboCount());
                    cr.setShot(Shot.YHu);

                    MultiAttackResult mar
                            = (MultiAttackResult) script.invoke(null, new BSaWapper(attackerEntity,
                                            defenderEntity, succor, bsaSkills.get(succor.getBsaSkill()),
                                            warInfo, j));
                    if (mar != null) {
                        cr.setTargets(mar.getTargets());
                    }
                    details.add(cr);
                    break;
                }
                case Q7: {
                    if (warCombo.getComboCount() <= 1) {
                        for (FightObject fo : attackerEntity.getFightObjects()) {
                            fo.incrementFury(1);
                        }
                    } else {
                        // 变怒
                        scriptManager.getScript("war.Q7ComboScript").invoke(null,
                                new Q7ComboWapper(warInfo, attackerEntity, warCombo));
                    }

                    ComboResult cr = new ComboResult();
                    cr.setCount(warCombo.getComboCount());
                    cr.setShot(Shot.Q7);

                    details.add(cr);
                    break;
                }
            }

            if (attackerEntity.isOver()) {
                return WarReport.Effect.D;
            }

            if (defenderEntity.isOver()) {
                return WarReport.Effect.W;
            }
        }

        return WarReport.Effect.C;
    }

    /**
     *
     * @param we
     * @return
     */
    public WarReport.Entity newWarReportEntity(WarInfo.Entity we) {
        WarReport.Entity rs = new WarReport.Entity();
        rs.setRoleName(we.getRoleName());
        rs.setLevel(we.getLevel());
        rs.setPowerGuess(we.getPowerGuess());
        
        rs.setSuccorIid(we.getSuccor().getHeroId());

        // FIXME Test
        rs.setSuccorAtk(we.getSuccor().getAtk());
        rs.setSuccorFury(we.getSuccor().getFury());

        List<JSONObject> heros = new ArrayList<>(we.getFightObjects().size());
        for (FightObject fo : we.getFightObjects()) {
            JSONObject json = new JSONObject();
            json.put(HeroPropertyKeys.ID, fo.getHeroId());
            json.put(HeroPropertyKeys.LADDER, fo.getLadder());
            json.put(HeroPropertyKeys.STAR, fo.getStar());
            json.put(HeroPropertyKeys.LEVEL, fo.getLevel());
            json.put(HeroPropertyKeys.HP, fo.getMaxHp());
            json.put(HeroPropertyKeys.FURY, fo.getFury());

            // FIXME Test Data
            json.put(HeroPropertyKeys.ATK, fo.getAtk());
            json.put(HeroPropertyKeys.DEF, fo.getDef());
            json.put(HeroPropertyKeys.MATK, fo.getMatk());
            json.put(HeroPropertyKeys.MDEF, fo.getMdef());
            json.put(HeroPropertyKeys.CRIT_RATE, MathUtils.round(fo.getCritRate(), 3));
            json.put(HeroPropertyKeys.DECRIT_RATE, MathUtils.round(fo.getDecritRate(), 3));
            json.put(HeroPropertyKeys.CRIT_MAGN, MathUtils.round(fo.getCritMagn(), 3));
            json.put(HeroPropertyKeys.PARRY_RATE, MathUtils.round(fo.getParryRate(), 3));
            json.put(HeroPropertyKeys.DEPARRY_RATE, MathUtils.round(fo.getDeparryRate(), 3));
            json.put(HeroPropertyKeys.PARRY_VALUE, MathUtils.round(fo.getParryValue(), 3));

            heros.add(json);
        }
        rs.setHeros(heros);

        return rs;
    }

    /**
     *
     * @return
     */
    public String randomSuccorHeroId() {
        String[] ids = Typhons.getProperty("typhon.spi.pve.buildHeroIds").split(",");
        int i = HERO_ID_RANDOM.nextInt(ids.length);
        return ids[i];
    }

    private PacketSuccor newPacketSuccor(int rid, VacantData vacantData) {
        Session otherSession = sessionManager.getSession(rid);
        PacketSuccor succor;
        int societyId;
        if (otherSession != null) {
            Normal otherNormal = SessionUtils.getPlayer(otherSession).getNormal();
            FightGroup fightGroup = otherNormal.getFightGroup(otherNormal.getLastFidx());
            HeroItem primaryHero = fightGroup.getHeroItem(fightGroup.getCaptain());

            succor = new PacketSuccor();
            succor.setRid(otherNormal.player().getRole().getRid());
            succor.setName(otherNormal.player().getRole().getName());
            succor.setPlayerLevel(otherNormal.getLevel());

            succor.setIid(primaryHero.getId());
            succor.setLevel(primaryHero.getLevel());
            succor.setStar(primaryHero.getStar().name());
            succor.setPowerGuess(primaryHero.getPowerGuess());
            succor.setLadder(primaryHero.getLadder());
            succor.setAvatar(otherNormal.getAvatar());
            succor.setAvatarBorder(otherNormal.getAvatarBorder());
            societyId = otherNormal.getSocietyId();
        } else {
            if (vacantData == null) {
                vacantData = roleProvider.loadVacantData(rid);
            }

            succor = new PacketSuccor();
            succor.setRid(vacantData.getRid());
            succor.setName(vacantData.getName());
            succor.setIid(vacantData.getPrimaryHero().getId());
            succor.setPlayerLevel(vacantData.getLevel());

            succor.setStar(vacantData.getPrimaryHero().getStar().name());
            succor.setPowerGuess(vacantData.getPrimaryHero().getPowerGuess());
            succor.setLevel(vacantData.getPrimaryHero().getLevel());
            succor.setLadder(vacantData.getPrimaryHero().getLadder());
            succor.setAvatar(vacantData.getAvatar());
            succor.setAvatarBorder(vacantData.getAvatarBorder());
            societyId = vacantData.getSocietyId();
        }

        Society society = societyProvider.findBySid(societyId);
        if (society != null) {
            succor.setSocietyName(society.getName());
        }
        return succor;
    }

    /**
     *
     * @return
     */
    private int[] randomHoldPoints() {
        int[] points
                = {HOLD_POINT_RANDOM.nextInt(14), HOLD_POINT_RANDOM.nextInt(14),
                    HOLD_POINT_RANDOM.nextInt(14), HOLD_POINT_RANDOM.nextInt(14),
                    HOLD_POINT_RANDOM.nextInt(14)};
        return points;
    }

    //======================== End =================================================================
    private double calculateSkillComboX(WarInfo warInfo, WarCombo combo, boolean b) {
        double sum = 0;
        FightObject fo;
        double atk;
        double f;
        double p1;

        StringBuilder sb = null;
        if (LOG.isDebugEnabled()) {
            sb = new StringBuilder("(");
        }

        for (Point point : combo.getPoints()) {
            fo = point.getFightObject();
            atk = Math.max(fo.getAtk(), fo.getMatk());
            f = getFuryFactor(fo.getFury());
            // 地形相克系数
            if (b) {
                p1 = getTerrainFactor(fo.getRace(), warInfo.getTerrain());
            } else {
                p1 = 1;
            }
            sum += atk * p1 * f;

            if (LOG.isDebugEnabled()) {
                sb.append("(").append(fo.getHeroId()).append(": ");
                sb.append(atk).append(" * ").append(p1).append(" * ");
                sb.append(f).append(") + ");
            }
        }

        double res = sum / combo.getPoints().size();
        if (LOG.isDebugEnabled()) {
            sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
            sb.append(")");
            LOG.debug("{} = {} / {}", res, sum, combo.getPoints().size());
        }
        return res;
    }

    private void plusComboPoint(Map<Shot, WarCombo> combos, Shot shot, int x, int y, FightObject fo) {
        WarCombo warCombo = combos.get(shot);
        if (warCombo == null) {
            warCombo = new WarCombo(shot);
            combos.put(shot, warCombo);
        }

        warCombo.addPoint(new WarCombo.Point(x, y, fo));
    }

    private Shot[][] toShotTable(List<FightObject> fightObjects, int[] holdPoints) {
        Shot[][] table = new Shot[3][5];
        for (int i = 0; i < fightObjects.size(); i++) {
            FightObject fightObject = fightObjects.get(i);
            if (!fightObject.isAvailable()) {
                table[ROW_1ST][i] = IFightItem.Shot.None;
                table[ROW_2ND][i] = IFightItem.Shot.None;
                table[ROW_3RD][i] = IFightItem.Shot.None;
            } else {
                table[ROW_1ST][i] = getNextShot(fightObject, holdPoints[i]);
                table[ROW_2ND][i] = getShot(fightObject, holdPoints[i]);
                table[ROW_3RD][i] = getPrevShot(fightObject, holdPoints[i]);
            }
        }

        if (LOG.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n++++++++++++++++++++++++++++++++++++");
            for (Shot[] shots : table) {
                sb.append("\n+");
                for (Shot shot : shots) {
                    sb.append(org.apache.commons.lang3.StringUtils.center(shot.name(), 6)).append(
                            "+");
                }
                sb.append("\n++++++++++++++++++++++++++++++++++++");
            }
            LOG.debug(sb.toString());
        }
        return table;
    }

    //=======================================加载配置数据===================================================
    private void loadTerrainWarFactor() {
        JSONArray array = JSON.parseArray(ComponentUtils.readDataFile("terrain_war_factor.json"));
        TerrainFactorData[] factors = new TerrainFactorData[array.size()];
        for (int i = 0; i < factors.length; i++) {
            factors[i] = new TerrainFactorData(array.getJSONObject(i));
        }
        terrainFactors = factors;
    }

    private void loadRaceWarFactor() {
        JSONArray array = JSON.parseArray(ComponentUtils.readDataFile("race_war_factor.json"));
        RaceFactorData[] factors = new RaceFactorData[array.size()];
        for (int i = 0; i < factors.length; i++) {
            factors[i] = new RaceFactorData(array.getJSONObject(i));
        }
        raceFactors = factors;
    }

    private void loadFuryWarFactor() {
        JSONArray array = JSON.parseArray(ComponentUtils.readDataFile("fury_war_factor.json"));
        double[] factors = new double[array.size()];
        for (int i = 0; i < factors.length; i++) {
            factors[i] = array.getJSONObject(i).getDouble("v");
        }
        furyFactors = factors;
    }

    private void loadCombosRatioFactor() {
        JSONArray array = JSON.parseArray(ComponentUtils.readDataFile("client/combos_ratio.json"));
        for (int i = 0; i < array.size(); i++) {
            JSONObject json = array.getJSONObject(i);
            Map<Shot, Double> factors = new HashMap<>(json.size());

            for (String k : json.keySet()) {
                factors.put(Shot.valueOf(k), json.getDoubleValue(k));
            }
            combosRatioFactors.add(factors);
        }
    }

    private void loadBSaSkillConfig() {
        List<BSaSkill> list
                = JSON.parseArray(ComponentUtils.readDataFile("BSa_skill.json"), BSaSkill.class);
        for (BSaSkill bs : list) {
            bsaSkills.put(bs.getId(), bs);
        }
    }

    private void loadJoinSkillConfig() {
        List<JoinSkill> list
                = JSON.parseArray(ComponentUtils.readDataFile("Joint_skill.json"), JoinSkill.class);
        for (JoinSkill js : list) {
            Map<String, JoinSkill> map = joinSkills.get(js.getAttackerCntNeeded());
            if (map == null) {
                map = new HashMap<>(3);
                joinSkills.put(js.getAttackerCntNeeded(), map);
            }
            map.put(js.getJointArea(), js);
        }
    }

    //======================战斗系数==================================================================
    private double getAttackFactor() {
        return (MIN_FACTOR + FACTOR_RANDOM.nextInt(11) / 100D);
    }

    private double getCritRate() {
        return (FACTOR_RANDOM.nextInt(101) / 100D);
    }

    private String randomName(List<PacketSuccor> vals) {
        String name;

        f1:
        for (;;) {
            name = roleProvider.randomName();
            for (PacketSuccor ps : vals) {
                if (name.equals(ps.getName())) {
                    continue f1;
                }
            }
            return name;
        }
    }

    private JSONObject buildSuccorData(HeroItem hero) {
        JSONObject json = new JSONObject();
        double p = Typhons.getDouble("typhon.spi.war.succorMagn");
        json.put("extraTong", hero.getExtraTong() * p);
        json.put("extraWu", hero.getExtraWu() * p);
        json.put("extraZhi", hero.getExtraZhi() * p);
        json.put("extraAtk", hero.getExtraAtk() * p);
        json.put("extraDef", hero.getExtraDef() * p);
        json.put("extraMatk", hero.getExtraMatk() * p);
        json.put("extraMdef", hero.getExtraMdef() * p);
        json.put("extraHp", hero.getExtraHp() * p);
        json.put("extraCritRate", hero.getExtraCritRate() * p);
        json.put("extraDecritRate", hero.getExtraDecritRate() * p);
        json.put("extraCritMagn", hero.getExtraCritMagn() * p);
        json.put("extraParryRate", hero.getExtraParryRate() * p);
        json.put("extraParryValue", hero.getExtraParryValue() * p);
        json.put("extraDeparryRate", hero.getExtraDeparryRate() * p);
        json.put("star", hero.getStar());
        json.put("level", hero.getLevel());
        return json;
    }

    private class RaceFactorData {

        Race Name;
        double Ce;
        double Bu;
        double Qi;
        double Gong;
        double Che;

        RaceFactorData(JSONObject obj) {
            Name = Race.valueOf(obj.getString("Name"));
            Ce = obj.getDouble("Ce");
            Bu = obj.getDouble("Bu");
            Qi = obj.getDouble("Qi");
            Gong = obj.getDouble("Gong");
            Che = obj.getDouble("Che");
        }

    }

    private class TerrainFactorData {

        Race Name;
        double SLu;
        double SLing;
        double PYuan;
        double SDi;
        double CGuan;

        TerrainFactorData(JSONObject obj) {
            Name = Race.valueOf(obj.getString("Name"));
            SLu = obj.getDouble("SLu");
            SLing = obj.getDouble("SLing");
            PYuan = obj.getDouble("PYuan");
            SDi = obj.getDouble("SDi");
            CGuan = obj.getDouble("CGuan");
        }
    }
}
