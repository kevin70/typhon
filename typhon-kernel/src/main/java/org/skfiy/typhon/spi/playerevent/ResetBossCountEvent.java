package org.skfiy.typhon.spi.playerevent;

import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.role.PlayerEventBean;

public class ResetBossCountEvent implements IPlayerEvent<PlayerEventBean> {
    @Override
    public String getEventName() {
        return IncidentConstants.RESET_BOSSCOUNT;
    }

    @Override
    public void invoke(PlayerEventBean obj) {
        Normal normal = obj.getPlayer().getNormal();
        normal.setSocietyBossCounts(0);
    }

    @Override
    public boolean isDeletable() {
        return true;
    }
}
