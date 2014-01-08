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
package org.skfiy.typhon.domain;

import com.alibaba.fastjson.annotation.JSONType;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.session.Session;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"session"})
public class Player extends Packet {

    private final Session session;
    
    private Role role;
    private Normal normal;
    private Big big;

    /**
     * 
     * @param session 
     */
    public Player(Session session) {
        this.session = session;
    }
    
    /**
     * 
     * @return 
     */
    public Session getSession() {
        return session;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
        this.big.setPlayer(this);
    }

    public Normal getNormal() {
        return normal;
    }

    public void setNormal(Normal normal) {
        this.normal = normal;
    }

    public Big getBig() {
        return big;
    }

    public void setBig(Big big) {
        this.big = big;
        this.big.setPlayer(this);
    }

}
