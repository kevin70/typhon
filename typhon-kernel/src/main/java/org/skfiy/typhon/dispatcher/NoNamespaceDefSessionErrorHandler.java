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
package org.skfiy.typhon.dispatcher;

import org.skfiy.typhon.net.SessionErrorHandler;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class NoNamespaceDefSessionErrorHandler implements SessionErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SessionErrorHandler.class);

    @Override
    public Class<?> getErrorType() {
        return NoNamespaceDefException.class;
    }

    @Override
    public void handleError(Session session, Throwable t) {
        Packet contextPacket = (Packet) session.getAttribute(SessionConstants.ATTR_CONTEXT_PACKET);
        LOG.debug(contextPacket.toString(), t);
        
        PacketError error = PacketError.createResult(contextPacket,
                PacketError.Condition.item_not_found);
        error.setNs(Namespaces.ERROR);
        error.setText("No namespace def");
        session.write(contextPacket);
    }

}
