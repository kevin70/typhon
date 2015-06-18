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
package org.skfiy.typhon.spi.hero;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.management.ObjectName;

import org.apache.commons.modeler.ManagedBean;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.dobj.EquipmentItemDobj;
import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.dobj.Soul;
import org.skfiy.typhon.dobj.SoulItemDobj;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.IHeroEntity;
import org.skfiy.typhon.domain.IHeroEntity.Rabbet;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.EquipmentItem;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.domain.item.Item.Star;
import org.skfiy.typhon.domain.item.Race;
import org.skfiy.typhon.domain.item.SoulItem;
import org.skfiy.typhon.packet.EnchantPacket;
import org.skfiy.typhon.packet.HeroLottery;
import org.skfiy.typhon.packet.HeroRabbet;
import org.skfiy.typhon.packet.MultipleValue;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.repository.impl.UserRepositoryImpl;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.Vip;
import org.skfiy.typhon.spi.hero.Herofactor.starEnum;
import org.skfiy.typhon.spi.ranking.UpdateRankingList;
import org.skfiy.typhon.spi.role.ExpLevel;
import org.skfiy.typhon.spi.store.Commoditied;
import org.skfiy.typhon.util.ComponentUtils;
import org.skfiy.typhon.util.FastRandom;
import org.skfiy.typhon.util.MBeanUtils;
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
public class HeroProvider extends AbstractComponent {

    private static final Logger LOG = LoggerFactory.getLogger(HeroProvider.class);

    // 功勋抽奖配置
    private final List<Lottery> copperLotteries = new ArrayList<>();
    private final List<Lottery> copperX3EquipmentItems = new ArrayList<>();
    private final List<Lottery> copperHeroItems = new ArrayList<>();
    private final List<Lottery> copperSoulItems = new ArrayList<>();

    // 钻石抽奖配置
    private final List<Lottery> diamondLotteries = new ArrayList<>();
    private final List<Lottery> diamondLotteries2 = new ArrayList<>();
    private final List<Lottery> diamondLotteries3 = new ArrayList<>();
    private final List<Lottery> diamondLotteries4 = new ArrayList<>();
    private final List<Lottery> diamondLotteries5 = new ArrayList<>();
    private final List<Lottery> diamondHeroItems = new ArrayList<>();
    private final List<Lottery> diamondHeroX2AndX3Items = new ArrayList<>();
    private final List<Lottery> diamondHeroX3Items = new ArrayList<>();
    private final List<Lottery> diamondHeroX2Items = new ArrayList<>();
    private final List<Lottery> diamondHeroX1Items = new ArrayList<>();

    private final FastRandom copperRandom = new FastRandom();
    private final FastRandom diamondRandom = new FastRandom();
    private final FastRandom lotteryCountRandom = new FastRandom();
    // 武将升阶配置
    private final Map<String, JSONArray> heroLadders = new HashMap<>();
    private final JSONArray heroLadderProperties = new JSONArray();
    // 武将升星配置
    private final Map<String, JSONArray> heroStars = new HashMap<>();

    // 土豪抽
    private final FastRandom tuhaoRandom = new FastRandom();
    private final List<TuhaoWeekLottery> tuhaoWeekLotteries = new ArrayList<>();
    private final List<List<Lottery>> tuhaoDayLotteries = new ArrayList<>();
    private final List<Lottery> tuhaoLotteries = new ArrayList<>();

    // 附魔属性评分
    private EnchantFactor enchantPropScoreFactor;
    // 附魔权重评分
    private EnchantFactor enchantWeightScoreFactor;

    @Inject
    private ItemProvider itemProvider;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private UpdateRankingList updateRankingLists;
    @Inject
    private UserRepositoryImpl userRepository;
    @Inject
    private HeroExclusiveProvider heroExclusiveProvider;

    private static JSONArray expLeveArray;
    private static JSONArray enchantReturn;
    private final Map<String, Integer> enchantCream = new HashMap<>();
    private int copperRandomFactor;
    private int diamonRandomFactor;
    private int diamonRandomFactor2;
    private int diamonRandomFactor3;
    private int diamonRandomFactor4;
    private int diamonRandomFactor5;

    private double tuhaoWeekRandomFactor;
    private int tuhaoRandomFactor;

    private ObjectName oname;

    @Override
    protected void doInit() {
        JSONArray datas = JSON.parseArray(ComponentUtils.readDataFile("copper_lottery.json"));
        parseLotteryDatas(datas, copperLotteries, null, "prob");
        for (Lottery lo : copperLotteries) {
            if (lo.itemDobj instanceof EquipmentItemDobj) {
                if (lo.itemDobj.getStar() == Star.X3) {
                    copperX3EquipmentItems.add(lo);
                }
            } else if (lo.itemDobj instanceof SoulItemDobj) {
                copperSoulItems.add(lo);
            } else if (lo.itemDobj instanceof HeroItemDobj) {
                copperHeroItems.add(lo);
            }
        }
        copperRandomFactor = calibrateLotteryProb(copperLotteries);

        datas = JSON.parseArray(ComponentUtils.readDataFile("diamond_lottery.json"));
        parseLotteryDatas(datas, diamondLotteries, diamondHeroItems, "prob");
        diamonRandomFactor = calibrateLotteryProb(diamondLotteries);

        parseLotteryDatas(datas, diamondLotteries2, null, "prob2");
        diamonRandomFactor2 = calibrateLotteryProb(diamondLotteries2);

        parseLotteryDatas(datas, diamondLotteries3, null, "prob3");
        diamonRandomFactor3 = calibrateLotteryProb(diamondLotteries3);

        parseLotteryDatas(datas, diamondLotteries4, null, "prob4");
        diamonRandomFactor4 = calibrateLotteryProb(diamondLotteries4);

        parseLotteryDatas(datas, diamondLotteries5, null, "prob5");
        diamonRandomFactor5 = calibrateLotteryProb(diamondLotteries5);

        for (Lottery lo : diamondHeroItems) {
            if (lo.itemDobj.getStar() == Star.X3) {
                diamondHeroX3Items.add(lo);
            } else if (lo.itemDobj.getStar() == Star.X2) {
                diamondHeroX2Items.add(lo);
            } else if (lo.itemDobj.getStar() == Star.X1) {
                diamondHeroX1Items.add(lo);
            }
        }
        diamondHeroX2AndX3Items.addAll(diamondHeroX2Items);
        diamondHeroX2AndX3Items.addAll(diamondHeroX3Items);

        datas = JSON.parseArray(ComponentUtils.readDataFile("hero_ladder.json"));
        for (int i = 0; i < datas.size(); i++) {
            JSONObject json = datas.getJSONObject(i);
            heroLadders.put(json.getString("#hero.id"), json.getJSONArray("ladders"));
        }

        heroLadderProperties.addAll(JSON.parseArray(ComponentUtils
                .readDataFile("hero_ladder_properties.json")));

        datas = JSON.parseArray(ComponentUtils.readDataFile("hero_stars.json"));
        for (int i = 0; i < datas.size(); i++) {
            JSONObject json = datas.getJSONObject(i);
            heroStars.put(json.getString("#hero.id"), json.getJSONArray("stars"));
        }

        expLeveArray = JSON.parseArray(ComponentUtils.readDataFile("enchant_exp_level.json"));
        JSONArray enchantProperty =
                JSON.parseArray(ComponentUtils.readDataFile("enchant_property.json"));
        enchantPropScoreFactor =
                JSON.toJavaObject(enchantProperty.getJSONObject(0), EnchantFactor.class);
        enchantWeightScoreFactor =
                JSON.toJavaObject(enchantProperty.getJSONObject(1), EnchantFactor.class);

        enchantReturn = JSON.parseArray(ComponentUtils.readDataFile("enchant_return.json"));

        JSONArray enchantArray =
                JSON.parseArray(ComponentUtils.readDataFile("enchant_consume.json"));
        for (int i = 0; i < enchantArray.size(); i++) {
            JSONObject obj = enchantArray.getJSONObject(i);
            enchantCream.put(obj.getString("#item.id"), obj.getInteger("cream"));
        }

        // =========================================================================================
        JSONArray jsonArray =
                JSON.parseObject(ComponentUtils.readDataFile("diamond_tuhao_week_lottery.json"))
                        .getJSONArray("datas");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            tuhaoWeekLotteries.add(new TuhaoWeekLottery(obj));
        }
        tuhaoWeekRandomFactor = calibrateLotteryProb2(tuhaoWeekLotteries);

        jsonArray =
                JSON.parseObject(ComponentUtils.readDataFile("diamond_tuhao_day_lottery.json"))
                        .getJSONArray("datas");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONArray subArray = jsonArray.getJSONArray(i);
            List<Lottery> list = new ArrayList<>();

            for (int j = 0; j < subArray.size(); j++) {
                list.add(new TuhaoLottery(subArray.getJSONObject(j)));
            }

            calibrateLotteryProb(list);
            tuhaoDayLotteries.add(list);
        }

        jsonArray =
                JSON.parseObject(ComponentUtils.readDataFile("diamond_tuhao_lottery.json"))
                        .getJSONArray("datas");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            tuhaoLotteries.add(new TuhaoLottery(obj));
        }
        tuhaoRandomFactor = calibrateLotteryProb(tuhaoLotteries);

        // 注册MBean
        ManagedBean managedBean = MBeanUtils.findManagedBean(getClass());
        oname = MBeanUtils.registerComponent(this, managedBean);
    }

    @Override
    protected void doReload() {
        // FIXME 重置武将配置文件
    }

    @Override
    protected void doDestroy() {
        if (oname != null) {
            MBeanUtils.REGISTRY.unregisterComponent(oname);
        }
    }

    // ================================Begin 获取英雄属性==============================================
    /**
     * 获取武将"统"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 统
     */
    public double getTong(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double tong =
                itemDobj.getTong() + heroEntity.getExtraTong() + (heroEntity.getLevel() - 1)
                        * itemDobj.getTongUp()
                        * Typhons.getDouble(heroEntity.getStar().getHeroFactorKey());

        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                tong += rabbet.getTong();
            }
        }
        return tong;
    }

    /**
     * 获取武将"武"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 武
     */
    public double getWu(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double wu =
                itemDobj.getWu() + heroEntity.getExtraWu() + (heroEntity.getLevel() - 1)
                        * itemDobj.getWuUp()
                        * Typhons.getDouble(heroEntity.getStar().getHeroFactorKey());

        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                wu += rabbet.getWu();
            }
        }
        return wu;
    }

    /**
     * 获取武将"智"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 智
     */
    public double getZhi(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double zhi
                = itemDobj.getZhi() + heroEntity.getExtraZhi() + (heroEntity.getLevel() - 1)
                * itemDobj.getZhiUp()
                * Typhons.getDouble(heroEntity.getStar().getHeroFactorKey());

        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                zhi += rabbet.getZhi();
            }
        }
        return zhi;
    }

    /**
     * 获取武将"攻击力"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 攻击力
     */
    public int getAtk(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double atk =
                (getTong(heroEntity) * itemDobj.getAtkX() + getWu(heroEntity) * itemDobj.getHpZ())
                        * itemDobj.getAtkA() + heroEntity.getExtraAtk();

        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                atk += rabbet.getAtk();
            }
        }
        if (heroEntity.getWeaponsRabbets() != null) {
            atk += heroEntity.getWeaponsRabbets().getAtk();
        }
        if (heroEntity.getWeaponsEnchant() != null) {
            atk += heroEntity.getWeaponsEnchant().getAtk();
        }
        if (heroEntity.getWeaponsStreng() != null) {
            atk += heroEntity.getWeaponsStreng().getAtk();
        }
        if (heroEntity.getWeaponsBuild() != null) {
            atk += heroEntity.getWeaponsBuild().getAtk();
        }

        // 铁骑增加15%的攻击力
        if (heroEntity.getRace() == Race.Qi) {
            atk *= 1.15;
        }

        return (int) atk;
    }

    /**
     * 获取武将"防御力"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 防御力
     */
    public int getDef(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double def =
                (getTong(heroEntity) * itemDobj.getHpZ() + getWu(heroEntity) * itemDobj.getAtkX())
                        * itemDobj.getDefB() + heroEntity.getExtraDef();

        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                def += rabbet.getDef();
            }
        }
        if (heroEntity.getWeaponsRabbets() != null) {
            def += heroEntity.getWeaponsRabbets().getDef();
        }
        if (heroEntity.getWeaponsEnchant() != null) {
            def += heroEntity.getWeaponsEnchant().getDef();
        }
        if (heroEntity.getWeaponsStreng() != null) {
            def += heroEntity.getWeaponsStreng().getDef();
        }
        if (heroEntity.getWeaponsBuild() != null) {
            def += heroEntity.getWeaponsBuild().getDef();
        }

        // 虎卫增加20%的物理防御
        if (heroEntity.getRace() == Race.Bu) {
            def *= (1.2);
        }
        return (int) def;
    }

    /**
     * 获取武将"魔法攻击力"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 魔法攻击力
     */
    public int getMatk(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double matk =
                (getTong(heroEntity) * itemDobj.getAtkX() + getZhi(heroEntity) * itemDobj.getHpZ())
                        * itemDobj.getAtkA() + heroEntity.getExtraMatk();

        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                matk += rabbet.getMatk();
            }
        }
        if (heroEntity.getWeaponsRabbets() != null) {
            matk += heroEntity.getWeaponsRabbets().getMatk();
        }
        if (heroEntity.getWeaponsEnchant() != null) {
            matk += heroEntity.getWeaponsEnchant().getMatk();
        }
        if (heroEntity.getWeaponsStreng() != null) {
            matk += heroEntity.getWeaponsStreng().getMatk();
        }
        if (heroEntity.getWeaponsBuild() != null) {
            matk += heroEntity.getWeaponsBuild().getMatk();
        }

        // 军师增加15%的法术攻击
        if (heroEntity.getRace() == Race.Ce) {
            matk *= (1.15);
        }
        return (int) matk;
    }

    /**
     * 获取武将"魔法防御力"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 魔法防御力
     */
    public int getMdef(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double mdef =
                (getTong(heroEntity) * itemDobj.getHpZ() + getZhi(heroEntity) * itemDobj.getAtkX())
                        * itemDobj.getDefB() + heroEntity.getExtraMdef();

        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                mdef += rabbet.getMdef();
            }
        }
        if (heroEntity.getWeaponsRabbets() != null) {
            mdef += heroEntity.getWeaponsRabbets().getMdef();
        }
        if (heroEntity.getWeaponsEnchant() != null) {
            mdef += heroEntity.getWeaponsEnchant().getMdef();
        }
        if (heroEntity.getWeaponsStreng() != null) {
            mdef += heroEntity.getWeaponsStreng().getMdef();
        }
        if (heroEntity.getWeaponsBuild() != null) {
            mdef += heroEntity.getWeaponsBuild().getMdef();
        }

        // 战车增加20%的法术防御
        if (heroEntity.getRace() == Race.Che) {
            mdef *= (1.2);
        }
        return (int) mdef;
    }

    /**
     * 获取武将"血量"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 血量
     */
    public int getHp(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double hp =
                (getTong(heroEntity) * itemDobj.getDefY() + getWu(heroEntity) * itemDobj.getDefY() + getZhi(heroEntity)
                        * itemDobj.getDefY())
                        * itemDobj.getHpC() + heroEntity.getExtraHp();

        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                hp += rabbet.getHp();
            }
        }
        if (heroEntity.getWeaponsRabbets() != null) {
            hp += heroEntity.getWeaponsRabbets().getHp();
        }
        if (heroEntity.getWeaponsEnchant() != null) {
            hp += heroEntity.getWeaponsEnchant().getHp();
        }
        if (heroEntity.getWeaponsStreng() != null) {
            hp += heroEntity.getWeaponsStreng().getHp();
        }
        if (heroEntity.getWeaponsBuild() != null) {
            hp += heroEntity.getWeaponsBuild().getHp();
        }
        return (int) hp;
    }

    /**
     * 获取武将"暴击"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 暴击
     */
    public double getCritRate(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double critRate =
                (int) Math
                        .floor((1 / (3 + 3000 / (getZhi(heroEntity) * itemDobj.getAtkX() + getWu(heroEntity)
                                * itemDobj.getHpZ())) + 0.05)
                                * 500 + heroEntity.getExtraCritRate());
        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                critRate += rabbet.getCritRate();
            }
        }
        if (heroEntity.getWeaponsBuild() != null) {
            critRate += heroEntity.getWeaponsBuild().getCritRate();
        }

        // 神射增加10%的暴击率
        if (heroEntity.getRace() == Race.Gong) {
            critRate += 50;
        }
        return critRate / 500;
    }

    /**
     * 获取武将"韧性"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 韧性
     */
    public double getDecritRate(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double decritRate =
                (int) Math
                        .floor((0.4 / (3 + 3000 / (getTong(heroEntity) * itemDobj.getAtkX() + getZhi(heroEntity)
                                * itemDobj.getHpZ())) + 0.03)
                                * 500 + heroEntity.getExtraDecritRate());
        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                decritRate += rabbet.getDecritRate();
            }
        }

        if (heroEntity.getWeaponsBuild() != null) {
            decritRate += heroEntity.getWeaponsBuild().getDecritRate();
        }
        return decritRate / 500;
    }

    /**
     * 获取武将"暴伤"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 暴伤
     */
    public double getCritMagn(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double critMagn = (int) Math.floor(itemDobj.getCritMagn() + heroEntity.getExtraCritMagn());
        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                critMagn += rabbet.getCritMagn();
            }
        }
        if (heroEntity.getWeaponsBuild() != null) {
            critMagn += heroEntity.getWeaponsBuild().getCritMagn();
        }
        return critMagn / 100;
    }

    /**
     * 获取武将"格挡"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 格挡
     */
    public double getParryRate(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double parryRate =
                (int) Math
                        .floor((0.8 / (3 + 3000 / (getTong(heroEntity) * itemDobj.getAtkX() + getWu(heroEntity)
                                * itemDobj.getHpZ())) + 0.05)
                                * 500 + heroEntity.getExtraParryRate());
        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                parryRate += rabbet.getParryRate();
            }
        }
        if (heroEntity.getWeaponsBuild() != null) {
            parryRate += heroEntity.getWeaponsBuild().getParryRate();
        }

        return parryRate / 500;
    }

    /**
     * 获取武将"穿透"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 穿透
     */
    public double getDeparryRate(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double deparryRate =
                (int) Math
                        .floor((0.5 / (3 + 3000 / (getZhi(heroEntity) * itemDobj.getHpZ() + getWu(heroEntity)
                                * itemDobj.getAtkX())) + 0.03)
                                * 600 + heroEntity.getExtraDeparryRate());
        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                deparryRate += rabbet.getDeparryRate();
            }
        }
        if (heroEntity.getWeaponsBuild() != null) {
            deparryRate += heroEntity.getWeaponsBuild().getDeparryRate();
        }
        return deparryRate / 600;
    }

    /**
     * 获取武将"免伤值"属性. 返回值包括武将本身, 装备与附魔总值.
     * 
     * @param heroEntity 武将实体
     * @return 免伤值
     */
    public double getParryValue(IHeroEntity heroEntity) {
        HeroItemDobj itemDobj = getHeroItemDobj(heroEntity);
        double parryValue =
                (int) Math.floor(itemDobj.getParryValue() + heroEntity.getExtraParryValue());
        if (heroEntity.getRabbets() != null && heroEntity.getRabbets().size() > 0) {
            for (Rabbet rabbet : heroEntity.getRabbets()) {
                parryValue += rabbet.getParryValue();
            }
        }
        return parryValue;
    }

    private HeroItemDobj getHeroItemDobj(IHeroEntity heroEntity) {
        if (heroEntity instanceof HeroItem) {
            return ((HeroItem) heroEntity).getItemDobj();
        } else {
            return itemProvider.getItem(heroEntity.getId());
        }
    }

    // ================================End 获取英雄属性================================================
    /**
     * 增加指定武将经验.
     * 
     * @param roleLevel 玩家等级
     * @param hero 英雄
     * @param exp 经验值
     */
    public void pushExp(int roleLevel, HeroItem hero, int exp) {
        if (exp <= 0) {
            return;
        }

        ExpLevel expLevel = roleProvider.getExpLevel(hero.getLevel());
        if (isMaxLevel(roleLevel, hero.getLevel()) && hero.getExp() >= expLevel.getHeroExp()) {
            return;
        }

        int oldExp = hero.getExp();
        int newExp = oldExp + exp;
        int newLevel = hero.getLevel();

        while (true) {
            expLevel = roleProvider.getExpLevel(newLevel);
            if (newExp < expLevel.getHeroExp()) {
                break;
            }

            if (isMaxLevel(roleLevel, newLevel)) {
                ExpLevel curLevel = roleProvider.getExpLevel(newLevel);
                if (newExp > curLevel.getHeroExp()) {
                    newExp = curLevel.getHeroExp();
                }
                break;
            }

            newExp -= expLevel.getHeroExp();
            newLevel++;
        }

        if (newLevel > hero.getLevel()) {
            hero.setLevel(newLevel);
            calculatorPowerGuess(hero);
        }
        hero.setExp(newExp);
    }

    /**
     * 判定武将是否已经达到目前最高等级.
     * 
     * @param roleLevel 角色等级
     * @param heroLevel 武将等级
     * @return 真/假
     */
    public boolean isMaxLevel(int roleLevel, int heroLevel) {
        ExpLevel expLevel = roleProvider.getExpLevel(roleLevel);
        return (heroLevel >= expLevel.getHeroMaxLevel());
    }

    /**
     * 计算武将战斗力.
     * 
     * @param heroItem 武将对象
     */
    public void calculatorPowerGuess(HeroItem heroItem) {

        double atk = Math.max(getAtk(heroItem), getMatk(heroItem));
        double bj = (1 + getCritRate(heroItem) * (getCritMagn(heroItem) - 1));
        double ct = (1 + getDeparryRate(heroItem) * 0.05) * 1.2;
        double def = (getDef(heroItem) + (double) getMdef(heroItem)) / 2;
        double ms = (getParryRate(heroItem) * getParryValue(heroItem) * 0.6);
        double hp = (getHp(heroItem) * 0.1);
        double rx = (1 + getDecritRate(heroItem) * 0.5);
        int pg =
                (int) ((atk * bj * ct + (def + ms + hp) * rx) * Typhons.getDouble(heroItem
                        .getRace().getHeroRace()));
        heroItem.setPowerGuess(pg);

        if (SessionUtils.isSessionAvailable()) {
            updateRankingLists.updatePowerguessRanking();
            // roleProvider.updateInformation();
        }
    }

    // 一键穿装备
    public void activateRabbetAll(HeroRabbet packet) {
        Player player = SessionUtils.getPlayer();
        Vip vip = roleProvider.getVip(player.getNormal().getVipLevel());
        if (!vip.privileged.one_key_equipment) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("VIPLEVEL is not enought");
            player.getSession().write(error);
            return;
        }
        HeroItem heroItem = player.getHeroBag().findNode(packet.getPos()).getItem();
        if (heroItem == null) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("Not found pos[" + packet.getPos() + "] item");
            player.getSession().write(error);
            return;
        }
        JSONArray ladder = heroLadders.get(heroItem.getId()).getJSONArray(heroItem.getLadder() - 1);
        String eid;
        Node equmNode;
        List<Integer> posts = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            if (heroItem.findRabbet(i) == null) {
                eid = ladder.getString(i - 1);
                equmNode = player.getBag().findNode(eid);
                if (equmNode != null
                        && heroItem.getLevel() >= ((EquipmentItem) equmNode.getItem())
                                .getLevelLimit()) {
                    EquipmentItem equipmentItem = equmNode.getItem();
                    activateRabbet(i, heroItem, equipmentItem.getItemDobj());
                    // 消耗道具
                    player.getBag().decrementTotal(equmNode, 1);
                    posts.add(i);
                }
            }
        }
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(posts);
        player.getSession().write(result);
    }

    /**
     * 激动武将槽口(进阶).
     * 
     * @param packet 数据包
     */
    public void activateRabbet(HeroRabbet packet) {
        Player player = SessionUtils.getPlayer();
        HeroItem heroItem = player.getHeroBag().findNode(packet.getPos()).getItem();
        if (heroItem == null) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("Not found pos[" + packet.getPos() + "] item");
            player.getSession().write(error);
            return;
        }

        if (heroItem.findRabbet(packet.getPoint()) != null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.bat_request);
            error.setText("Activated");
            player.getSession().write(error);
            return;
        }
        JSONArray ladder = heroLadders.get(heroItem.getId()).getJSONArray(heroItem.getLadder() - 1);
        String eid = ladder.getString(packet.getPoint() - 1);
        Node equmNode = player.getBag().findNode(eid);
        if (equmNode == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText(heroItem.getId() + ":Not found equipment[" + eid + "]");
            player.getSession().write(error);
            return;
        }

        if (heroItem.getLevel() < ((EquipmentItem) equmNode.getItem()).getLevelLimit()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("level limit");
            player.getSession().write(error);
            return;
        }
        // 转移装备增加的属性至武将身上
        EquipmentItem equipmentItem = equmNode.getItem();
        activateRabbet(packet.getPoint(), heroItem, equipmentItem.getItemDobj());

        player.getBag().decrementTotal(equmNode, 1); // 消耗道具
        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 激活武将的槽点.
     * 
     * @param point 槽点
     * @param heroItem 形状对象
     * @param eqipmentItemDobj 装备对象, 如果该对象为null, 则通过武将的等阶, 及point来获取对应的装备对象
     */
    public void activateRabbet(int point, HeroItem heroItem, EquipmentItemDobj eqipmentItemDobj) {
        if (eqipmentItemDobj == null) {
            JSONArray ladder =
                    heroLadders.get(heroItem.getId()).getJSONArray(heroItem.getLadder() - 1);
            String eid = ladder.getString(point - 1);
            eqipmentItemDobj = itemProvider.getItem(eid);
        }

        heroItem.setExtraTong(heroItem.getExtraTong() + eqipmentItemDobj.getTong());
        heroItem.setExtraWu(heroItem.getExtraWu() + eqipmentItemDobj.getWu());
        heroItem.setExtraZhi(heroItem.getExtraZhi() + eqipmentItemDobj.getZhi());
        heroItem.setExtraAtk(heroItem.getExtraAtk() + eqipmentItemDobj.getAtk());
        heroItem.setExtraDef(heroItem.getExtraDef() + eqipmentItemDobj.getDef());
        heroItem.setExtraMatk(heroItem.getExtraMatk() + eqipmentItemDobj.getMatk());
        heroItem.setExtraMdef(heroItem.getExtraMdef() + eqipmentItemDobj.getMdef());

        heroItem.setExtraHp(heroItem.getExtraHp() + eqipmentItemDobj.getHp());
        heroItem.setExtraParryRate(heroItem.getExtraParryRate() + eqipmentItemDobj.getParryRate());
        heroItem.setExtraParryValue(heroItem.getExtraParryValue()
                + eqipmentItemDobj.getParryValue());
        heroItem.setExtraCritRate(heroItem.getExtraCritRate() + eqipmentItemDobj.getCritRate());
        heroItem.setExtraDecritRate(heroItem.getExtraDecritRate()
                + eqipmentItemDobj.getDecritRate());
        heroItem.setExtraDeparryRate(heroItem.getExtraDeparryRate()
                + eqipmentItemDobj.getDeparryRate());
        heroItem.setExtraCritMagn(heroItem.getExtraCritMagn() + eqipmentItemDobj.getCritMagn());

        Rabbet rabbet = new Rabbet();
        rabbet.setPoint(point);
        heroItem.addRabbet(rabbet);
        calculatorPowerGuess(heroItem);
    }

    /**
     * 
     * @param packet
     */
    public void enchantOnce(EnchantPacket packet) {
        Player player = SessionUtils.getPlayer();
        HeroItem heroItem = player.getHeroBag().findNode(packet.getPos()).getItem();
        if (heroItem.findRabbet(packet.getPoint()) == null) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("the hore is not equipment");
            player.getSession().write(error);
            return;
        }
        int expAdd = 0;
        int number = 0;
        for (int a : packet.getExpendables()) {
            number += a;
            expAdd = (int) (a * Typhons.getDouble("typhon.spi.role.enchant.diamond.factor", 1));
        }

        JSONArray ladder = heroLadders.get(heroItem.getId()).getJSONArray(heroItem.getLadder() - 1);
        String eid = ladder.getString(packet.getPoint() - 1);

        Rabbet rabbet = start(expAdd, heroItem, eid, heroItem.findRabbet(packet.getPoint()));
        if (rabbet != null) {
            heroItem.setRabbet(rabbet);
            calculatorPowerGuess(heroItem);
            JSONObject object = new JSONObject();
            object.put("place", "EnchantOnce");
            object.put("heroId", heroItem.getId());
            object.put("heroStar", heroItem.getStar());
            object.put("equipmentPoint", packet.getPoint());
            SessionUtils.decrementDiamond(number, object.toString());
        }
        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 
     * @param packet
     */
    public void enchantRise(EnchantPacket packet) {
        Player player = SessionUtils.getPlayer();
        HeroItem heroItem = player.getHeroBag().findNode(packet.getPos()).getItem();
        if (heroItem.findRabbet(packet.getPoint()) == null) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("the hore is not equipment");
            player.getSession().write(error);
            return;
        }

        JSONArray ladder = heroLadders.get(heroItem.getId()).getJSONArray(heroItem.getLadder() - 1);
        String eid = ladder.getString(packet.getPoint() - 1);
        Rabbet rabbet =
                enchant(heroItem, player, packet, eid, heroItem.findRabbet(packet.getPoint()));
        if (rabbet != null) {
            heroItem.setRabbet(rabbet);
            calculatorPowerGuess(heroItem);
        }
        player.getSession().write(Packet.createResult(packet));
    }

    public Rabbet enchant(HeroItem heroItem, Player player, EnchantPacket packet, String eid,
            Rabbet rabbet) {
        List<Integer> a = packet.getExpendables();
        Set<Integer> c = new HashSet<>();
        int expAdd = 0;
        Map<Node, Integer> nodes = new HashMap<>();
        for (int i = 0; i < a.size(); i++) {
            c.add(a.get(i));
        }

        for (int b : c) {
            Node node = player.getBag().findNode(b);
            int count = 0;
            for (int j = 0; j < a.size(); j++) {
                if (b == a.get(j)) {
                    count++;
                }
                if (j == a.size() - 1) {
                    expAdd += enchantCream.get(node.getItem().getId()) * count;
                    nodes.put(node, count);
                }
            }
        }
        Rabbet newRabbet = start(expAdd, heroItem, eid, rabbet);
        if (newRabbet != null) {
            SessionUtils.decrementCopper((expAdd * Typhons.getInteger(
                    "typhon.spi.hero.enchant.depletion", 1000)));
            for (Map.Entry<Node, Integer> entry : nodes.entrySet()) {
                player.getBag().decrementTotal(entry.getKey(), entry.getValue());
            }
        }
        return newRabbet;
    }

    private List<Commoditied> enchantReturn(HeroItem heroItem) {
        int exp = 0;
        for (Rabbet r : heroItem.getRabbets()) {
            exp += r.getExpSum();
        }
        return enchantReturn1(exp);
    }

    public List<Commoditied> enchantReturn1(int exp) {
        List<Commoditied> commodities = new ArrayList<>();
        exp = (int) (exp * Typhons.getDouble("typhon.spi.hero.enchant.return", 0.5));
        // 个位不为0 向上进1
        if (exp % 10 != 0) {
            exp = exp / 10 * 10;
            if (exp == 0) {
                return commodities;
            }
        }
        for (int i = 0; i < enchantReturn.size(); i++) {
            Commoditied commoditied = new Commoditied();
            JSONObject object = enchantReturn.getJSONObject(i);

            int b = exp / object.getIntValue("exp");
            if (b <= 0) {
                continue;
            }
            String id = object.getString("#item.id");
            commoditied.setId(id);
            commoditied.setCount(b);
            commodities.add(commoditied);

            BagUtils.intoItem(itemProvider.getItem(id), b);
            int a = exp % object.getIntValue("exp");
            if (a > 0) {
                exp = a;
                continue;
            }

            if (a == 0) {
                break;
            }
        }
        return commodities;
    }

    /**
     * 
     * @param packet
     */
    public void upgradeLadder(HeroRabbet packet) {
        Player player = SessionUtils.getPlayer();
        HeroItem heroItem = player.getHeroBag().findNode(packet.getPos()).getItem();
        // 判断武将装备是否足够升阶
        if (heroItem.getRabbets().size() < 6) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("No upgrade");
            player.getSession().write(error);
            return;
        }

        if (heroItem.getLadder() >= Typhons.getInteger("typhon.domain.hero.maxLadder", 15)) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Already the largest");
            player.getSession().write(error);
            return;
        }

        // 进阶增加武将的属性
        JSONObject ladderProperty = heroLadderProperties.getJSONObject(heroItem.getLadder() - 1);
        heroItem.setExtraTong(heroItem.getExtraTong() + ladderProperty.getIntValue("tong"));
        heroItem.setExtraWu(heroItem.getExtraWu() + ladderProperty.getIntValue("wu"));
        heroItem.setExtraZhi(heroItem.getExtraZhi() + ladderProperty.getIntValue("zhi"));

        heroItem.setLadder(heroItem.getLadder() + 1);
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(enchantReturn(heroItem));
        heroItem.clearRabbets();

        calculatorPowerGuess(heroItem);
        player.getSession().write(result);
    }

    /**
     * 武将升星.
     * 
     * @param packet 协议包
     */
    public void upstar(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        HeroItem heroItem = player.getHeroBag().findNode((int) packet.getVal()).getItem();
        HeroItem.Star star = heroItem.getStar();

        if (star == HeroItem.Star.X5) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("highest-star");
            player.getSession().write(error);
            return;
        }

        HeroItem.Star nextStar = HeroItem.Star.values()[star.ordinal() + 1];
        JSONArray jsonArray = heroStars.get(heroItem.getId());
        int soulCount = Integer.MAX_VALUE;

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (nextStar.name().equals(obj.getString("star"))) {
                soulCount = obj.getIntValue("soulCount");
                break;
            }
        }

        Node soulNode =
                player.getBag().findNode(heroItem.getItemDobj().getSoul().getItemDobj().getId());
        if (!player.getBag().decrementTotal(soulNode, soulCount)) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("soul not enough");
            player.getSession().write(error);
            return;
        }

        heroItem.setStar(nextStar);
        calculatorPowerGuess(heroItem);

        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 武将召唤.
     * 
     * @param packet 协议包
     */
    public void beckon(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Node soul = player.getBag().findNode((int) packet.getVal());
        SoulItem soulItem = soul.getItem();
        SoulItemDobj soulItemDobj = soulItem.getItemDobj();

        // 已经存在对应的武将
        if (player.getHeroBag().findNode(soulItemDobj.getHeroItemDobj().getId()) != null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("exists hero");
            player.getSession().write(error);
            return;
        }

        SessionUtils.decrementCopper(soulItemDobj.getDepletion());

        if (!player.getBag().decrementTotal(soul, soulItemDobj.getCount())) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("count not enought");
            player.getSession().write(error);
            return;
        }

        player.getHeroBag().intoItem(soulItemDobj.getHeroItemDobj());
        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 金币抽卡.
     * 
     * @param packet 协议包
     */
    public void buyCopperLottery(Packet packet) {
        Normal normal = SessionUtils.getPlayer().getNormal();

        boolean freed = false;
        int freeCount = normal.getFreeCount();
        if (freeCount > 0) {
            freed =
                    (normal.getLastBuyCopperTime() + Typhons
                            .getLong("typhon.spi.hero.copperLottery.freeCD")) < System
                            .currentTimeMillis();
        }

        if (!freed) {
            int depletion = Typhons.getInteger("typhon.spi.hero.copperLottery.1.depletion");
            SessionUtils.decrementCopper(depletion);
        } else {
            normal.setFreeCount(freeCount - 1);
            normal.setLastBuyCopperTime(System.currentTimeMillis());
        }

        Lottery lottery = spueLottery(copperRandom, copperLotteries, copperRandomFactor);
        Packet result = pushLottery(lottery, false, false);
        Packet.assignResult(packet, result);
        SessionContext.getSession().write(result);
    }

    /**
     * 金币10连抽.
     * 
     * @param packet 协议包
     */
    public void buyCopperLottery10(Packet packet) {
        int depletion = Typhons.getInteger("typhon.spi.hero.copperLottery.10.depletion");
        SessionUtils.decrementCopper(depletion);
        Normal normal = SessionUtils.getPlayer().getNormal();
        MultipleValue result = MultipleValue.createResult(packet);

        Lottery lottery;
        if (!normal.isFirstC10Lottery()) {
            // 金币十连抽必出武将卡且不能是五小强
            String initHeros = Typhons.getProperty("typhon.spi.player.initHeros");
            for (;;) {
                lottery = copperHeroItems.get(copperRandom.nextInt(copperHeroItems.size()));
                if (!initHeros.contains(lottery.itemDobj.getId())) {
                    break;
                }
            }
            result.addVal(pushLottery(lottery, false, false));
            normal.setFirstC10Lottery(true);
        } else {
            // 灵魂石
            lottery = copperSoulItems.get(copperRandom.nextInt(copperSoulItems.size()));
            result.addVal(pushLottery(lottery, false, false));
        }

        // 3星装备
        lottery = copperX3EquipmentItems.get(copperRandom.nextInt(copperX3EquipmentItems.size()));
        result.addVal(pushLottery(lottery, false, false));

        for (int i = 0; i < 8; i++) {
            lottery = spueLottery(copperRandom, copperLotteries, copperRandomFactor);
            result.addVal(pushLottery(lottery, false, false));
        }

        Packet.assignResult(packet, result);
        SessionContext.getSession().write(result);
    }

    /**
     * 钻石抽卡.
     * 
     * @param packet 协议包
     */
    public void buyDiamondLottery(Packet packet) {
        Normal normal = SessionUtils.getPlayer().getNormal();
        boolean freed =
                (normal.getLastBuyDiamondTime() + Typhons
                        .getLong("typhon.spi.hero.diamondLottery.freeCD")) < System
                        .currentTimeMillis();

        Lottery lottery;
        if (!freed) {
            int depletion = Typhons.getInteger("typhon.spi.hero.diamondLottery.1.depletion");

            // 钻石首次抽卡必出3星英雄
            if (!normal.isFirstD1Lottery()) {
                for (;;) {
                    lottery =
                            diamondHeroX3Items
                                    .get(diamondRandom.nextInt(diamondHeroX3Items.size()));
                    // 首次单抽不出“关羽”
                    if (!"hb02".equals(lottery.itemDobj.getId())) {
                        break;
                    }
                }
                normal.setFirstD1Lottery(true);
            } else {
                lottery = diamondSpueLottery(normal);
            }

            JSONObject object = new JSONObject();
            object.put("place", "BuyDiamondLottery");
            object.put("lotteryItems", lottery.itemDobj.getId());
            SessionUtils.decrementDiamond(depletion, object.toString());
        } else {
            normal.setLastBuyDiamondTime(System.currentTimeMillis());
            // 免费抽卡采用第5档概率
            lottery = spueLottery(diamondRandom, diamondLotteries5, diamonRandomFactor5);
        }

        Packet result = pushLottery(lottery, true, false);
        Packet.assignResult(packet, result);

        SessionContext.getSession().write(result);
    }

    /**
     * 钻石10连抽.
     * 
     * @param packet 协议包
     */
    public void buyDiamondLottery10(Packet packet) {
        int depletion = Typhons.getInteger("typhon.spi.hero.diamondLottery.10.depletion");

        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        MultipleValue result = MultipleValue.createResult(packet);

        int c = 10;
        LotteryLimit lotteryLimit = new LotteryLimit(4, 3);
        Lottery lottery;
        // 钻石10连抽必出3星, 2星, 1星英雄
        if (!normal.isFirstD10Lottery()) {
            lottery = diamondHeroX3Items.get(diamondRandom.nextInt(diamondHeroX3Items.size()));
            result.addVal(pushLottery(lottery, true, false));
            lotteryLimit.hero++;

            lottery = diamondHeroX2Items.get(diamondRandom.nextInt(diamondHeroX2Items.size()));
            result.addVal(pushLottery(lottery, true, false));
            lotteryLimit.hero++;

            lottery = diamondHeroX1Items.get(diamondRandom.nextInt(diamondHeroX1Items.size()));
            result.addVal(pushLottery(lottery, true, false));
            lotteryLimit.hero++;

            normal.setDiamondLotteryCount(normal.getDiamondLotteryCount() + 3);
            normal.setFirstD10Lottery(true);
            c -= 3;
        } else {
            lottery =
                    diamondHeroX2AndX3Items.get(diamondRandom.nextInt(diamondHeroX2AndX3Items
                            .size()));
            result.addVal(pushLottery(lottery, true, false));
            normal.setDiamondLotteryCount(normal.getDiamondLotteryCount() + 1);
            lotteryLimit.hero++;
            c--;
        }

        boolean isSoul;
        for (int i = 0; i < c; i++) {
            for (;;) {
                lottery = diamondSpueLottery(normal);

                if (lottery.isHero && lotteryLimit.isMaxHero()) {
                    continue;
                } else {
                    isSoul = (lottery.itemDobj instanceof SoulItemDobj);
                    if (isSoul && lotteryLimit.isMaxSoul()) {
                        continue;
                    }
                }

                result.addVal(pushLottery(lottery, true, false));

                if (lottery.isHero) {
                    lotteryLimit.hero++;
                }
                if (isSoul) {
                    lotteryLimit.soul++;
                }
                break;
            }
        }

        JSONObject object = new JSONObject();
        object.put("place", "BuyDiamondLottery10");
        object.put("lottery10Items", result.getVals());

        SessionUtils.decrementDiamond(depletion, object.toString());
        player.getSession().write(result);
    }

    /**
     * 
     * @param packet
     */
    public void buyTuhaoLottery(Packet packet) {
        Player player = SessionUtils.getPlayer();
        MultipleValue result = MultipleValue.createResult(packet);

        Calendar cal = Calendar.getInstance();
        // 抽卡
        int weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        if (weekOfYear != 1 && cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            weekOfYear--;
        }

        TuhaoWeekLottery tuhaoWeekLottery = tuhaoWeekLotteries.get(weekOfYear - 1);

        boolean heroed = drawTuhao0(player, tuhaoWeekLottery);
        if (heroed) {
            result.addVal(pushLottery(tuhaoWeekLottery, false, true));
        } else {
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            Lottery tuhaoDayLottery =
                    spueLottery(tuhaoRandom, tuhaoDayLotteries.get(dayOfMonth - 1), 100);
            result.addVal(pushLottery(tuhaoDayLottery, false, true));
        }

        Lottery tuhaoLottery;
        for (int i = 0; i < 5; i++) {
            tuhaoLottery = spueLottery(tuhaoRandom, tuhaoLotteries, tuhaoRandomFactor);
            result.addVal(pushLottery(tuhaoLottery, false, true));
        }

        int diamond = Typhons.getInteger("typhon.spi.hero.diamondTuhaoLottery.1.depletion");

        JSONObject plog = new JSONObject();
        plog.put("name", "Tuhao");
        plog.put("items", result.getVals());
        SessionUtils.decrementDiamond(diamond, plog.toJSONString());

        player.getSession().write(result);
    }

    private boolean drawTuhao0(Player player, TuhaoWeekLottery tuhaoWeekLottery) {
        int ttc = player.getInvisible().getTuhaoLotteryCount();
        double prob = tuhaoWeekLottery.prob1;
        if (ttc <= 10) {
            prob += (tuhaoWeekLottery.prob1 * ttc);
        } else if (ttc <= 25) {
            prob += (tuhaoWeekLottery.prob1 * 10);
            prob += (tuhaoWeekLottery.prob2 * (ttc - 10));
        } else if (ttc <= tuhaoWeekLottery.maxNum) {
            prob += (tuhaoWeekLottery.prob1 * 10);
            prob += (tuhaoWeekLottery.prob2 * 15);
            prob += (tuhaoWeekLottery.prob3 * (ttc - 25));
        }

        if (ttc >= (tuhaoWeekLottery.maxNum - 1) || tuhaoRandom.nextDouble() < prob) {
            player.getInvisible().setTuhaoLotteryCount(0);
            return true;
        }

        player.getInvisible().setTuhaoLotteryCount(ttc + 1);
        return false;
    }

    private HeroLottery pushLottery(Lottery lottery, boolean diamond, boolean tuhaoed) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        int counts = normal.getDailyTask().getTaskLotteries();
        if (counts >= 0) {
            normal.getDailyTask().setTaskLotteries(counts + 1);
        }
        boolean souled = false;
        int count = 1;
        if (lottery.isHero) {
            souled = player.getHeroBag().findNode(lottery.itemDobj.getId()) != null;
            if (!souled) {
                player.getHeroBag().intoItem(lottery.itemDobj);
            } else {
                Soul soul = ((HeroItemDobj) lottery.itemDobj).getSoul();
                player.getBag().intoItem(soul.getItemDobj(), soul.getCount());
            }
        } else {
            int prob = lotteryCountRandom.nextInt(100);
            if (tuhaoed) {
                if (lottery.itemDobj instanceof SoulItemDobj) {
                    if (prob < 60) {
                        count = 3;
                    } else if (count < 95) {
                        count = 5;
                    } else {
                        count = 10;
                    }
                } else {
                    if (prob < 70) {
                        count = 1;
                    } else if (count < 99) {
                        count = 2;
                    } else {
                        count = 4;
                    }
                }
            } else if (diamond) { // 钻石抽卡
                if (lottery.itemDobj instanceof SoulItemDobj) {
                    if (prob < 60) {
                        count = 3;
                    } else if (count < 90) {
                        count = 4;
                    } else {
                        count = 5;
                    }
                } else {
                    if (prob < 70) {
                        count = 1;
                    } else if (count < 99) {
                        count = 2;
                    } else {
                        count = 3;
                    }
                }
            } else { // 金币抽卡
                if (prob < 70) {
                    count = 1;
                } else if (count < 99) {
                    count = 2;
                } else {
                    count = 3;
                }
            }

            player.getBag().intoItem(lottery.itemDobj, count);
        }
        HeroLottery packet = new HeroLottery();
        packet.setIid(lottery.itemDobj.getId());
        packet.setCount(count);
        packet.setSouled(souled);
        return packet;
    }

    private Lottery diamondSpueLottery(Normal normal) {
        // 如果抽卡次数超过200次则回到第31次继续抽卡
        if (normal.getDiamondLotteryCount() > 200) {
            normal.setDiamondLotteryCount(31);
        }

        Lottery lottery;
        if (normal.getDiamondLotteryCount() <= 30) {
            lottery = spueLottery(diamondRandom, diamondLotteries, diamonRandomFactor);
        } else if (normal.getDiamondLotteryCount() <= 60) {
            lottery = spueLottery(diamondRandom, diamondLotteries2, diamonRandomFactor2);
        } else if (normal.getDiamondLotteryCount() <= 90) {
            lottery = spueLottery(diamondRandom, diamondLotteries3, diamonRandomFactor3);
        } else if (normal.getDiamondLotteryCount() <= 130) {
            lottery = spueLottery(diamondRandom, diamondLotteries4, diamonRandomFactor4);
        } else {
            lottery = spueLottery(diamondRandom, diamondLotteries5, diamonRandomFactor5);
        }

        normal.setDiamondLotteryCount(normal.getDiamondLotteryCount() + 1);
        return lottery;
    }

    private Lottery spueLottery(FastRandom ran, List<Lottery> lotteries, int factor) {
        int prob = ran.nextInt(factor);
        for (Lottery lo : lotteries) {
            if (prob <= lo.prob) {
                return lo;
            }
        }
        return lotteries.get(ran.nextInt(lotteries.size()));
    }

    private void parseLotteryDatas(JSONArray datas, List<Lottery> target,
            List<Lottery> targetHeroItems, String probKey) {

        for (int i = 0; i < datas.size(); i++) {
            JSONObject json = datas.getJSONObject(i);
            Lottery lottery =
                    new Lottery(itemProvider.getItem(json.getString("iid")),
                            json.getIntValue(probKey));
            target.add(lottery);

            if (targetHeroItems != null) {
                if (lottery.itemDobj instanceof HeroItemDobj) {
                    targetHeroItems.add(lottery);
                }
            }
        }
    }

    private int calibrateLotteryProb(List<Lottery> lotteries) {
        Lottery prev = null;
        for (Lottery lottery : lotteries) {
            if (prev == null) {
                prev = lottery;
                continue;
            }

            lottery.prob += prev.prob;
            prev = lottery;
        }

        Lottery last = lotteries.get(lotteries.size() - 1);
        return last.prob;
    }

    private double calibrateLotteryProb2(List<TuhaoWeekLottery> lotteries) {
        TuhaoWeekLottery prev = null;
        for (TuhaoWeekLottery lottery : lotteries) {
            if (prev == null) {
                prev = lottery;
                continue;
            }

            lottery.prob += prev.prob;
            prev = lottery;
        }

        TuhaoWeekLottery last = lotteries.get(lotteries.size() - 1);
        return last.prob;
    }

    private class Lottery {

        ItemDobj itemDobj;
        int prob;
        boolean isHero;

        Lottery() {}

        Lottery(JSONObject json) {
            itemDobj = itemProvider.getItem(json.getString("iid"));
            prob = json.getIntValue("prob");
            isHero = (itemDobj instanceof HeroItemDobj);
        }

        Lottery(ItemDobj itemDobj, int prob) {
            this.itemDobj = itemDobj;
            this.prob = prob;
            isHero = (itemDobj instanceof HeroItemDobj);
        }

    }

    private class TuhaoWeekLottery extends Lottery {

        double prob1;
        double prob2;
        double prob3;
        int maxNum;

        TuhaoWeekLottery(JSONObject json) {
            itemDobj = itemProvider.getItem(json.getString("#item.id"));
            prob1 = json.getDoubleValue("prob1");
            prob2 = json.getDoubleValue("prob2");
            prob3 = json.getDoubleValue("prob3");
            maxNum = json.getIntValue("maxNum");
            isHero = (itemDobj instanceof HeroItemDobj);
        }
    }

    private class TuhaoLottery extends Lottery {

        TuhaoLottery(JSONObject json) {
            itemDobj = itemProvider.getItem(json.getString("#item.id"));
            prob = json.getIntValue("prob");
            isHero = (itemDobj instanceof HeroItemDobj);
        }
    }

    public Rabbet start(int expAdd, HeroItem heroItem, String eid, Rabbet rabbet) {
        Player player = SessionUtils.getPlayer();
        int counts = player.getNormal().getDailyTask().getTaskEnchants();
        if (counts >= 0) {
            player.getNormal().getDailyTask().setTaskEnchants(counts + 1);
        }
        int exp = 0;
        expAdd += rabbet.getExp();
        int level = rabbet.getLevel();
        int expSum = rabbet.getExpSum();
        Herofactor herofactor = enchantStar(eid);

        if (levelLimit(level, herofactor.getLevel())
                || heroExclusiveProvider.limit(level, eid, heroItem)) {
            return null;
        }

        while (true) {
            if (levelLimit(level, herofactor.getLevel())
                    || heroExclusiveProvider.limit(level, eid, heroItem)) {
                expAdd = 0;
                break;
            }
            exp = herofactor.getExps().get(level);
            if (expAdd < exp) {
                break;
            }
            level++;
            expSum += exp;
            expAdd -= exp;
        }

        if (level != rabbet.getLevel() || exp != rabbet.getExp()) {
            rabbet.setExpSum(expSum + expAdd - rabbet.getExp());
        }

        rabbet.setExp(expAdd);
        rabbet.setLevel(level);
        counting(rabbet, eid, herofactor.getFactorNamber());
        return rabbet;
    }

    private void counting(Rabbet rabbet, String eid, double d) {
        int lv = rabbet.getLevel();
        EquipmentItemDobj equip = itemProvider.getItem(eid);

        double propScore = calculatorProps(equip, enchantPropScoreFactor) * d;
        double weightScore = calculatorProps(equip, enchantWeightScoreFactor);

        double f = 0;
        if (equip.getTong() != 0 || equip.getWu() != 0 || equip.getZhi() != 0) {
            f = Typhons.getDouble("typhon.domain.hero.factorOne", 1);
        }

        if (equip.getTong() != 0 && equip.getWu() != 0 || equip.getWu() != 0 && equip.getZhi() != 0
                || equip.getZhi() != 0 && equip.getTong() != 0) {
            f = Typhons.getDouble("typhon.domain.hero.factorTwo", 1.2);
        }

        if (equip.getZhi() != 0 && equip.getTong() != 0 && equip.getWu() != 0) {
            f = Typhons.getDouble("typhon.domain.hero.factorThree", 1.5);
        }

        rabbet.setTong((int) Math.ceil(propScore * equip.getTong()
                * enchantWeightScoreFactor.getTongUp() / weightScore
                / enchantPropScoreFactor.getTongUp() * f)
                * lv);
        rabbet.setWu((int) Math.ceil(propScore * equip.getWu() * enchantWeightScoreFactor.getWuUp()
                / weightScore / enchantPropScoreFactor.getWuUp() * f)
                * lv);
        rabbet.setZhi((int) Math.ceil(propScore * equip.getZhi()
                * enchantWeightScoreFactor.getZhiUp() / weightScore
                / enchantPropScoreFactor.getZhiUp() * f)
                * lv);
        rabbet.setAtk((int) Math.ceil(propScore * equip.getAtk()
                * enchantWeightScoreFactor.getAtkUp() / weightScore
                / enchantPropScoreFactor.getAtkUp())
                * lv);
        rabbet.setDef((int) Math.ceil(propScore * equip.getDef()
                * enchantWeightScoreFactor.getDefUp() / weightScore
                / enchantPropScoreFactor.getDefUp())
                * lv);
        rabbet.setMatk((int) Math.ceil(propScore * equip.getMatk()
                * enchantWeightScoreFactor.getMatkUp() / weightScore
                / enchantPropScoreFactor.getMatkUp())
                * lv);
        rabbet.setMdef((int) Math.ceil(propScore * equip.getMdef()
                * enchantWeightScoreFactor.getMdefUp() / weightScore
                / enchantPropScoreFactor.getMdefUp())
                * lv);
        rabbet.setHp((int) Math.ceil(propScore * equip.getHp() * enchantWeightScoreFactor.getHpUp()
                / weightScore / enchantPropScoreFactor.getHpUp())
                * lv);
        rabbet.setCritMagn((int) Math.ceil((propScore * equip.getCritMagn()
                * enchantWeightScoreFactor.getCritMagnUp() / weightScore / enchantPropScoreFactor
                .getCritMagnUp())) * lv);
        rabbet.setCritRate((int) Math.ceil((propScore * equip.getCritRate()
                * enchantWeightScoreFactor.getCritRateUp() / weightScore / enchantPropScoreFactor
                .getCritRateUp())) * lv);
        rabbet.setDecritRate((int) Math.ceil((propScore * equip.getDecritRate()
                * enchantWeightScoreFactor.getDecritRateUp() / weightScore / enchantPropScoreFactor
                .getDecritRateUp())) * lv);
        rabbet.setDeparryRate((int) Math.ceil((propScore * equip.getDeparryRate()
                * enchantWeightScoreFactor.getDeparryRateUp() / weightScore / enchantPropScoreFactor
                .getDeparryRateUp()))
                * lv);
        rabbet.setParryRate((int) Math.ceil((propScore * equip.getParryRate()
                * enchantWeightScoreFactor.getParryRateUp() / weightScore / enchantPropScoreFactor
                .getParryRateUp())) * lv);
        rabbet.setParryValue((int) Math.ceil(propScore * equip.getParryValue()
                * enchantWeightScoreFactor.getParryValueUp() / weightScore
                / enchantPropScoreFactor.getParryValueUp())
                * lv);
    }

    private double calculatorProps(EquipmentItemDobj equip, EnchantFactor enchant) {
        double sumNumber = 0;
        sumNumber += equip.getTong() * enchant.getTongUp();
        sumNumber += equip.getWu() * enchant.getWuUp();
        sumNumber += equip.getZhi() * enchant.getZhiUp();
        sumNumber += equip.getAtk() * enchant.getAtkUp();
        sumNumber += equip.getDef() * enchant.getDefUp();
        sumNumber += equip.getMatk() * enchant.getMatkUp();
        sumNumber += equip.getMdef() * enchant.getMdefUp();
        sumNumber += equip.getHp() * enchant.getHpUp();
        sumNumber += equip.getCritRate() * enchant.getCritRateUp();
        sumNumber += equip.getCritMagn() * enchant.getCritMagnUp();
        sumNumber += equip.getDecritRate() * enchant.getDecritRateUp();
        sumNumber += equip.getDeparryRate() * enchant.getDeparryRateUp();
        sumNumber += equip.getParryRate() * enchant.getParryRateUp();
        sumNumber += equip.getParryValue() * enchant.getParryValueUp();
        return sumNumber;
    }

    private Herofactor enchantStar(String eid) {
        Herofactor herofactor = new Herofactor();
        EquipmentItemDobj equip = itemProvider.getItem(eid);
        starEnum starenum = Herofactor.starEnum.valueOf(equip.getStar().toString());
        String str = null;
        int level = 0;
        JSONObject object;
        switch (starenum) {
            case X1:
                herofactor
                        .setFactorNamber(Typhons.getDouble("typhon.domain.hero.X1.enchant", 0.75));
                str = "enchantX1Exp";
                level = 1;
                break;
            case X2:
                herofactor
                        .setFactorNamber(Typhons.getDouble("typhon.domain.hero.X2.enchant", 0.45));
                str = "enchantX2Exp";
                level = 2;
                break;
            case X3:
                herofactor
                        .setFactorNamber(Typhons.getDouble("typhon.domain.hero.X3.enchant", 0.35));
                str = "enchantX3Exp";
                level = 3;
                break;
            case X4:
                herofactor.setFactorNamber(Typhons.getDouble("typhon.domain.hero.X3.enchant", 0.3));
                str = "enchantX4Exp";
                level = 4;
                break;
            case X5:
                herofactor
                        .setFactorNamber(Typhons.getDouble("typhon.domain.hero.X5.enchant", 0.27));
                str = "enchantX5Exp";
                level = 5;
                break;
        }
        for (int i = 0; i < level; i++) {
            object = expLeveArray.getJSONObject(i);
            herofactor.addExps(object.getIntValue(str));
        }
        herofactor.setLevel(level);
        return herofactor;
    }

    private boolean levelLimit(int level, int levelLimit) {
        return (level >= levelLimit);
    }

    private static class LotteryLimit {

        int maxSoul;
        int maxHero;

        int soul;
        int hero;

        LotteryLimit(int maxSoul, int maxHero) {
            this.maxSoul = maxSoul;
            this.maxHero = maxHero;
        }

        boolean isMaxSoul() {
            return (maxSoul != -1 && soul >= maxSoul);
        }

        boolean isMaxHero() {
            return (maxHero != -1 && hero >= maxHero);
        }
    }
}
