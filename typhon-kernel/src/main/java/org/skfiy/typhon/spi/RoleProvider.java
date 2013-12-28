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
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.repository.RoleRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class RoleProvider {

    private static final Logger LOG = LoggerFactory.getLogger(RoleProvider.class);

    @Inject
    private RoleRepository roleReposy;
    @Inject
    private Set<RoleListener> roleListeners;

    /**
     * 
     * @param name 
     */
    public void create(String name) {
        // save role
        Role role = new Role();
        role.setRid(SessionUtils.getUser().getUid());
        role.setName(name);
        roleReposy.save(role);

        LOG.debug("create role [rid={}, name={}] successful", role.getRid(), role.getName());

        create0(role);
        load0(role);
    }

    /**
     *
     */
    public void preload() {
        Session session = SessionContext.getSession();
        User user = SessionUtils.getUser();

        // 当前存在Player则不从数据库查询角色信息
        Player player = (Player) session.getAttribute(SessionUtils.ATTR_PLAYER);
        if (player != null) {
            load0(player.getRole());
        } else {
            Role role = roleReposy.get(user.getUid());
            if (role == null) {
                session.write(Namespaces.ROLE, "{}");
            } else {
                load0(role);
            }
        }
    }

    /**
     *
     * @param role
     */
    protected void create0(Role role) {
        // fire RoleListener
        for (RoleListener roleListener : roleListeners) {
            roleListener.roleCreated(role);
        }
    }

    /**
     *
     * @param role
     */
    protected void load0(Role role) {
        for (RoleListener roleListener : roleListeners) {
            roleListener.roleLoaded(role);
        }
    }

}
