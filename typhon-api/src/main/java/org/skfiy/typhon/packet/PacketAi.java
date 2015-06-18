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
package org.skfiy.typhon.packet;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PacketAi extends Packet {

    private List<Attack> attacks;
    private String state;
    //=================================================
    private double[] factors;
    private double[] critRates;

    public List<Attack> getAttacks() {
        return attacks;
    }

    public void setAttacks(List<Attack> attacks) {
        if (attacks == null) {
            attacks = new ArrayList<>();
        }

        this.attacks.addAll(attacks);
    }

    public boolean addAttack(Attack attack) {
        if (attacks == null) {
            attacks = new ArrayList<>();
        }

        return this.attacks.add(attack);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double[] getFactors() {
        return factors;
    }

    public void setFactors(double[] factors) {
        this.factors = factors;
    }

    public double[] getCritRates() {
        return critRates;
    }

    public void setCritRates(double[] critRates) {
        this.critRates = critRates;
    }

    public static PacketAi createResult(Packet packet) {
        PacketAi ai = new PacketAi();
        ai.setId(packet.getId());
        ai.setType(Type.rs);
        return ai;
    }

    public static class Attack {

        private String atkType;
        private int atkerIdx;
        private int targetIdx;
        private String skillName;
        private int hurt;

        public String getAtkType() {
            return atkType;
        }

        public void setAtkType(String atkType) {
            this.atkType = atkType;
        }

        public int getAtkerIdx() {
            return atkerIdx;
        }

        public void setAtkerIdx(int idx) {
            this.atkerIdx = idx;
        }

        public int getTargetIdx() {
            return targetIdx;
        }

        public void setTargetIdx(int idx) {
            this.targetIdx = idx;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public int getHurt() {
            return hurt;
        }

        public void setHurt(int hurt) {
            this.hurt = hurt;
        }
    }

}
