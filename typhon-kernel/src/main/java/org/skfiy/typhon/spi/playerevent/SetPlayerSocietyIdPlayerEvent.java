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

import javax.inject.Inject;

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.role.PlayerEventBean;
import org.skfiy.typhon.spi.society.SocietyProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class SetPlayerSocietyIdPlayerEvent implements IPlayerEvent<PlayerEventBean> {

    @Inject
    private RoleProvider roleProvider;
    @Inject
    private SocietyProvider societyProvider;

    @Override
    public String getEventName() {
        return IncidentConstants.EVENT_SOCIETY_SET_PLAYER_SOCIETY_ID;
    }

    @Override
    public boolean isDeletable() {
        return true;
    }

    @Override
    public void invoke(PlayerEventBean obj) {
        Player player = SessionUtils.getPlayer();
        int societyId = Integer.valueOf(obj.getIncident().getData());
        obj.getPlayer().getNormal().setSocietyId(societyId);
        if (societyId == 0) {
            player.getNormal().setSocietyName(null);
        } else {
            player.getNormal().setSocietyName(societyProvider.findBySid(societyId).getName());
        }
        roleProvider.updateInformation();
    }

}
