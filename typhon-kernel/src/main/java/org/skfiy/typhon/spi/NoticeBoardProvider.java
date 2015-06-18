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
package org.skfiy.typhon.spi;

import javax.inject.Inject;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.PacketNotice;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class NoticeBoardProvider {

    @Inject
    private SessionManager sessionManager;

    /**
     * 给除自己之外的其它在线玩家发送公告信息.
     *
     * @param notice 协议包
     */
    public void announce(PacketNotice notice) {
        notice.setNs(Namespaces.NOTICE);

        for (Session session : sessionManager.findSessions()) {
            if (session.isAvailable()) {
                session.write(notice);
            }
        }
    }

}
