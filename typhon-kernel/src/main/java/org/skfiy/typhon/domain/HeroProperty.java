package org.skfiy.typhon.domain;

import java.util.List;
import org.skfiy.typhon.domain.IHeroEntity.Rabbet;

import org.skfiy.typhon.domain.item.Item;
import org.skfiy.typhon.domain.item.Race;

public class HeroProperty implements IHeroEntity {

    private String id;
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
    private Race race;
    private Item.Star star;
    // 专属武器/基础/强化/洗练
    private Rabbet weaponsRabbets;
    // 专属武器的打造
    private Rabbet weaponsBuild;
    // 专属武器附魔
    private Rabbet weaponsEnchant;
    // 专属武器洗练
    private Rabbet weaponsStreng;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getLadder() {
        return ladder;
    }

    public void setLadder(int ladder) {
        this.ladder = ladder;
    }

    @Override
    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    @Override
    public List<Rabbet> getRabbets() {
        return rabbets;
    }

    public void setRabbets(List<Rabbet> rabbets) {
        this.rabbets = rabbets;
    }

    @Override
    public int getExtraTong() {
        return extraTong;
    }

    public void setExtraTong(int extraTong) {
        this.extraTong = extraTong;
    }

    @Override
    public int getExtraWu() {
        return extraWu;
    }

    public void setExtraWu(int extraWu) {
        this.extraWu = extraWu;
    }

    @Override
    public int getExtraZhi() {
        return extraZhi;
    }

    public void setExtraZhi(int extraZhi) {
        this.extraZhi = extraZhi;
    }

    @Override
    public int getExtraAtk() {
        return extraAtk;
    }

    public void setExtraAtk(int extraAtk) {
        this.extraAtk = extraAtk;
    }

    @Override
    public int getExtraDef() {
        return extraDef;
    }

    public void setExtraDef(int extraDef) {
        this.extraDef = extraDef;
    }

    @Override
    public int getExtraMatk() {
        return extraMatk;
    }

    public void setExtraMatk(int extraMatk) {
        this.extraMatk = extraMatk;
    }

    @Override
    public int getExtraMdef() {
        return extraMdef;
    }

    public void setExtraMdef(int extraMdef) {
        this.extraMdef = extraMdef;
    }

    @Override
    public int getExtraHp() {
        return extraHp;
    }

    public void setExtraHp(int extraHp) {
        this.extraHp = extraHp;
    }

    @Override
    public int getExtraParryRate() {
        return extraParryRate;
    }

    public void setExtraParryRate(int extraParryRate) {
        this.extraParryRate = extraParryRate;
    }

    @Override
    public int getExtraParryValue() {
        return extraParryValue;
    }

    public void setExtraParryValue(int extraParryValue) {
        this.extraParryValue = extraParryValue;
    }

    @Override
    public int getExtraCritRate() {
        return extraCritRate;
    }

    public void setExtraCritRate(int extraCritRate) {
        this.extraCritRate = extraCritRate;
    }

    @Override
    public int getExtraDecritRate() {
        return extraDecritRate;
    }

    public void setExtraDecritRate(int extraDecritRate) {
        this.extraDecritRate = extraDecritRate;
    }

    @Override
    public int getExtraDeparryRate() {
        return extraDeparryRate;
    }

    public void setExtraDeparryRate(int extraDeparryRate) {
        this.extraDeparryRate = extraDeparryRate;
    }

    @Override
    public int getExtraCritMagn() {
        return extraCritMagn;
    }

    public void setExtraCritMagn(int extraCritMagn) {
        this.extraCritMagn = extraCritMagn;
    }

    @Override
    public int getPowerGuess() {
        return powerGuess;
    }

    public void setPowerGuess(int powerGuess) {
        this.powerGuess = powerGuess;
    }

    @Override
    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    @Override
    public Item.Star getStar() {
        return star;
    }

    public void setStar(Item.Star star) {
        this.star = star;
    }

    @Override
    public Rabbet getWeaponsRabbets() {
        return weaponsRabbets;
    }

    public void setWeaponsRabbets(Rabbet weaponsRabbets) {
        this.weaponsRabbets = weaponsRabbets;
    }

    @Override
    public Rabbet getWeaponsBuild() {
        return weaponsBuild;
    }

    public void setWeaponsBuild(Rabbet weaponsBuild) {
        this.weaponsBuild = weaponsBuild;
    }

    @Override
    public Rabbet getWeaponsEnchant() {
        return weaponsEnchant;
    }

    public void setWeaponsEnchant(Rabbet weaponsEnchant) {
        this.weaponsEnchant = weaponsEnchant;
    }

    @Override
    public Rabbet getWeaponsStreng() {
        return weaponsStreng;
    }

    public void setWeaponsStreng(Rabbet weaponsStreng) {
        this.weaponsStreng = weaponsStreng;
    }

}
