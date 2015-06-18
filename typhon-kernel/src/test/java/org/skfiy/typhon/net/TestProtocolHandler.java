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
package org.skfiy.typhon.net;

import com.alibaba.fastjson.JSON;
import java.nio.charset.StandardCharsets;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionConstants;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TestProtocolHandler extends JsonProtocolHandler {

    @Override
    public void handle(Session session, byte[] nsbs, byte[] datas) {
        // 保存Session至上下文
        _local_session.set(session);

        String ns = new String(nsbs, StandardCharsets.UTF_8);
        Packet packet = JSON.parseObject(datas, dispatcherFactory.getPacketClass(ns));
        session.setAttribute(SessionConstants.ATTR_CONTEXT_PACKET, packet);
        dispatcherFactory.getDispatcher().dispatch(ns, packet);
        session.removeAttribute(SessionConstants.ATTR_CONTEXT_PACKET);
    }

}
