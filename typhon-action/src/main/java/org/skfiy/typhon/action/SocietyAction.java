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
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.packet.DoubleValue;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.packet.SocietyBossPacket;
import org.skfiy.typhon.packet.SocietyPacket;
import org.skfiy.typhon.spi.society.SocietyBossProvider;
import org.skfiy.typhon.spi.society.SocietyProvider;
import org.skfiy.typhon.spi.store.SocietyStoreProvider;

/**
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class SocietyAction {

    @Inject
    private SocietyProvider societyProvider;
    @Inject
    private SocietyStoreProvider societyStoreProvider;
    @Inject
    private SocietyBossProvider societyBossProvider;

    @Action(Namespaces.SOCIETY_CREATE)
    public void create(SocietyPacket packet) {
        societyProvider.create(packet);
    }

    @Action(Namespaces.SOCIETY_DISSOLVE)
    public void dissolve(Packet packet) {
        societyProvider.dissolve(packet);
    }

    @Action(Namespaces.SOCIETY_LOAD_LIST)
    public void loadSocieties(SingleValue packet) {
        societyProvider.loadSocieties(packet);
    }

    @Action(Namespaces.SOCIETY_ACCEPT)
    public void accept(SingleValue packet) {
        societyProvider.accept(packet);
    }

    @Action(Namespaces.SOCIETY_REJECT)
    public void reject(SingleValue packet) {
        societyProvider.reject(packet);
    }

    @Action(Namespaces.SOCIETY_APPLY)
    public void apply(SingleValue packet) {
        societyProvider.apply(packet);
    }

    @Action(Namespaces.SOCIETY_KICKOUT)
    public void kickout(SingleValue packet) {
        societyProvider.kickout(packet);
    }

    @Action(Namespaces.SOCIETY_LEAVE)
    public void leave(Packet packet) {
        societyProvider.leave(packet);
    }

    @Action(Namespaces.SOCIETY_LOAD)
    public void load(Packet packet) {
        societyProvider.load(packet);
    }

    @Action(Namespaces.SOCIETY_UPDATE_INFO)
    public void updateInfo(DoubleValue packet) {
        societyProvider.updateInfo(packet);
    }

    @Action(Namespaces.SOCIETY_UPDATE_PERM)
    public void updatePerm(DoubleValue packet) {
        // 更新权限
        societyProvider.updatePerm(packet);
    }

    @Action(Namespaces.SOCIETY_STORE_REFRESH)
    public void StoreRefresh(SingleValue packet) {
        societyStoreProvider.refreshCommodity(packet);
    }

    @Action(Namespaces.SOCIETY_STORE_BUY)
    public void storeBuy(SingleValue packet) {
        societyStoreProvider.buyCommodities(packet);
    }

    @Action(Namespaces.SOCIETY_BOSS_WISH)
    public void societyBossWish(SingleValue packet) {
        societyBossProvider.societyBossWish(packet);
    }

    @Action(Namespaces.GETTING_SOCIETY_BOSSRANKING)
    public void gettingWishInformation(SingleValue packet) {
        societyBossProvider.gettingRanking(packet);
    }

    @Action(Namespaces.SOCIETY_BOSS_ATK)
    public void getSocietyBossAtk(SocietyBossPacket packet) {
        societyBossProvider.pveBosses(packet);
    }

    @Action(Namespaces.GETTING_WISH_INFORMATION)
    public void gettingBossRanking(SingleValue packet) {
        societyBossProvider.gettingWishInformation(packet);
    }

    @Action(Namespaces.RECEIVE_SOCIETY_BOSSREWARD)
    public void receiveSocietyBossReward(SingleValue packet) {
        societyBossProvider.gettingWishInformation(packet);
    }

    @Action(Namespaces.RESET_PVEBOSSCD)
    public void resetPveBossCD(SingleValue packet) {
        societyBossProvider.resetBossCDTime(packet);
    }
}
