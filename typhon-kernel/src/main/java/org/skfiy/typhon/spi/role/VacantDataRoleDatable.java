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
package org.skfiy.typhon.spi.role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.inject.Inject;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.HeroProperty;
import org.skfiy.typhon.domain.ITroop;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.domain.Troop;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.spi.RoleProvider;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author Administrator
 */
public class VacantDataRoleDatable implements RoleDatable {

    @Inject
    private RoleProvider roleProvider;
    private final Cache<Integer, VacantData> cache;

    public VacantDataRoleDatable() {
        cache = Caching.getCacheManager().getCache("__roleVacantDataCache");
    }

    @Override
    public void initialize(Player player) {}

    @Override
    public void serialize(Player player, RoleData roleData) {
        Normal normal = player.getNormal();

        VacantData vacantData = new VacantData();

        vacantData.setRid(player.getRole().getRid());
        vacantData.setName(player.getRole().getName());
        vacantData.setLevel(player.getRole().getLevel());
        vacantData.setFriendSize(normal.getFriends().size());
        vacantData.setAvatar(normal.getAvatar());
        vacantData.setSocietyId(normal.getSocietyId());
        vacantData.setSocietyName(normal.getSocietyName());
        vacantData.setAvatarBorder(normal.getAvatarBorder());
        vacantData.setCaptain(normal.getFightGroup(normal.getLastFidx()).getCaptain());
        vacantData.setAidReceiveCounts(normal.getAidReceiveCounts());
        vacantData.setVipLevel(normal.getVipLevel());
        vacantData.setLastFidx(normal.getLastFidx());
        vacantData.setLastLogoutTime(normal.getLastLogoutTime());
        roleProvider.removeSuccor(normal);

        // ======================军营强化=============================================================
        List<Troop> troops = new ArrayList<>(ITroop.MAX_TROOP_SIZE);
        for (Troop t : normal.getTroops()) {
            troops.add((Troop) t.clone());
        }
        vacantData.setTroops(troops);
        // ======================军营强化=============================================================

        // 设置武将的ID
        vacantData.setPvpSuccorIid(normal.getFightGroup(3).getSuccorIid());

        buildVacantHeroData(normal, vacantData);

        cache.put(vacantData.getRid(), vacantData);
        roleData.setVacantData(JSON.toJSONString(vacantData));
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {}

    private void buildVacantHeroData(Normal normal, VacantData vacantData) {
        String[][] fightGroups =
                new String[Typhons.getInteger("typhon.spi.fightGroupCount")][Typhons
                        .getInteger("typhon.spi.fightGroup.heroCount")];

        int i = 0;
        Set<HeroItem> heroItems = new HashSet<>();
        for (FightGroup fightGroup : normal.getFightGroups()) {
            heroItems.addAll(Arrays.asList(fightGroup.getHeroItems()));
            fightGroups[i++] = fightGroup.getHeroItemIds();

            if (fightGroup.getSuccor() > 0 && fightGroup.getSuccorIid() != null) {
                Node node = normal.player().getHeroBag().findNode(fightGroup.getSuccor());
                heroItems.add((HeroItem) node.getItem());
            }
        }

        for (HeroItem heroItem : heroItems) {
            vacantData.addHeroProperty(newHeroProperty(heroItem));
        }

        vacantData.setFightGroups(fightGroups);
    }

    private HeroProperty newHeroProperty(HeroItem heroItem) {
        HeroProperty heroProperty = new HeroProperty();

        heroProperty.setLevel(heroItem.getLevel());
        heroProperty.setLadder(heroItem.getLadder());
        heroProperty.setExp(heroItem.getExp());
        heroProperty.setRabbets(heroItem.getRabbets());
        heroProperty.setExtraTong(heroItem.getExtraTong());
        heroProperty.setExtraWu(heroItem.getExtraWu());
        heroProperty.setExtraZhi(heroItem.getExtraZhi());
        heroProperty.setExtraAtk(heroItem.getExtraAtk());
        heroProperty.setExtraDef(heroItem.getExtraDef());
        heroProperty.setExtraMatk(heroItem.getExtraMatk());
        heroProperty.setExtraMdef(heroItem.getExtraMdef());
        heroProperty.setExtraHp(heroItem.getExtraHp());
        heroProperty.setExtraParryRate(heroItem.getExtraParryRate());
        heroProperty.setExtraParryValue(heroItem.getExtraParryValue());
        heroProperty.setExtraCritRate(heroItem.getExtraCritRate());
        heroProperty.setExtraDecritRate(heroItem.getExtraDecritRate());
        heroProperty.setExtraDeparryRate(heroItem.getExtraDeparryRate());
        heroProperty.setExtraCritMagn(heroItem.getExtraCritMagn());
        heroProperty.setId(heroItem.getId());
        heroProperty.setPowerGuess(heroItem.getPowerGuess());
        heroProperty.setRace(heroItem.getRace());
        heroProperty.setStar(heroItem.getStar());
        heroProperty.setWeaponsBuild(heroItem.getWeaponsBuild());
        heroProperty.setWeaponsEnchant(heroItem.getWeaponsEnchant());
        heroProperty.setWeaponsRabbets(heroItem.getWeaponsRabbets());
        heroProperty.setWeaponsStreng(heroItem.getWeaponsStreng());
        return heroProperty;
    }
}
