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

import com.alibaba.fastjson.annotation.JSONType;
import org.skfiy.typhon.domain.item.IFightItem;
import org.skfiy.typhon.domain.item.Race;
import org.skfiy.typhon.script.Script;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"askill", "pskill"}, shortType = MonsterItemDobj.JSON_SHORT_TYPE)
public abstract class AbstractHeroItemDobj extends ItemDobj implements IFightItem {

    private Race race;
    private int atk;
    private int def;
    private int matk;
    private int mdef;
    private int hp;
    private short defaultFury;
    private short maxFury;
    private int critRate;
    private int decritRate;
    private int critMagn;
    private int parryRate;
    private int deparryRate;
    private int parryValue;
    //
    private Script askill;
    private Script pskill;

    @Override
    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }
    
    @Override
    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    @Override
    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    @Override
    public int getMatk() {
        return matk;
    }

    public void setMatk(int matk) {
        this.matk = matk;
    }

    @Override
    public int getMdef() {
        return mdef;
    }

    public void setMdef(int mdef) {
        this.mdef = mdef;
    }

    @Override
    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public short getDefaultFury() {
        return defaultFury;
    }

    public void setDefaultFury(short defaultFury) {
        this.defaultFury = defaultFury;
    }

    @Override
    public short getMaxFury() {
        return maxFury;
    }

    public void setMaxFury(short maxFury) {
        this.maxFury = maxFury;
    }

    @Override
    public int getCritRate() {
        return critRate;
    }

    public void setCritRate(int critRate) {
        this.critRate = critRate;
    }

    @Override
    public int getDecritRate() {
        return decritRate;
    }

    public void setDecritRate(int decritRate) {
        this.decritRate = decritRate;
    }

    @Override
    public int getCritMagn() {
        return critMagn;
    }

    public void setCritMagn(int critMagn) {
        this.critMagn = critMagn;
    }

    @Override
    public int getParryRate() {
        return parryRate;
    }

    public void setParryRate(int parryRate) {
        this.parryRate = parryRate;
    }

    @Override
    public int getDeparryRate() {
        return deparryRate;
    }

    public void setDeparryRate(int deparryRate) {
        this.deparryRate = deparryRate;
    }

    @Override
    public int getParryValue() {
        return parryValue;
    }

    public void setParryValue(int parryValue) {
        this.parryValue = parryValue;
    }

    @Override
    public Script getAskill() {
        return askill;
    }

    public void setAskill(Script askill) {
        this.askill = askill;
    }

    @Override
    public Script getPskill() {
        return pskill;
    }

    public void setPskill(Script pskill) {
        this.pskill = pskill;
    }

    @Override
    public Shot[] getShots() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
