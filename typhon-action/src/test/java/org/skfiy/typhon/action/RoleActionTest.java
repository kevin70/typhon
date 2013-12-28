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

import org.skfiy.typhon.Response;
import org.skfiy.typhon.TestConstants;
import org.skfiy.typhon.TestProtocolBase;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.PacketRole;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class RoleActionTest extends TestProtocolBase {

    @Test
    public void create() {
        initUser();
        auth();
        poll();
        
        // 创建角色
        PacketRole packetRole = new PacketRole();
        packetRole.setNs(Namespaces.ROLE_CREATE);
        packetRole.setId(generateId());
        packetRole.setName(TestConstants.ROLE_NAME);
        offer(packetRole);
        
        // 收到创建角色响应信息
        Response resp = poll();
        cleanRole();
        roleResposy.delete(resp.getData().getJSONObject("role").getIntValue("rid"));
        
        Assert.assertEquals(resp.getNs(), Namespaces.PLAYER_INFO);
    }
}
