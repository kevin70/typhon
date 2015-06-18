package org.skfiy.typhon.spi.playerevent;

import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.role.PlayerEventBean;

public class ReceiveInviteRewardEvent implements IPlayerEvent<PlayerEventBean> {
    @Override
    public String getEventName() {
        return IncidentConstants.INVITE_MANAGER;
    }

    public void invoke(PlayerEventBean obj) {
        obj.getPlayer().getInvisible().getInvite()
                .add(Integer.valueOf(obj.getIncident().getData()));
    }

    @Override
    public boolean isDeletable() {
        return true;
    };
}
