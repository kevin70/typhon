package org.skfiy.typhon.spi.activity;

import org.skfiy.typhon.domain.item.Area;
import org.skfiy.typhon.domain.item.Gender;
import org.skfiy.typhon.domain.item.Race;

/**
 * @author Administrator
 * 
 */
public class AtlasHeros {
    private String[] requirements;
    private String wid;
    private int count;
    private int copper;
    private int heroCount;
    private Race heroRace;
    private Area heroArea;
    private Gender gender;
    private Type type;
    public String[] getRequirements() {
        return requirements;
    }

    public void setRequirements(String[] requirements) {
        this.requirements = requirements;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public int getCopper() {
        return copper;
    }

    public void setCopper(int copper) {
        this.copper = copper;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    public int getHeroCount() {
        return heroCount;
    }

    public void setHeroCount(int heroCount) {
        this.heroCount = heroCount;
    }

    public Race getHeroRace() {
        return heroRace;
    }

    public void setHeroRace(Race heroRace) {
        this.heroRace = heroRace;
    }

    public Area getHeroArea() {
        return heroArea;
    }

    public void setHeroArea(Area heroArea) {
        this.heroArea = heroArea;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        a, b
    }
}
