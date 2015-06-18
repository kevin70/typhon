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

import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.packet.GeneralPacket;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.PvpPacket;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.spi.pvp.PvpProvider;
import org.skfiy.typhon.spi.pvp.PvpStoreProvider;
import org.skfiy.typhon.spi.war.WarProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class PvpAction {

    @Inject
    private PvpProvider pvpProvider;
    @Inject
    private WarProvider warProvider;
    @Inject
    private PvpStoreProvider pvpStoreProvider;

    @Action(Namespaces.SEARCH_SUCCOR)
    public void searchSuccor(Packet packet) {
        warProvider.searchSuccor(packet);
    }

    @Action(Namespaces.PVP_SEARCH_RIVALS)
    public void searchRivals(Packet packet) {
        pvpProvider.searchRivals(packet);
    }

    @Action(Namespaces.PVP_CHOOSE_RIVAL)
    public void chooseRival(PvpPacket packet) {
        pvpProvider.chooseRival(packet);
    }
    
    @Action(Namespaces.PVP_BUY_COUNT)
    public void buyCount(Packet packet) {
        pvpProvider.buyCount(packet);
    }
        
    @Action(Namespaces.PVP_REFRESH_CD)
    public void refreshCd(Packet packet) {
        pvpProvider.refreshCd(packet);
    }
    
    @Action(Namespaces.PVP_LOAD_RANKING_LIST)
    public void loadRankingList(SingleValue packet) {
        SingleValue result = SingleValue.createResult(packet, pvpProvider.loadRankingList());
        result.setNs(packet.getNs());
        SessionContext.getSession().write(result);
    }
    
    @Action(Namespaces.PVP_PLAYBACK)
    public void playback(SingleValue packet) {
        pvpProvider.playback(packet);
    }
    
    //==============================================================================================
    @Action(Namespaces.PVP_STORE_REFRESH)
    public void storeRefresh(SingleValue packet) {
        pvpStoreProvider.refreshCommodity(packet);
    }

    @Action(Namespaces.PVP_BUY_COMMODITY)
    public void buyStore(SingleValue packet) {
        pvpStoreProvider.buyCommodities(packet);
    }
    @Action(Namespaces.HERO_PROPERTIES)
    public void heroProperties(GeneralPacket packet) {
        pvpProvider.showHero(packet);
    }
}
