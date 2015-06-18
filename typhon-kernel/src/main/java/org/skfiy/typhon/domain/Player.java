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
@JSONType(ignores = {"session", "invisible"})
public class Player extends Packet implements Changeable {

    private Session session;

    private Role role;
    private Normal normal;
    private Bag bag;
    // 英雄包
    private Bag heroBag;
    
    private Invisible invisible;

    /**
     *
     * @return
     */
    public Session getSession() {
        return session;
    }

    /**
     * 
     * @param session 
     */
    public void setSession(Session session) {
        this.session = session;
    }

    public Role getRole() {
        return role;
    }

    /**
     *
     * @param role
     */
    public void setRole(Role role) {
        this.role = role;
        this.role.set(this, "role");
    }

    /**
     *
     * @return
     */
    public Normal getNormal() {
        return normal;
    }

    /**
     *
     * @param normal
     */
    public void setNormal(Normal normal) {
        this.normal = normal;
        this.normal.set(this, "normal");
    }

    /**
     *
     * @return
     */
    public Bag getBag() {
        return bag;
    }

    /**
     *
     * @param bag
     */
    public void setBag(Bag bag) {
        this.bag = bag;
        this.bag.set(this, "bag");
    }

    /**
     *
     * @return
     */
    public Bag getHeroBag() {
        return heroBag;
    }

    /**
     *
     * @param heroBag
     */
    public void setHeroBag(Bag heroBag) {
        this.heroBag = heroBag;
        this.heroBag.set(this, "heroBag");
    }

    /**
     * 
     * @return 
     */
    public Invisible getInvisible() {
        return invisible;
    }

    /**
     * 
     * @param invisible 
     */
    public void setInvisible(Invisible invisible) {
        this.invisible = invisible;
    }

    @Override
    public Changeable parent() {
        return null;
    }

    @Override
    public void set(Changeable parent, String parentPropertyName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String parentPropertyName() {
        return null;
    }

    @Override
    public Player player() {
        return this;
    }
}
