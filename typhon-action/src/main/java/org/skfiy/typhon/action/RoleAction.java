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
import org.skfiy.typhon.packet.PacketRole;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.util.StringUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class RoleAction {

    @Inject
    private RoleProvider roleProvider;

    @Action(Namespaces.NS_ROLE_CREATE)
    public void create(PacketRole packet) {
        if (StringUtils.isEmpty(packet.getName())) {
            throw new IllegalArgumentException("role name is null or empty string");
        }

        roleProvider.create(packet);
    }

}
