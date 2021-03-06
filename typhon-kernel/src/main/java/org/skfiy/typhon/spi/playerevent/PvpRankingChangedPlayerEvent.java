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
package org.skfiy.typhon.spi.playerevent;

import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.role.PlayerEventBean;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PvpRankingChangedPlayerEvent implements IPlayerEvent<PlayerEventBean> {

    @Override
    public String getEventName() {
        return IncidentConstants.EVENT_PVP_RANKING_CHANGED;
    }

    @Override
    public void invoke(PlayerEventBean obj) {
//        Normal normal = obj.getPlayer().getNormal();
//        Incident incident = obj.getIncident();
//        normal.setPvpRanking(Integer.valueOf(incident.getData()));
    }

    @Override
    public boolean isDeletable() {
        return true;
    }

}
