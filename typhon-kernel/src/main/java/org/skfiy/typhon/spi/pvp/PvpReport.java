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
package org.skfiy.typhon.spi.pvp;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PvpReport {

    private String attackerName;
    private List<JSONObject> attackerHeros;
    private String attackerSuccorIid;

    private String defenderName;
    private List<JSONObject> defenderHeros;
    private String defenderSuccorIid;

    private final List<Round> rounds = new ArrayList<>();

    private int win;

    public String getAttackerName() {
        return attackerName;
    }

    public void setAttackerName(String attackerName) {
        this.attackerName = attackerName;
    }

    public List<JSONObject> getAttackerHeros() {
        return attackerHeros;
    }

    public void setAttackerHeros(List<JSONObject> attackerHeros) {
        this.attackerHeros = attackerHeros;
    }

    public String getAttackerSuccorIid() {
        return attackerSuccorIid;
    }

    public void setAttackerSuccorIid(String attackerSuccorIid) {
        this.attackerSuccorIid = attackerSuccorIid;
    }

    public String getDefenderName() {
        return defenderName;
    }

    public void setDefenderName(String defenderName) {
        this.defenderName = defenderName;
    }

    public List<JSONObject> getDefenderHeros() {
        return defenderHeros;
    }

    public void setDefenderHeros(List<JSONObject> defenderHeros) {
        this.defenderHeros = defenderHeros;
    }

    public String getDefenderSuccorIid() {
        return defenderSuccorIid;
    }

    public void setDefenderSuccorIid(String defenderSuccorIid) {
        this.defenderSuccorIid = defenderSuccorIid;
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

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public static class Round {

        private int[] aholdPoints;
        private int[] bholdPoints;
        private final List<Object> details = new ArrayList<>();

        public int[] getAholdPoints() {
            return aholdPoints;
        }

        public void setAholdPoints(int[] aholdPoints) {
            this.aholdPoints = aholdPoints;
        }

        public int[] getBholdPoints() {
            return bholdPoints;
        }

        public void setBholdPoints(int[] bholdPoints) {
            this.bholdPoints = bholdPoints;
        }

        public List<Object> getDetails() {
            return details;
        }

        public boolean addDetail(Object detail) {
            return details.add(detail);
        }

        public void setDetails(List<Object> details) {
            this.details.addAll(details);
        }
    }

}
