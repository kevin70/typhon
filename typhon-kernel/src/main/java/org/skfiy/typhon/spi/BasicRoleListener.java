/*
 * Copyright 2013 The Skfiy Open Association.
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
package org.skfiy.typhon.spi;

import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.repository.RoleRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class BasicRoleListener extends AbstractRoleListener {

    @Inject
    private RoleRepository roleReposy;
    @Inject
    private Set<RoleDatable> roleDatables;

    @Override
    public void roleCreated(Role role) {
        Player player = new Player();
        player.setRole(role);

        // 首次初始化角色信息
        sendPlayerInfo(player);
    }

    @Override
    public void roleLoaded(Role role) {
        Player player = new Player();
        player.setRole(role);
        
        RoleData roleData = roleReposy.loadRoleData(role.getRid());
        for (RoleDatable rd : roleDatables) {
            rd.deserialize(roleData, player);
        }

        sendPlayerInfo(player);
    }

    @Override
    public void roleUnloaded(Role role) {
        RoleData roleData = new RoleData();
        for (RoleDatable rd : roleDatables) {
            rd.serialize(SessionUtils.getPlayer(), roleData);
        }
        
        roleReposy.update(roleData);
    }
    
    private void sendPlayerInfo(Player player) {
        Session session = SessionContext.getSession();
        session.setAttribute(SessionUtils.ATTR_PLAYER, player);
        
        // send player
        player.setNs(Namespaces.NS_PLAYER_INF);
        player.setType(Packet.Type.set);
        session.write(player);
    }
}
