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
package org.skfiy.typhon.spi.pve;

import com.alibaba.fastjson.annotation.JSONType;
import org.skfiy.typhon.dobj.MonsterItemDobj;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"parent", "monster"})
public class Member {

    private MonsterItemDobj monster;
    private String[] ais;
    private Step parent;

    public MonsterItemDobj getMonster() {
        return monster;
    }

    public void setMonster(MonsterItemDobj monster) {
        this.monster = monster;
    }

    public String[] getAis() {
        return ais;
    }

    public void setAis(String[] ais) {
        this.ais = ais;
    }

    public Step getParent() {
        return parent;
    }

    public void setParent(Step parent) {
        this.parent = parent;
    }

}
