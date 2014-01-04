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
package org.skfiy.typhon.spi.role;

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class FinalRoleListener extends AbstractRoleListener {

//    @Override
//    public void roleCreated(Role role) {
//        sendPlayerInfo();
//    }

    @Override
    public void roleLoaded(Role role) {
        sendPlayerInfo();
    }

    private void sendPlayerInfo() {
        Session session = SessionContext.getSession();
        Player player = SessionUtils.getPlayer();

        // send player
        player.setNs(Namespaces.PLAYER_INFO);
        player.setType(Packet.Type.set);
        session.write(player);
    }

}
