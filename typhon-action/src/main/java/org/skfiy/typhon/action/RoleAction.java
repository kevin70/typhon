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
package org.skfiy.typhon.action;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.PacketRole;
import org.skfiy.typhon.repository.RoleRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class RoleAction {
    
    private static final Logger LOG = LoggerFactory.getLogger(RoleAction.class);
    
    @Inject
    private RoleRepository roleReposy;
    @Inject
    private RoleProvider roleProvider;

    /**
     * 
     * @param packet 
     */
    @Action(Namespaces.ROLE_CREATE)
    public void create(PacketRole packet) {
        if (StringUtils.isEmpty(packet.getName())
                || StringUtils.containsWhitespace(packet.getName())) {
            throw new IllegalArgumentException(
                    "name is null, non string or contains whitespace");
        }
        
        String lock = (packet.getName() + "@create-role").intern();
        synchronized (lock) {
            Session session = SessionContext.getSession();
            if (roleReposy.existsName(packet.getName())) {
                LOG.debug("exists role name [{}]", packet.getName());
                PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
                session.write(error);
                return;
            }
            
            roleProvider.create(packet.getName());
            // 角色创建成功
//            Packet result = SingleValue.createResult(packet, SingleValue.SUCCESS);
//            result.setNs(Namespaces.SINGLE_VAL);
//            result.setType(Packet.Type.result);
//            session.write(result);
        }
        
    }

}
