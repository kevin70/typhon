package org.skfiy.typhon.dobj;

import org.skfiy.typhon.domain.item.AbstractItem;
import org.skfiy.typhon.domain.item.TroopItem;

public class TroopItemDobj extends EquipmentItemDobj {

    private int level;
    private PrimaryType primary;
    private int[] atkUps;
    private int[] defUps;
    private int[] matkUps;
    private int[] mdefUps;
    private int[] hpUps;

    private int[] upgradeTexps;
    private int[] splitTexps;
    private int[] masterLevels;
    private int[] coppers;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public PrimaryType getPrimary() {
        return primary;
    }

    public void setPrimary(PrimaryType primary) {
        this.primary = primary;
    }

    public int[] getAtkUps() {
        return atkUps;
    }

    public void setAtkUps(int[] atkUps) {
        this.atkUps = atkUps;
    }

    public int[] getDefUps() {
        return defUps;
    }

    public void setDefUps(int[] defUps) {
        this.defUps = defUps;
    }

    public int[] getMatkUps() {
        return matkUps;
    }

    public void setMatkUps(int[] matkUps) {
        this.matkUps = matkUps;
    }

    public int[] getMdefUps() {
        return mdefUps;
    }

    public void setMdefUps(int[] mdefUps) {
        this.mdefUps = mdefUps;
    }

    public int[] getHpUps() {
        return hpUps;
    }

    public void setHpUps(int[] hpUps) {
        this.hpUps = hpUps;
    }

    public int[] getUpgradeTexps() {
        return upgradeTexps;
    }

    public int getUpgradeTexp(int i) {
        return upgradeTexps[i];
    }

    public void setUpgradeTexps(int[] upgradeTexps) {
        this.upgradeTexps = upgradeTexps;
    }

    public int[] getSplitTexps() {
        return splitTexps;
    }

    public int getSplitTexp(int i) {
        return splitTexps[i];
    }

    public void setSplitTexps(int[] splitTexps) {
        this.splitTexps = splitTexps;
    }

    public int[] getMasterLevels() {
        return masterLevels;
    }

    public int getMasterLevel(int i) {
        return masterLevels[i];
    }

    public void setMasterLevels(int[] masterLevels) {
        this.masterLevels = masterLevels;
    }

    public int[] getCoppers() {
        return coppers;
    }

    public int getCopper(int i) {
        return coppers[i];
    }

    public void setCoppers(int[] coppers) {
        this.coppers = coppers;
    }

    @Override
    public AbstractItem toDomainItem() {
        TroopItem troop = new TroopItem();
        troop.setItemDobj(this);

        troop.setLevel(level);
        troop.setAtk(getAtk());
        troop.setDef(getDef());
        troop.setMatk(getMatk());
        troop.setMdef(getMdef());
        troop.setHp(getHp());
        return troop;
    }

    /**
     *
     */
    public enum PrimaryType {

        atk, def, matk, mdef, hp, exp
    }
}
