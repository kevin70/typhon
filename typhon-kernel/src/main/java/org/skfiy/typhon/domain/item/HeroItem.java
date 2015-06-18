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

import java.util.ArrayList;
import java.util.List;
import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.domain.Changeable;
import org.skfiy.typhon.domain.IHeroEntity;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.util.DomainUtils;
import org.skfiy.util.Assert;

import com.alibaba.fastjson.annotation.JSONType;
import org.skfiy.typhon.domain.item.IFightItem.Shot;

/**
 * ladder
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"area", "race", "gender", "shots",
    "tong", "wu", "zhi",
    "atk", "def", "hp",
    "maxFury", "critRate", "decritRate", "critMagn",
    "parryRate", "deparryRate", "parryValue",
    "askill", "pskill", "shots1", "shots2", "shots3", "shots4", "shots5",
    "shots6", "shots7", "shots8", "shots9", "shots10"
})
public class HeroItem extends AbstractItem<HeroItemDobj> implements IHeroEntity, Changeable,
        Cloneable {

//    private static final JSONArray POWER_GUESS_CONFIG;
//
//    static {
//        String data = ComponentUtils.readDataFile("power_guess.json");
//        POWER_GUESS_CONFIG = JSON.parseArray(data);
//    }

    private int level = 1;
    private int ladder = 1;
    private int exp;
    private List<Rabbet> rabbets;
    private int extraTong;
    private int extraWu;
    private int extraZhi;
    private int extraAtk;
    private int extraDef;
    private int extraMatk;
    private int extraMdef;
    private int extraHp;
    private int extraParryRate;
    private int extraParryValue;
    private int extraCritRate;
    private int extraDecritRate;
    private int extraDeparryRate;
    private int extraCritMagn;
    private int powerGuess;
    // 专属武器/基础/洗练
    private Rabbet weaponsRabbets;
    private int buildLevel;
    private int strengLevel;
    // 专属武器强化
    private Rabbet weaponsStreng;
    // 专属武器的打造
    private Rabbet weaponsBuild;
    // 专属武器附魔
    private Rabbet weaponsEnchant;

    public HeroItem() {

    }

    @Override
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        DomainUtils.firePropertyChange(this, "level", this.level);
    }

    @Override
    public int getLadder() {
        return ladder;
    }

    public void setLadder(int ladder) {
        this.ladder = ladder;
        DomainUtils.firePropertyChange(this, "ladder", this.ladder);
    }

    @Override
    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        if (this.exp != exp) {
            this.exp = exp;
            DomainUtils.firePropertyChange(this, "exp", this.exp);
        }
    }

    @Override
    public List<Rabbet> getRabbets() {
        return rabbets;
    }

    public void setRabbets(List<Rabbet> rabbets) {
        if (this.rabbets == null) {
            this.rabbets = new ArrayList<>(6);
        }

        this.rabbets.addAll(rabbets);

        for (int i = 0; i < this.rabbets.size(); i++) {
            Rabbet rabbet = this.rabbets.get(i);
            rabbet.set(this, "rabbets", i);
        }
        DomainUtils.firePropertyChange(this, "rabbets", this.rabbets);
    }

    public void clearRabbets() {
        this.rabbets.clear();
        DomainUtils.firePropertyChange(this, "rabbets", this.rabbets);
    }

    /**
     * 
     * @param rabbet
     */
    public void addRabbet(Rabbet rabbet) {
        Assert.notNull(rabbet);

        if (rabbets == null) {
            rabbets = new ArrayList<>(6);
        }

        rabbets.add(rabbet);
        rabbet.set(this, "rabbets", rabbets.size() - 1);
        DomainUtils.fireIndexPropertyAdd(this, "rabbets", rabbet);
    }

    public void setRabbet(Rabbet rabbet) {
        for (Rabbet b : rabbets) {
            if (b.getPoint() == rabbet.getPoint()) {
                rabbets.remove(b);
                rabbets.add(rabbet);
                break;
            }
        }
        DomainUtils.firePropertyChange(this, "rabbets", this.rabbets);
    }

    /**
     * 
     * @param point
     * @return
     */
    public Rabbet findRabbet(int point) {
        if (rabbets == null) {
            return null;
        }

        for (Rabbet r : rabbets) {
            if (r.getPoint() == point) {
                return r;
            }
        }
        return null;
    }

    @Override
    public int getExtraTong() {
        return extraTong;
    }

    public void setExtraTong(int extraTong) {
        this.extraTong = extraTong;
        DomainUtils.firePropertyChange(this, "extraTong", this.extraTong);
    }

    @Override
    public int getExtraWu() {
        return extraWu;
    }

    public void setExtraWu(int extraWu) {
        this.extraWu = extraWu;
        DomainUtils.firePropertyChange(this, "extraWu", this.extraWu);
    }

    @Override
    public int getExtraZhi() {
        return extraZhi;
    }

    public void setExtraZhi(int extraZhi) {
        this.extraZhi = extraZhi;
        DomainUtils.firePropertyChange(this, "extraZhi", this.extraZhi);
    }

    @Override
    public int getExtraAtk() {
        return extraAtk;
    }

    public void setExtraAtk(int extraAtk) {
        this.extraAtk = extraAtk;
        DomainUtils.firePropertyChange(this, "extraAtk", this.extraAtk);
    }

    @Override
    public int getExtraDef() {
        return extraDef;
    }

    public void setExtraDef(int extraDef) {
        this.extraDef = extraDef;
        DomainUtils.firePropertyChange(this, "extraDef", this.extraDef);
    }

    @Override
    public int getExtraMatk() {
        return extraMatk;
    }

    public void setExtraMatk(int extraMatk) {
        this.extraMatk = extraMatk;
        DomainUtils.firePropertyChange(this, "extraMatk", this.extraMatk);
    }

    @Override
    public int getExtraMdef() {
        return extraMdef;
    }

    public void setExtraMdef(int extraMdef) {
        this.extraMdef = extraMdef;
        DomainUtils.firePropertyChange(this, "extraMdef", this.extraMdef);
    }

    @Override
    public int getExtraHp() {
        return extraHp;
    }

    public void setExtraHp(int extraHp) {
        this.extraHp = extraHp;
        DomainUtils.firePropertyChange(this, "extraHp", this.extraHp);
    }

    @Override
    public int getExtraParryRate() {
        return extraParryRate;
    }

    public void setExtraParryRate(int extraParryRate) {
        this.extraParryRate = extraParryRate;
        DomainUtils.firePropertyChange(this, "extraParryRate", this.extraParryRate);
    }

    @Override
    public int getExtraParryValue() {
        return extraParryValue;
    }

    public void setExtraParryValue(int extraParryValue) {
        this.extraParryValue = extraParryValue;
        DomainUtils.firePropertyChange(this, "extraParryValue", this.extraParryValue);
    }

    @Override
    public int getExtraCritRate() {
        return extraCritRate;
    }

    public void setExtraCritRate(int extraCritRate) {
        this.extraCritRate = extraCritRate;
        DomainUtils.firePropertyChange(this, "extraCritRate", this.extraCritRate);
    }

    @Override
    public int getExtraDecritRate() {
        return extraDecritRate;
    }

    public void setExtraDecritRate(int extraDecritRate) {
        this.extraDecritRate = extraDecritRate;
        DomainUtils.firePropertyChange(this, "extraDecritRate", this.extraDecritRate);
    }

    @Override
    public int getExtraDeparryRate() {
        return extraDeparryRate;
    }

    public void setExtraDeparryRate(int extraDeparryRate) {
        this.extraDeparryRate = extraDeparryRate;
        DomainUtils.firePropertyChange(this, "extraDeparryRate", this.extraDeparryRate);
    }

    @Override
    public int getExtraCritMagn() {
        return extraCritMagn;
    }

    public void setExtraCritMagn(int extraCritMagn) {
        this.extraCritMagn = extraCritMagn;
        DomainUtils.firePropertyChange(this, "extraCritMagn", this.extraCritMagn);
    }

    @Override
    public int getPowerGuess() {
        return powerGuess;
    }

    public void setPowerGuess(int powerGuess) {
        this.powerGuess = powerGuess;
        DomainUtils.firePropertyChange(this, "powerGuess", this.powerGuess);
    }

    public Area getArea() {
        return getItemDobj().getArea();
    }

    @Override
    public Race getRace() {
        return getItemDobj().getRace();
    }

    public Gender getGender() {
        return getItemDobj().getGender();
    }

    public Shot[] getShots() {
        switch (getWallColor().ordinal()) {
            case 0:
                return getItemDobj().getShots1();
            case 1:
                return getItemDobj().getShots2();
            case 2:
                return getItemDobj().getShots3();
            case 3:
                return getItemDobj().getShots4();
            default:
                return getItemDobj().getShots5();
        }
    }

    public Script getAskill() {
        return getItemDobj().getAskill();
    }

    public Script getPskill() {
        return getItemDobj().getPskill();
    }

    /**
     * 专属武器
     */
    @Override
    public Rabbet getWeaponsRabbets() {
        return weaponsRabbets;
    }

    public void setWeaponsRabbets(Rabbet weaponsRabbets) {
        this.weaponsRabbets = weaponsRabbets;
        if (weaponsRabbets != null) {
            this.weaponsRabbets.set(this, "weaponsRabbets");
            DomainUtils.firePropertyChange(this, "weaponsRabbets", this.weaponsRabbets);
        }
    }

    public int getBuildLevel() {
        return buildLevel;
    }

    public void setBuildLevel(int buildLevel) {
        this.buildLevel = buildLevel;
        DomainUtils.firePropertyChange(this, "buildLevel", this.buildLevel);
    }

    public int getStrengLevel() {
        return strengLevel;
    }

    public void setStrengLevel(int strengLevel) {
        this.strengLevel = strengLevel;
        DomainUtils.firePropertyChange(this, "strengLevel", this.strengLevel);
    }

    @Override
    public Rabbet getWeaponsBuild() {
        return weaponsBuild;
    }

    public void setWeaponsBuild(Rabbet weaponsBuild) {
        this.weaponsBuild = weaponsBuild;
        if (weaponsBuild != null) {
            this.weaponsBuild.set(this, "weaponsBuild");
            DomainUtils.firePropertyChange(this, "weaponsBuild", this.weaponsBuild);
        }
    }

    @Override
    public Rabbet getWeaponsEnchant() {
        return weaponsEnchant;
    }

    public void setWeaponsEnchant(Rabbet weaponsEnchant) {
        this.weaponsEnchant = weaponsEnchant;
        if (weaponsEnchant != null) {
            this.weaponsEnchant.set(this, "weaponsEnchant");
            DomainUtils.firePropertyChange(this, "weaponsEnchant", this.weaponsEnchant);
        }
    }

    @Override
    public Rabbet getWeaponsStreng() {
        return weaponsStreng;
    }

    public void setWeaponsStreng(Rabbet weaponsStreng) {
        this.weaponsStreng = weaponsStreng;
        if (weaponsStreng != null) {
            this.weaponsStreng.set(this, "weaponsStreng");
            DomainUtils.firePropertyChange(this, "weaponsStreng", this.weaponsStreng);
        }
    }

    /**
     * 
     * @return
     */
    public WallColor getWallColor() {
        if (ladder < 2) {
            return WallColor.Gray;
        } else if (ladder < 4) {
            return WallColor.Green;
        } else if (ladder < 7) {
            return WallColor.Blue;
        } else if (ladder < 11) {
            return WallColor.Red;
        }
        return WallColor.Purple;
    }
}
