package org.skfiy.typhon.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.skfiy.typhon.util.DomainUtils;

public class Friend extends AbstractIndexable {

    private int rid;
    private String name;
    private int level;
    private String heroId;
    private int powerGuessSum;
    // 头像
    private String avatar;
    private String avatarBorder;
    private String societyName;

    public Friend() {}

    public Friend(int rid, String name, int level, int powerGuessSum, String heroId, String avater,
            String avatarBorder, String societyName) {
        this.rid = rid;
        this.name = name;
        this.level = level;
        this.heroId = heroId;
        this.powerGuessSum = powerGuessSum;
        this.avatar = avater;
        this.avatarBorder = avatarBorder;
        this.societyName = societyName;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        DomainUtils.firePropertyChange(this, "name", this.name);
    }

    public int getLevel() {
        return level;
    }

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public int getPowerGuessSum() {
        return powerGuessSum;
    }

    public void setPowerGuessSum(int powerGuessSum) {
        this.powerGuessSum = powerGuessSum;
        DomainUtils.firePropertyChange(this, "powerGuessSum", this.powerGuessSum);
    }

    public void setLevel(int level) {
        this.level = level;
        DomainUtils.firePropertyChange(this, "level", this.level);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Friend) {
            Friend f = (Friend) obj;
            return (rid == f.rid);
        }
        return false;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("rid", rid).append("name", name).append("level", level);
        return builder.toString();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        DomainUtils.firePropertyChange(this, "avatar", this.avatar);
    }

    public String getAvatarBorder() {
        return avatarBorder;
    }

    public void setAvatarBorder(String avatarBorder) {
        this.avatarBorder = avatarBorder;
        DomainUtils.firePropertyChange(this, "avatarBorder", this.avatarBorder);
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
        DomainUtils.firePropertyChange(this, "societyName", this.societyName);
    }
}
