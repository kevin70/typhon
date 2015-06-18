package org.skfiy.typhon.spi.playerevent;

import org.skfiy.typhon.domain.Friend;
import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.role.PlayerEventBean;

import com.alibaba.fastjson.JSONObject;

public class UpdateFriendInformationEvent implements IPlayerEvent<PlayerEventBean> {
    
    @Override
    public String getEventName() {
        return IncidentConstants.EVENT_UPDATE_FRIENDS;
    }
    
    @Override
    public boolean isDeletable() {
        return true;
    };
    
    @Override
    public void invoke(PlayerEventBean bean) {
        Player player=SessionUtils.getPlayer();
        Incident incident = bean.getIncident();
        
        Friend befriend = JSONObject.parseObject(incident.getData(), Friend.class);
        for (Friend friend : player.getNormal().getFriends()) {
            
            if (friend.getRid() == befriend.getRid()) {
                if (friend.getAvatar() != befriend.getAvatar()) {
                    friend.setAvatar(befriend.getAvatar());
                }
                if (friend.getAvatarBorder() != befriend.getAvatarBorder()) {
                    friend.setAvatarBorder(befriend.getAvatarBorder());
                }
                if (friend.getLevel() != befriend.getLevel()) {
                    friend.setLevel(befriend.getLevel());
                }
                if (!friend.getName().equals(befriend.getName())) {
                    friend.setName(befriend.getName());
                }
                if (friend.getPowerGuessSum() != befriend.getPowerGuessSum()) {
                    friend.setPowerGuessSum(befriend.getPowerGuessSum());
                }
                if (friend.getSocietyName() != befriend.getSocietyName()) {
                    friend.setSocietyName(befriend.getSocietyName());
                }
            }
        }
    }
}
