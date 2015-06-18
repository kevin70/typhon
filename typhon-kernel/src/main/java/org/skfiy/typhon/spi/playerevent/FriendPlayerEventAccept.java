package org.skfiy.typhon.spi.playerevent;

import javax.inject.Inject;

import org.skfiy.typhon.domain.Friend;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.role.PlayerEventBean;

import com.alibaba.fastjson.JSONObject;

public class FriendPlayerEventAccept implements IPlayerEvent<PlayerEventBean> {

    @Override
    public String getEventName() {
        return IncidentConstants.EVENT_FRIEND_ACCEPTED;
    }

    @Inject
    private RoleProvider roleProvider;
    @Inject
    private SessionManager sessionManager;

    @Override
    public void invoke(PlayerEventBean bean) {
        int rid = JSONObject.parseObject(bean.getIncident().getData(), Friend.class).getRid();

        Session beSeesion = sessionManager.getSession(rid);
        Friend friend = new Friend();
        
        if (beSeesion != null) {
            Player beplayer = SessionUtils.getPlayer(beSeesion);
            friend = roleProvider.integrationFriend(beplayer);
        } else {
            VacantData vacantData = roleProvider.loadVacantData(rid);
            friend =
                    new Friend(rid, vacantData.getName(), vacantData.getLevel(),
                            roleProvider.findHeroFighting(rid), roleProvider.primaryHeroId(rid),
                            vacantData.getAvatar(), vacantData.getAvatarBorder(),
                            vacantData.getSocietyName());
        }
        bean.getPlayer().getNormal().addFriend(friend);
    }

    @Override
    public boolean isDeletable() {
        return true;
    }
}
