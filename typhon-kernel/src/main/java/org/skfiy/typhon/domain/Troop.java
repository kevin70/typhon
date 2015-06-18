package org.skfiy.typhon.domain;

import com.alibaba.fastjson.annotation.JSONType;
import java.io.Serializable;
import org.skfiy.typhon.domain.item.AcePack;

import org.skfiy.typhon.util.DomainUtils;

@JSONType(ignores = {"acePack"})
public class Troop extends AbstractIndexable implements Cloneable, Serializable {

    private int first;
    private int second;
    private int third;
    private int four;
    private int five;
    private boolean full;

    private int level;
    private int exp;

    // 战位强化属性
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

    private AcePack acePack;

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
        DomainUtils.firePropertyChange(this, "first", this.first);

        full();
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
        DomainUtils.firePropertyChange(this, "second", this.second);

        full();
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
        DomainUtils.firePropertyChange(this, "third", this.third);

        full();
    }

    
    public int getFour() {
        return four;
    }

    public void setFour(int four) {
        this.four = four;
        DomainUtils.firePropertyChange(this, "four", this.four);

        full();
    }

    public int getFive() {
        return five;
    }

    public void setFive(int five) {
        this.five = five;
        DomainUtils.firePropertyChange(this, "five", this.five);

        full();
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
        DomainUtils.firePropertyChange(this, "full", this.full);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        DomainUtils.firePropertyChange(this, "level", this.level);
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
        DomainUtils.firePropertyChange(this, "exp", this.exp);
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;

        DomainUtils.firePropertyChange(this, "atk", this.atk);
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;

        DomainUtils.firePropertyChange(this, "def", this.def);
    }

    public int getMatk() {
        return matk;
    }

    public void setMatk(int matk) {
        this.matk = matk;

        DomainUtils.firePropertyChange(this, "matk", this.matk);
    }

    public int getMdef() {
        return mdef;
    }

    public void setMdef(int mdef) {
        this.mdef = mdef;

        DomainUtils.firePropertyChange(this, "mdef", this.mdef);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;

        DomainUtils.firePropertyChange(this, "hp", this.hp);
    }

    public int getCritRate() {
        return critRate;
    }

    public void setCritRate(int critRate) {
        this.critRate = critRate;

        DomainUtils.firePropertyChange(this, "critRate", this.critRate);
    }

    public int getCritMagn() {
        return critMagn;
    }

    public void setCritMagn(int critMagn) {
        this.critMagn = critMagn;

        DomainUtils.firePropertyChange(this, "critMagn", this.critMagn);
    }

    public int getDecritRate() {
        return decritRate;
    }

    public void setDecritRate(int decritRate) {
        this.decritRate = decritRate;

        DomainUtils.firePropertyChange(this, "decritRate", this.decritRate);
    }

    public int getParryRate() {
        return parryRate;
    }

    public void setParryRate(int parryRate) {
        this.parryRate = parryRate;

        DomainUtils.firePropertyChange(this, "parryRate", this.parryRate);
    }

    public int getParryValue() {
        return parryValue;
    }

    public void setParryValue(int parryValue) {
        this.parryValue = parryValue;

        DomainUtils.firePropertyChange(this, "parryValue", this.parryValue);
    }

    public int getDeparryRate() {
        return deparryRate;
    }

    public void setDeparryRate(int deparryRate) {
        this.deparryRate = deparryRate;

        DomainUtils.firePropertyChange(this, "deparryRate", this.deparryRate);
    }

    public AcePack getAcePack() {
        if (acePack == null) {
            acePack = new AcePack();
        }
        return acePack;
    }

    public void setAcePack(AcePack acePack) {
        this.acePack = acePack;
    }

    private void full() {
        setFull((first > 0 && second > 0 && third > 0 && four > 0 && five > 0));
    }

    @Override
    public Object clone() {
        Troop troop = new Troop();
        troop.setAtk(atk);
        troop.setDef(def);
        troop.setMatk(matk);
        troop.setMdef(mdef);
        troop.setHp(hp);

        troop.setCritRate(critRate);
        troop.setCritMagn(critMagn);
        troop.setDecritRate(decritRate);

        troop.setParryRate(parryRate);
        troop.setParryValue(parryValue);
        troop.setDeparryRate(deparryRate);

        troop.setAcePack((AcePack) acePack.clone());
        return troop;
    }

}
