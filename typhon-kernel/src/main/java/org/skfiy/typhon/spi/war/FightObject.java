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

import java.util.ArrayList;
import java.util.List;
import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.domain.item.Area;
import org.skfiy.typhon.domain.item.Gender;
import org.skfiy.typhon.domain.item.IFightItem;
import org.skfiy.typhon.domain.item.Item.Star;
import org.skfiy.typhon.domain.item.Race;
import org.skfiy.typhon.spi.war.BufferSkill.Type;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public final class FightObject {

    public static final int YHU_FIGHT_OBJECT_LAB = 9;

    private final int lab;
    private final HeroItemDobj heroItemDobj;
    private final int level;
    private final int ladder;
    private final Star star;

    private int baseAtk;
    private int baseMatk;
    
    private int maxAtk;
    private int maxDef;
    private int maxMatk;
    private int maxMdef;
    private int maxHp;

    private double maxCritRate;
    private double maxDecritRate;
    private double maxCritMagn;
    private double maxParryRate;
    private double maxDeparryRate;
    private double maxParryValue;
    //============================================================================================//
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
    //============================================================================================//
    private int shieldHp;

    private int fury;
    private IFightItem.Shot[] shots;

    //
    private final List<BufferSkill> bufferSkills = new ArrayList<>();
    private Status status = Status.NORMAL;

    /**
     *
     * @param lab
     * @param level
     * @param ladder
     * @param star
     * @param heroItemDobj
     */
    public FightObject(int lab, int level, int ladder, Star star, HeroItemDobj heroItemDobj) {
        this.lab = lab;
        this.level = level;
        this.ladder = ladder;
        this.star = star;
        this.heroItemDobj = heroItemDobj;
        this.fury = heroItemDobj.getDefaultFury();
    }

    public int getLab() {
        return lab;
    }

    public int getLevel() {
        return level;
    }

    public int getLadder() {
        return ladder;
    }

    public Star getStar() {
        return star;
    }
    //============================================================================================//

    public int getBaseAtk() {
        return baseAtk;
    }

    public void setBaseAtk(int baseAtk) {
        this.baseAtk = baseAtk;
        
        setMaxAtk(baseAtk);
    }

    public int getBaseMatk() {
        return baseMatk;
    }

    public void setBaseMatk(int baseMatk) {
        this.baseMatk = baseMatk;
        
        setMaxMatk(baseMatk);
    }

    public int getMaxAtk() {
        return maxAtk;
    }

    public void setMaxAtk(int maxAtk) {
        this.maxAtk = maxAtk;
        this.atk = maxAtk;
    }

    public int getMaxDef() {
        return maxDef;
    }

    public void setMaxDef(int maxDef) {
        this.maxDef = maxDef;
        this.def = maxDef;
    }

    public int getMaxMatk() {
        return maxMatk;
    }

    public void setMaxMatk(int maxMatk) {
        this.maxMatk = maxMatk;
        this.matk = maxMatk;
    }

    public int getMaxMdef() {
        return maxMdef;
    }

    public void setMaxMdef(int maxMdef) {
        this.maxMdef = maxMdef;
        this.mdef = maxMdef;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public double getMaxCritRate() {
        return maxCritRate;
    }

    public void setMaxCritRate(double maxCritRate) {
        this.maxCritRate = maxCritRate;
        this.critRate = maxCritRate;
    }

    public double getMaxDecritRate() {
        return maxDecritRate;
    }

    public void setMaxDecritRate(double maxDecritRate) {
        this.maxDecritRate = maxDecritRate;
        this.decritRate = maxDecritRate;
    }

    public double getMaxCritMagn() {
        return maxCritMagn;
    }

    public void setMaxCritMagn(double maxCritMagn) {
        this.maxCritMagn = maxCritMagn;
        this.critMagn = maxCritMagn;
    }

    public double getMaxParryRate() {
        return maxParryRate;
    }

    public void setMaxParryRate(double maxParryRate) {
        this.maxParryRate = maxParryRate;
        this.parryRate = maxParryRate;
    }

    public double getMaxDeparryRate() {
        return maxDeparryRate;
    }

    public void setMaxDeparryRate(double maxDeparryRate) {
        this.maxDeparryRate = maxDeparryRate;
        this.deparryRate = maxDeparryRate;
    }

    public double getMaxParryValue() {
        return maxParryValue;
    }

    public void setMaxParryValue(double maxParryValue) {
        this.maxParryValue = maxParryValue;
        this.parryValue = maxParryValue;
    }

    //=============================================================================
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

        if (this.hp <= 0) {
            status = Status.DEAD;
        }
    }

    public int getShieldHp() {
        return shieldHp;
    }

    public void setShieldHp(int shieldHp) {
        this.shieldHp += shieldHp;
        if (this.shieldHp > maxHp) {
            this.shieldHp = maxHp;
        }
    }

    public double getCritRate() {
        return critRate;
    }

    public void setCritRate(double critRate) {
        this.critRate = critRate;
    }

    public double getDecritRate() {
        return decritRate;
    }

    public void setDecritRate(double decritRate) {
        this.decritRate = decritRate;
    }

    public double getCritMagn() {
        return critMagn;
    }

    public void setCritMagn(double critMagn) {
        this.critMagn = critMagn;
    }

    public double getParryRate() {
        return parryRate;
    }

    public void setParryRate(double parryRate) {
        this.parryRate = parryRate;
    }

    public double getDeparryRate() {
        return deparryRate;
    }

    public void setDeparryRate(double deparryRate) {
        this.deparryRate = deparryRate;
    }

    public double getParryValue() {
        return parryValue;
    }

    public void setParryValue(double parryValue) {
        this.parryValue = parryValue;
    }

    public int getFury() {
        return fury;
    }

    public void setFury(int fury) {
        this.fury = fury;
    }

    public IFightItem.Shot[] getShots() {
        return shots;
    }

    public IFightItem.Shot getShot(int i) {
        return shots[i];
    }

    public void setShots(IFightItem.Shot[] shots) {
        this.shots = shots;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    //==============================================================================================
    /**
     *
     * @return
     */
    public List<BufferSkill> getBufferSkills() {
        return bufferSkills;
    }

    /**
     *
     * @param bufferSkill
     */
    public void addBufferSkill(BufferSkill bufferSkill) {
        // BufferSkill rmbs = null;
        for (BufferSkill bs : bufferSkills) {
            if (bs.getName().equals(bufferSkill.getName())) {
                bs.onFinish();
                break;
            }
        }

        // removeBufferSkill(rmbs);
        bufferSkills.add(bufferSkill);
    }

    /**
     *
     * @param bufferSkill
     */
    public void removeBufferSkill(BufferSkill bufferSkill) {
        if (bufferSkill != null) {
            bufferSkills.remove(bufferSkill);
        }
    }

    /**
     *
     */
    public void removeAllDebuff() {
        if (bufferSkills.size() > 0) {
            for (BufferSkill bs : new ArrayList<>(bufferSkills)) {
                if (bs.getType() == Type.DEBUFF) {
                    bs.onFinish();
                }
            }
        }
    }
    //==============================================================================================

    //=============================================================================
    public String getHeroId() {
        return heroItemDobj.getId();
    }

    public String getBsaSkill() {
        return heroItemDobj.getBsaSkill();
    }

    public String getLadderSkill() {
        return heroItemDobj.getLeaderSkill();
    }

    public int getMaxFury() {
        return heroItemDobj.getMaxFury();
    }

    public Area getArea() {
        return heroItemDobj.getArea();
    }

    public Race getRace() {
        return heroItemDobj.getRace();
    }

    public Gender getGender() {
        return heroItemDobj.getGender();
    }
    //=============================================================================

    /**
     *
     * @return
     */
    public boolean isAvailable() {
        return (status == Status.NORMAL);
    }

    /**
     * 战斗对象是否已经死亡.
     *
     * @return Boolean
     */
    public boolean isDead() {
        return (status == Status.DEAD);
    }

    /**
     *
     * @param ifury
     * @return
     */
    public int incrementFury(int ifury) {
        int a = heroItemDobj.getMaxFury() - fury;
        if (a > ifury) {
            fury += ifury;
            return ifury;
        } else {
            fury += a;
            return a;
        }
    }

    /**
     *
     * @param dfury
     * @return
     */
    public int decrementFury(int dfury) {
        int a = fury - dfury;
        if (a < 0) {
            fury = 0;
            return a;
        } else {
            fury = a;
            return dfury;
        }
    }

    /**
     *
     * @param ihp
     */
    public void incrementHp(int ihp) {
        int a = maxHp - hp;
        if (a > ihp) {
            setHp(hp + ihp);
//            return ihp;
        } else {
            setHp(maxHp);
//            return a;
        }
    }

    /**
     *
     * @param dhp
     */
    public void decrementHp(int dhp) {
        // 首先减护循
        int a = shieldHp - dhp;
        if (a < 0) {
            setHp(hp += a);
            shieldHp = 0;
        } else {
            shieldHp = a;
        }
    }

    public enum Status {

        /**
         * 正常.
         */
        NORMAL,
        /**
         * 晕眩(不可攻击).
         */
        DAZE,
        /**
         * 睡眠状态(不可进行任何操作).
         */
        SLEEPING,
        /**
         * 混乱. 不能进行COMBO结算, 可能攻击自己人.
         */
        CONFUSION,
        /**
         * 死亡.
         */
        DEAD
    }

}
