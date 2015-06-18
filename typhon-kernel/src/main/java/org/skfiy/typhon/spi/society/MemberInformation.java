package org.skfiy.typhon.spi.society;

import java.util.ArrayList;
import java.util.List;

public class MemberInformation {

    private int uid;
    private String name;
    private String avatar;
    private String avatarBorder;
    private int powerGuessSum;
    private int societyHurt;
    private int level;
    private List<Object> heroes = new ArrayList<>();


    public MemberInformation() {}

    public MemberInformation(int uid, int societyHurt) {
        this.uid = uid;
        this.societyHurt = societyHurt;
    }

    public MemberInformation(int uid, int societyHurt, List<Object> heroes) {
        this.uid = uid;
        this.societyHurt = societyHurt;
        this.heroes = heroes;
    }

    public MemberInformation(int uid, String name, String avatar, String avatarBorder,
            int powerGuessSum, List<Object> heroes, int level) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.avatarBorder = avatarBorder;
        this.powerGuessSum = powerGuessSum;
        this.heroes = heroes;
        this.level = level;
    }
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarBorder() {
        return avatarBorder;
    }

    public void setAvatarBorder(String avatarBorder) {
        this.avatarBorder = avatarBorder;
    }

    public int getPowerGuessSum() {
        return powerGuessSum;
    }

    public void setPowerGuessSum(int powerGuessSum) {
        this.powerGuessSum = powerGuessSum;
    }

    public int getSocietyHurt() {
        return societyHurt;
    }

    public void setSocietyHurt(int societyHurt) {
        this.societyHurt = societyHurt;
    }

    public List<Object> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<Object> heroes) {
        this.heroes = heroes;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
