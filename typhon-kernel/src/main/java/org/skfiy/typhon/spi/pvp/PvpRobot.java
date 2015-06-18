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

import java.util.List;
import org.skfiy.typhon.domain.item.Item;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PvpRobot {

    private String name;
    private int level;
    private int powerGuess;
    private String honour;
    private List<Hero> heros;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getHonour() {
        return honour;
    }

    public void setHonour(String honour) {
        this.honour = honour;
    }

    public List<Hero> getHeros() {
        return heros;
    }

    public Hero getHero(int i) {
        return heros.get(i);
    }

    public void setHeros(List<Hero> heros) {
        this.heros = heros;
    }

    public static class Hero {

        private String iid;
        private int level;
        private Item.Star star;
        private int ladder;

        private int atk;
        private int def;
        private int matk;
        private int mdef;
        private int hp;

        private double critRate;
        private double decritRate;
        private double critMagn;

        private double parryRate;
        private double deparryRate;
        private double parryValue;

        public String getIid() {
            return iid;
        }

        public void setIid(String iid) {
            this.iid = iid;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public Item.Star getStar() {
            return star;
        }

        public void setStar(Item.Star star) {
            this.star = star;
        }

        public int getLadder() {
            return ladder;
        }

        public void setLadder(int ladder) {
            this.ladder = ladder;
        }

        public int getAtk() {
            return atk;
        }

        public void setAtk(int atk) {
            this.atk = atk;
        }

        public int getDef() {
            return def;
        }

        public void setDef(int def) {
            this.def = def;
        }

        public int getMatk() {
            return matk;
        }

        public void setMatk(int matk) {
            this.matk = matk;
        }

        public int getMdef() {
            return mdef;
        }

        public void setMdef(int mdef) {
            this.mdef = mdef;
        }

        public int getHp() {
            return hp;
        }

        public void setHp(int hp) {
            this.hp = hp;
        }

        public double getCritRate() {
            return critRate;
        }

        public void setCritRate(double critRate) {
            this.critRate = critRate / 500;
        }

        public double getDecritRate() {
            return decritRate;
        }

        public void setDecritRate(double decritRate) {
            this.decritRate = decritRate / 500;
        }

        public double getCritMagn() {
            return critMagn;
        }

        public void setCritMagn(double critMagn) {
            this.critMagn = critMagn / 100;
        }

        public double getParryRate() {
            return parryRate;
        }

        public void setParryRate(double parryRate) {
            this.parryRate = parryRate / 500;
        }

        public double getDeparryRate() {
            return deparryRate;
        }

        public void setDeparryRate(double deparryRate) {
            this.deparryRate = deparryRate / 600;
        }

        public double getParryValue() {
            return parryValue;
        }

        public void setParryValue(double parryValue) {
            this.parryValue = parryValue;
        }
    }
}
