package org.skfiy.typhon.spi.pve;

import java.util.ArrayList;
import java.util.List;

    //玩家ID
public class RankingListRival {

    private int rid;
    //玩家pve进度
    private int pveProgresses;
    //玩家名字
    private String name;
    //玩家等级
    private int level;
    private List<Object> hero = new ArrayList<>();
    //玩家战斗力
    private int powerGuess;
    // 头像
    private String avatar;
    private String avatarBorder;
    private String societyName;
    private int star;
    private int hdPveProgresses;
    public RankingListRival() {
    }

    public RankingListRival(int rid, int pveProgresses) {
        this.rid = rid;
        this.pveProgresses = pveProgresses;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getPveProgresses() {
        return pveProgresses;
    }

    public void setPveProgresses(int pveProgresses) {
        this.pveProgresses = pveProgresses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Object> getHero() {
        return hero;
    }

    public void setHero(List<Object> hero) {
        this.hero = hero;
    }

    public int getPowerGuess() {
        return powerGuess;
    }

    public void setPowerGuess(int powerGuess) {
        this.powerGuess = powerGuess;
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

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getHdPveProgresses() {
        return hdPveProgresses;
    }

    public void setHdPveProgresses(int hdPveProgresses) {
        this.hdPveProgresses = hdPveProgresses;
    }
}
