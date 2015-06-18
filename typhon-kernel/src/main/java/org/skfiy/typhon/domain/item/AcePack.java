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
package org.skfiy.typhon.domain.item;

/**
 * 锦囊属性.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class AcePack {

    private int atk;
    private int def;
    private int matk;
    private int mdef;
    private int hp;

    private int critRate;
    private int critMagn;
    private int decritRate;
    private int parryRate;
    private int parryValue;
    private int deparryRate;

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

    public int getCritRate() {
        return critRate;
    }

    public void setCritRate(int critRate) {
        this.critRate = critRate;
    }

    public int getCritMagn() {
        return critMagn;
    }

    public void setCritMagn(int critMagn) {
        this.critMagn = critMagn;
    }

    public int getDecritRate() {
        return decritRate;
    }

    public void setDecritRate(int decritRate) {
        this.decritRate = decritRate;
    }

    public int getParryRate() {
        return parryRate;
    }

    public void setParryRate(int parryRate) {
        this.parryRate = parryRate;
    }

    public int getParryValue() {
        return parryValue;
    }

    public void setParryValue(int parryValue) {
        this.parryValue = parryValue;
    }

    public int getDeparryRate() {
        return deparryRate;
    }

    public void setDeparryRate(int deparryRate) {
        this.deparryRate = deparryRate;
    }

    @Override
    public Object clone() {
        AcePack acePack = new AcePack();
        acePack.setAtk(atk);
        acePack.setDef(def);
        acePack.setMatk(matk);
        acePack.setMdef(mdef);
        acePack.setHp(hp);

        acePack.setCritRate(critRate);
        acePack.setCritMagn(critMagn);
        acePack.setDecritRate(decritRate);

        acePack.setParryRate(parryRate);
        acePack.setParryValue(parryValue);
        acePack.setDeparryRate(deparryRate);
        
        return acePack;
    }
}
