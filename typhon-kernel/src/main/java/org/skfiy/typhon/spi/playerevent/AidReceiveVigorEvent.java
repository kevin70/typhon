package org.skfiy.typhon.spi.playerevent;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.role.PlayerEventBean;

public class AidReceiveVigorEvent implements IPlayerEvent<PlayerEventBean> {

    @Override
    public String getEventName() {
        return IncidentConstants.ADI_RECEIVE_VIGOR;
    }

    @Override
    public void invoke(PlayerEventBean obj) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = normal.getAidReceiveCounts().size();

        if (index < Typhons.getInteger("typhon.spi.DailyVigorFromFriend.TotalTimes")) {
            normal.AddAidReceiveCounts(new RecordObject(index++, 0));
        }
    }

    @Override
    public boolean isDeletable() {
        return true;
    }

}
