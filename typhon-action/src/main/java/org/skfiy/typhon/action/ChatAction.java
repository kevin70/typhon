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
package org.skfiy.typhon.action;

import javax.inject.Inject;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.PacketChatMessage;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.society.Member;
import org.skfiy.typhon.spi.society.Society;
import org.skfiy.typhon.spi.society.SocietyProvider;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class ChatAction {

    @Inject
    private SessionManager sessionManager;
    @Inject
    private SocietyProvider societyProvider;

    @Action(Namespaces.CHAT_MSG)
    public void send(PacketChatMessage.Sending message) {
        Player player = SessionUtils.getPlayer();
        Role role = player.getRole();

        switch (message.getTo()) {
            case PacketChatMessage.WORLD_CHANNEL: {
                Normal normal = player.getNormal();
                if (normal.getHornNum() > 0) {
                    normal.setHornNum(normal.getHornNum() - 1);
                } else {
                    JSONObject object = new JSONObject();
                    object.put("place", "ChatMessage");
                    object.put("sendPlayer", message.getToName());
                    SessionUtils.decrementDiamond(
                            Typhons.getInteger("typhon.spi.chat.world.depletionDiamond"),object.toString());
                }

                for (Session otherSession : sessionManager.findSessions()) {
                    if (player.getSession() != otherSession) {
                        send(PacketChatMessage.WORLD_CHANNEL, role, otherSession, message);
                    }
                }

                break;
            }
            case PacketChatMessage.GUILD_CHANNEL: {
                Normal normal = player.getNormal();
                if (normal.getSocietyId() <= 0) {
                    return;
                }

                Society society = societyProvider.findBySid(normal.getSocietyId());
                Session otherSession;
                for (Member member : society.getMembers()) {
                    if (member.getRid() == role.getRid()) {
                        continue;
                    }

                    otherSession = sessionManager.getSession(member.getRid());
                    if (SessionUtils.isSessionAvailable(otherSession)) {
                        send(PacketChatMessage.GUILD_CHANNEL, role, otherSession, message);
                    }
                }
                break;
            }
            default: {
                Session otherSession = sessionManager.getSession(message.getTo());
                send(PacketChatMessage.PRIVATE_CHANNEL, role, otherSession, message);
            }
        }
    }

    private void send(int channel, Role role, Session otherSession,
            PacketChatMessage.Sending message) {
        if (otherSession.isAvailable()) {
            otherSession.write(new PacketChatMessage.Receiving(channel, role.getRid(),
                    role.getName(), message.getMsg(), role.player().getNormal().getAvatar(),
                    role.player().getNormal().getAvatarBorder(), role.getLevel()));
        }
    }

}
