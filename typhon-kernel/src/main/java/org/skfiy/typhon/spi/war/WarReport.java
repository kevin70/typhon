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
package org.skfiy.typhon.spi.war;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class WarReport {

    private Entity attackerEntity;
    private Entity defenderEntity;
    private final List<Round> rounds = new ArrayList<>();
    private Effect effect = Effect.W;

    public Entity getAttackerEntity() {
        return attackerEntity;
    }

    public void setAttackerEntity(Entity attackerEntity) {
        this.attackerEntity = attackerEntity;
    }

    public Entity getDefenderEntity() {
        return defenderEntity;
    }

    public void setDefenderEntity(Entity defenderEntity) {
        this.defenderEntity = defenderEntity;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public boolean addRound(Round round) {
        return rounds.add(round);
    }

    public void setRounds(List<Round> rounds) {
        this.rounds.addAll(rounds);
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    /**
     *
     */
    public enum Effect {

        /**
         * win.
         */
        W,
        /**
         * defeat.
         */
        D,
        /**
         * continue.
         */
        C
    }

    /**
     *
     */
    public static class Entity {

        private String roleName;
        private int level;
        private int powerGuess;
        private List<JSONObject> heros;
        private String succorIid;
        // FIXME Test
        private int succorAtk;
        private int succorFury;

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getPowerGuess() {
            return powerGuess;
        }

        public void setPowerGuess(int powerGuess) {
            this.powerGuess = powerGuess;
        }

        public List<JSONObject> getHeros() {
            return heros;
        }

        public void setHeros(List<JSONObject> heros) {
            this.heros = heros;
        }

        public String getSuccorIid() {
            return succorIid;
        }

        public void setSuccorIid(String succorIid) {
            this.succorIid = succorIid;
        }

        public int getSuccorAtk() {
            return succorAtk;
        }

        public void setSuccorAtk(int succorAtk) {
            this.succorAtk = succorAtk;
        }

        public int getSuccorFury() {
            return succorFury;
        }

        public void setSuccorFury(int succorFury) {
            this.succorFury = succorFury;
        }

    }

    /**
     *
     */
    public static class Round {

        // 南方
        private int[] sholdPoints;
        // 北方
        private int[] nholdPoints;
        private final List<Object> sbufDetails = new ArrayList<>();
        private final List<Object> sdetails = new ArrayList<>();

        private final List<Object> nbufDetails = new ArrayList<>();
        private final List<Object> ndetails = new ArrayList<>();

        private Object sjson;
        private Object njson;

        public int[] getSholdPoints() {
            return sholdPoints;
        }

        public void setSholdPoints(int[] sholdPoints) {
            this.sholdPoints = sholdPoints;
        }

        public int[] getNholdPoints() {
            return nholdPoints;
        }

        public void setNholdPoints(int[] nholdPoints) {
            this.nholdPoints = nholdPoints;
        }

        public List<Object> getNbufDetails() {
            return nbufDetails;
        }

        public List<Object> getSdetails() {
            return sdetails;
        }

        public void setSdetails(List<Object> sdetails) {
            this.sdetails.addAll(sdetails);
        }

        public List<Object> getSbufDetails() {
            return sbufDetails;
        }

        public List<Object> getNdetails() {
            return ndetails;
        }

        public void setNdetails(List<Object> ndetails) {
            this.ndetails.addAll(ndetails);
        }

        public Object getSjson() {
            return sjson;
        }

        public void setSjson(Object sjson) {
            this.sjson = sjson;
        }

        public Object getNjson() {
            return njson;
        }

        public void setNjson(Object njson) {
            this.njson = njson;
        }

    }
}
