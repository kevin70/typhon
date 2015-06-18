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
package org.skfiy.typhon.dobj;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class BSaSkill {

    private String id;
    private DamageType damageType;
    private int numOfTargets;
    private int numOfAttacks;
    @JSONField(name = "parameter")
    private double factor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public int getNumOfTargets() {
        return numOfTargets;
    }

    public void setNumOfTargets(int numOfTargets) {
        this.numOfTargets = numOfTargets;
    }

    public int getNumOfAttacks() {
        return numOfAttacks;
    }

    public void setNumOfAttacks(int numOfAttacks) {
        this.numOfAttacks = numOfAttacks;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public enum DamageType {

        AD, AP, MIX
    }

}
