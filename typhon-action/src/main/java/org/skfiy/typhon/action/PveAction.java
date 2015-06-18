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
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PveCleanPacket;
import org.skfiy.typhon.packet.PvePacket;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.spi.pve.PveProvider;
import org.skfiy.typhon.spi.ranking.HeroStarRankingProvider;
import org.skfiy.typhon.spi.ranking.LevelRankingProvider;
import org.skfiy.typhon.spi.ranking.PowerguessRankingProvider;
import org.skfiy.typhon.spi.ranking.PveDifficultRankingProvider;
import org.skfiy.typhon.spi.ranking.PveRankingProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class PveAction {

    @Inject
    private PveProvider pveProvider;
    @Inject
    private PveRankingProvider pveRankingProvider;
    @Inject
    private LevelRankingProvider levelRankingProvider;
    @Inject
    private PowerguessRankingProvider powerguessRankingProvider;
    @Inject
    private PveDifficultRankingProvider pveDifficultRankingProvider;
    @Inject
    private HeroStarRankingProvider heroStarRankingProvider;

    @Action(Namespaces.PVE_SEARCH_SUCCOR)
    public void searchSuccor(Packet packet) {
        pveProvider.searchSuccor(packet);
    }

    @Action(Namespaces.PVE_LOAD_SUCCOR)
    public void loadSuccorData(SingleValue packet) {
        pveProvider.loadSuccorData(packet);
    }
    
    @Action(Namespaces.PVE_ENTER)
    public void enter(PvePacket packet) {
        pveProvider.enter(packet);
    }

    @Action(Namespaces.PVE_EXIT)
    public void exit(PvePacket packet) {
        pveProvider.exit(packet);
    }

    @Action(Namespaces.PVE_REC_AWARD)
    public void receiveFruitionAward(PvePacket packet) {
        pveProvider.receiveFruitionAward(packet);
    }

    @Action(Namespaces.PVE_CLEAN)
    public void clean(PveCleanPacket packet) {
        pveProvider.cleanOut(packet);
    }

    @Action(Namespaces.PVE_DIFFICULT_RANKING_LIST)
    public void pveDifficultRanking(SingleValue packet) {
        SingleValue result =
                SingleValue.createResult(packet,
                        pveDifficultRankingProvider.loadLevelRankingList(packet));
        result.setNs(packet.getNs());
        SessionContext.getSession().write(result);
    }

    @Action(Namespaces.LEVEL_RANKING_LIST)
    public void levelRanking(SingleValue packet) {
        SingleValue result =
                SingleValue.createResult(packet, levelRankingProvider.loadLevelRankingList(packet));
        result.setNs(packet.getNs());
        SessionContext.getSession().write(result);
    }

    @Action(Namespaces.POWERGUESS_RANKING_LIST)
    public void powerguessRanking(SingleValue packet) {
        SingleValue result =
                SingleValue.createResult(packet,
                        powerguessRankingProvider.loadLevelRankingList(packet));
        result.setNs(packet.getNs());
        SessionContext.getSession().write(result);
    }

    @Action(Namespaces.PVE_RANKING_LIST)
    public void pveRanking(SingleValue packet) {
        SingleValue result =
                SingleValue.createResult(packet, pveRankingProvider.loadLevelRankingList(packet));
        result.setNs(packet.getNs());
        SessionContext.getSession().write(result);
    }

    @Action(Namespaces.HEROSTAR_RANKING_LIST)
    public void heroStarRanking(SingleValue packet) {
        SingleValue result =
                SingleValue.createResult(packet,
                        heroStarRankingProvider.loadLevelRankingList(packet));
        result.setNs(packet.getNs());
        SessionContext.getSession().write(result);
    }

    @Action(Namespaces.PVE_RESETCOUNT)
    public void pveReset(PvePacket packet) {
        pveProvider.resetPveCounts(packet);
    }
    
    @Action(Namespaces.PVE_CLEAN_REC_AWARD)
    public void cleanFruitionAward(PvePacket packet) {
        pveProvider.cleanFruitionAward(packet);
    }
}
