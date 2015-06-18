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

import org.skfiy.typhon.Response;
import org.skfiy.typhon.TestConstants;
import org.skfiy.typhon.TestProtocolBase;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.PacketRole;
import org.skfiy.typhon.packet.PvePacket;
import org.testng.Assert;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PveActionTest extends TestProtocolBase{
    
    // @Test
    public void testEnter() {
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
        Assert.assertEquals(resp.getNs(), Namespaces.PLAYER_INFO);
        getDecoderEmbedder().pollAll();
        
        // 发送进入副本消息
        PvePacket packetAttack = new PvePacket();
        packetAttack.setNs(Namespaces.PVE_ENTER);
        packetAttack.setCidx(0);
        packetAttack.setPidx(0);
        packetAttack.setFgidx(0);
        packetAttack.setId(generateId());
        offer(packetAttack);
        
        removalOverMessage();
        // clean data
        cleanRole();
        roleResposy.delete(resp.getData().getJSONObject("role").getIntValue("rid"));
    }
    
}
