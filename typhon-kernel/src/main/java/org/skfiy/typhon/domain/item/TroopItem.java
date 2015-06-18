package org.skfiy.typhon.domain.item;

import org.skfiy.typhon.dobj.TroopItemDobj;
import org.skfiy.typhon.domain.ITroop;
import org.skfiy.typhon.util.DomainUtils;

public class TroopItem extends AbstractItem<TroopItemDobj> {

    private int level;

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

    private int texp;
    private ITroop.Type activeType;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        DomainUtils.firePropertyChange(this, "level", level);
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
        DomainUtils.firePropertyChange(this, "atk", atk);
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
        DomainUtils.firePropertyChange(this, "def", def);
    }

    public int getMatk() {
        return matk;
    }

    public void setMatk(int matk) {
        this.matk = matk;
        DomainUtils.firePropertyChange(this, "matk", matk);
    }

    public int getMdef() {
        return mdef;
    }

    public void setMdef(int mdef) {
        this.mdef = mdef;
        DomainUtils.firePropertyChange(this, "mdef", mdef);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        DomainUtils.firePropertyChange(this, "hp", hp);
    }

    public int getCritRate() {
        return critRate;
    }

    public void setCritRate(int critRate) {
        this.critRate = critRate;
        DomainUtils.firePropertyChange(this, "critRate", critRate);
    }

    public int getCritMagn() {
        return critMagn;
    }

    public void setCritMagn(int critMagn) {
        this.critMagn = critMagn;
        DomainUtils.firePropertyChange(this, "critMagn", critMagn);
    }

    public int getDecritRate() {
        return decritRate;
    }

    public void setDecritRate(int decritRate) {
        this.decritRate = decritRate;
        DomainUtils.firePropertyChange(this, "decritRate", decritRate);
    }

    public int getParryRate() {
        return parryRate;
    }

    public void setParryRate(int parryRate) {
        this.parryRate = parryRate;
        DomainUtils.firePropertyChange(this, "parryRate", parryRate);
    }

    public int getParryValue() {
        return parryValue;
    }

    public void setParryValue(int parryValue) {
        this.parryValue = parryValue;
        DomainUtils.firePropertyChange(this, "parryValue", parryValue);
    }

    public int getDeparryRate() {
        return deparryRate;
    }

    public void setDeparryRate(int deparryRate) {
        this.deparryRate = deparryRate;
        DomainUtils.firePropertyChange(this, "deparryRate", deparryRate);
    }

    public int getTexp() {
        return texp;
    }

    public void setTexp(int texp) {
        this.texp = texp;
        DomainUtils.firePropertyChange(this, "texp", texp);
    }

    public ITroop.Type getActiveType() {
        return activeType;
    }

    public void setActiveType(ITroop.Type activeType) {
        this.activeType = activeType;
        DomainUtils.firePropertyChange(this, "activeType", activeType);
    }

    @Override
    public TroopItemDobj getItemDobj() {
        return (TroopItemDobj) super.getItemDobj();
    }
}
