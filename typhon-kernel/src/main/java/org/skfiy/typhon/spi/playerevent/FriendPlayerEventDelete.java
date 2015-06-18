package org.skfiy.typhon.spi.playerevent;

import org.skfiy.typhon.domain.Friend;
import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.role.PlayerEventBean;

import com.alibaba.fastjson.JSONObject;

public class FriendPlayerEventDelete implements IPlayerEvent<PlayerEventBean> {

    @Override
    public String getEventName() {
        return IncidentConstants.EVENT_FRIEND_DELETEED;
    }

    @Override
    public void invoke(PlayerEventBean bean) {
        int rid = JSONObject.parseObject(bean.getIncident().getData(), Friend.class).getRid();
        bean.getPlayer().getNormal().deleteFriend(rid);
    }

    @Override
    public boolean isDeletable() {
        return true;
    }
}
