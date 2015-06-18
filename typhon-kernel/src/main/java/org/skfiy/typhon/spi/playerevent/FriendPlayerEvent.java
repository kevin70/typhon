package org.skfiy.typhon.spi.playerevent;

import com.alibaba.fastjson.JSON;
import org.skfiy.typhon.domain.Friend;
import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.IPlayerEvent;
import org.skfiy.typhon.spi.RoleProvider;

import com.alibaba.fastjson.JSONObject;
import java.util.Map;

import javax.inject.Inject;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.repository.IncidentRepository;

import org.skfiy.typhon.spi.role.PlayerEventBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendPlayerEvent implements IPlayerEvent<PlayerEventBean> {

    private static final Logger LOG = LoggerFactory.getLogger(FriendPlayerEvent.class);

    @Inject
    private RoleProvider roleProvider;
    @Inject
    private IncidentRepository incidentReposy;

    @Override
    public String getEventName() {
        return IncidentConstants.EVENT_FRIEND_REQUEST;
    }

    @Override
    public void invoke(PlayerEventBean bean) {
        Player player = bean.getPlayer();
        Incident incident = bean.getIncident();

        Friend friend = JSONObject.parseObject(incident.getData(), Friend.class);
        // 如果已经是好友则不处理
        if (player.getNormal().findFriend(friend.getRid()) != null) {
            LOG.debug("{}: <{}> is friend.", player.getRole().getName(), friend.getName());
            incidentReposy.delete(incident.getPid());
            return;
        }

        if (!(roleProvider.checkFriendLimit(player) && roleProvider.checkFriendLimit(friend
                .getRid()))) {
            LOG.debug("{}/{}: friend size limit.", player.getRole().getName(), friend.getName());
            incidentReposy.delete(incident.getPid());
            return;
        }
        
        if (incident.getCreationTime() + 5 * 24 * 60 * 60 * 1000 <=System.currentTimeMillis()) {
            LOG.debug("{}/{}:friendRequest is overdue", player.getRole().getName(),
                    friend.getName());
            incidentReposy.delete(incident.getPid());
            return;
        }
        
        JSONObject result = new JSONObject();
        result.put("data", incident.getJSONData());
        result.put("pid", incident.getPid());
        
        Map<Integer, Incident> friendMsgMap = roleProvider.getFriendMsgSessionMap(player.getSession());
        friendMsgMap.put(incident.getPid(), incident);

        player.getSession().write(Namespaces.FRIEND_ADD, result);
    }

    @Override
    public boolean isDeletable() {
        return false;
    }
}
