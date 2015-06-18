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
package org.skfiy.typhon.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.ArrayUtils;
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.packet.DoubleValue;
import org.skfiy.typhon.packet.EnchantPacket;
import org.skfiy.typhon.packet.ExclusivePacket;
import org.skfiy.typhon.packet.HeroFightGroup;
import org.skfiy.typhon.packet.HeroRabbet;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.hero.HeroExclusiveProvider;
import org.skfiy.typhon.spi.hero.HeroProvider;
import org.skfiy.typhon.spi.ranking.UpdateRankingList;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class HeroAction {

    @Inject
    private HeroProvider heroProvider;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private HeroExclusiveProvider heroExclusiveProvider;

    @Inject
    private UpdateRankingList updateRankingLists;

    @Action(Namespaces.HERO_LOAD)
    public void load(HeroFightGroup packet) {
        Session session = SessionContext.getSession();
        Player player = SessionUtils.getPlayer();
        FightGroup fightGroup = player.getNormal().getFightGroup(packet.getGidx());

        // 不能在同一个组重复装载英雄
        if (ArrayUtils.indexOf(fightGroup.getHeroPoses(), packet.getPos()) >= 0
                || packet.getPos() == fightGroup.getSuccor()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.not_acceptable);
            error.setText("Repeat hero");
            session.write(error);
            return;
        }

        Node node = player.getHeroBag().findNode(packet.getPos());
        if (node == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("Not found hero");
            session.write(error);
            return;
        }

        if (packet.getIdx() < 0) {
            fightGroup.setSuccor(packet.getPos());
            fightGroup.setSuccorIid(node.getItem().getId());
        } else {
            fightGroup.setHero(packet.getIdx(), node);
        }
        if (packet.getGidx() == player.getNormal().getLastFidx()) {
            updateRankingLists.updatePowerguessRanking();
            roleProvider.updateInformation();
        }

        // 通知客户端操作成功
        session.write(SingleValue.createResult(packet, SingleValue.SUCCESS));
    }

    /**
     *
     * @param packet
     */
    @Action(Namespaces.HERO_RABBET)
    public void activateRabbet(HeroRabbet packet) {
        heroProvider.activateRabbet(packet);
    }

    @Action(Namespaces.HERO_RABBET_ALL)
    public void activateRabbetAll(HeroRabbet packet) {
        heroProvider.activateRabbetAll(packet);
    }

    @Action(Namespaces.HERO_ENCHEM)
    public void enchantStar(EnchantPacket packet) {
        heroProvider.enchantRise(packet);
    }

    @Action(Namespaces.ENCHEM_ONEC)
    public void enchantOnec(EnchantPacket packet) {
        heroProvider.enchantOnce(packet);
    }

    /**
     *
     * @param packet
     */
    @Action(Namespaces.HERO_UPLADDER)
    public void upgradeLadder(HeroRabbet packet) {
        heroProvider.upgradeLadder(packet);
    }

    @Action(Namespaces.HERO_UPSTAR)
    public void upstar(SingleValue packet) {
        heroProvider.upstar(packet);
    }

    @Action(Namespaces.HERO_BECKON)
    public void beckon(SingleValue packet) {
        heroProvider.beckon(packet);
    }

    /**
     *
     * @param packet
     */
    @Action(Namespaces.HERO_SWAP)
    public void swap(HeroFightGroup packet) {
        Player player = SessionUtils.getPlayer();
        FightGroup fightGroup = player.getNormal().getFightGroup(packet.getGidx());

        if (packet.getIdx() == -1 || packet.getToIdx() == -1) {
            int a = fightGroup.getSuccor();
            if (a <= 0) {
                return;
            }

            int idx;

            if (packet.getIdx() == -1) {
                idx = packet.getToIdx();
            } else {
                idx = packet.getIdx();
            }

            HeroItem heroItem = player.getHeroBag().findNode(fightGroup.getSuccor()).getItem();
            int[] heroPoses = fightGroup.getHeroPoses();
            HeroItem[] heroItems = fightGroup.getHeroItems();

            int b = heroPoses[idx];

            // 设置新的援军位武将
            fightGroup.setSuccor(b);
            fightGroup.setSuccorIid(heroItems[idx].getId());

            heroPoses[idx] = a;
            heroItems[idx] = heroItem;

            fightGroup.setHeroPoses(heroPoses);
            fightGroup.setHeroItems(heroItems);
        } else {
            fightGroup.swap(packet.getIdx(), packet.getToIdx());
        }
        if (packet.getGidx() == player.getNormal().getLastFidx()) {
            roleProvider.updateInformation();
            updateRankingLists.updatePowerguessRanking();
        }
    }

    /**
     *
     * @param packet
     */
    @Action(Namespaces.HERO_CHANGE_CAPTAIN)
    public void changeCaptain(DoubleValue packet) {
        Player player = SessionUtils.getPlayer();
        int gidx = (int) packet.getFirst();
        int i = (int) packet.getSecond();

        if (i < 0 || i > FightGroup.RIGHT_PIONEER_POS) {
            return;
        }

        FightGroup fightGroup = player.getNormal().getFightGroup(gidx);
        fightGroup.setCaptain(i);
    }

    @Action(Namespaces.HERO_LUCK_DRAW_C)
    public void luckyDrawCopper(Packet packet) {
        heroProvider.buyCopperLottery(packet);
    }

    @Action(Namespaces.HERO_LUCK_DRAW_C10)
    public void luckyDrawCopper10(Packet packet) {
        heroProvider.buyCopperLottery10(packet);
    }

    @Action(Namespaces.HERO_LUCK_DRAW_D)
    public void luckyDrawDiamond(Packet packet) {
        heroProvider.buyDiamondLottery(packet);
    }

    @Action(Namespaces.HERO_LUCK_DRAW_D10)
    public void luckyDrawDiamond10(Packet packet) {
        heroProvider.buyDiamondLottery10(packet);
    }

    @Action(Namespaces.HERO_LUCK_DRAW_TUHAO)
    public void luckyDrawTuhao(Packet packet) {
        heroProvider.buyTuhaoLottery(packet);
    }
    
    /**
     * 专属武器.
     */
    @Action(Namespaces.EXCLUSIVE_RABBET)
    public void exclusiveRabbet(SingleValue packet) {
        heroExclusiveProvider.exclusiveRabber(packet);
    }

    @Action(Namespaces.EXCLUSIVE_BUILD)
    public void exclusiveBuild(SingleValue packet) {
        heroExclusiveProvider.exclusiveBuild(packet);
    }

    @Action(Namespaces.EXCLUSIVE_STRENG)
    public void exclusiveStreng(SingleValue packet) {
        heroExclusiveProvider.exclusiveStreng(packet);
    }

    @Action(Namespaces.EXCLUSIVE_ENCHEM)
    public void exclusiveEnchem(EnchantPacket packet) {
        heroExclusiveProvider.exclusiveEnchem(packet);
    }

    @Action(Namespaces.EXCLUSIVE_ENCHEM_ONCE)
    public void exclusiveEnhancementonce(EnchantPacket packet) {
        heroExclusiveProvider.exclusiveEnchemOnce(packet);
    }
    
    @Action(Namespaces.EXCLUSIVE_ENHANCEMENT)
    public void exclusiveEnhancement(ExclusivePacket packet) {
        heroExclusiveProvider.exclusiveWash(packet);
    }
    
    
    @Action(Namespaces.EXCLUSIVE_ENHANCEMENT_SAVE)
    public void exclusiveEnhancementSave(SingleValue packet) {
        heroExclusiveProvider.exclusiveEnhancement2(packet);
    }
}
