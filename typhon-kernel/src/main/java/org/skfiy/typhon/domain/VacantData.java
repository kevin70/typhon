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
package org.skfiy.typhon.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.domain.item.RecordObject;

import com.alibaba.fastjson.annotation.JSONType;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"rid", "level", "name", "primaryHero"})
public class VacantData implements Serializable, ITroop {

    private int rid;
    private int level;
    private String name;
    private int lastFidx;
    private String[][] fightGroups;
    private int friendSize;
    private int vipLevel;
    // 头像
    private String avatar;
    private String avatarBorder;

    private List<Troop> troops;
    private List<RecordObject> aidReceiveCounts = new ArrayList<>();
    
    //队长位置索引
    private int captain;

    /**
     * 最后登出时间.
     */
    private long lastLogoutTime;
    //公会ID
    private int societyId;
    //公会Name
    private String societyName;
    private final Set<HeroProperty> heroProperties = new HashSet<>();
    /**
     * PVP援军武将ID.
     */
    private String pvpSuccorIid;

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLastFidx() {
        return lastFidx;
    }

    public void setLastFidx(int lastFidx) {
        this.lastFidx = lastFidx;
    }

    public String[][] getFightGroups() {
        return fightGroups;
    }

    public String[] getFightGroup(int i) {
        return fightGroups[i];
    }

    public void setFightGroups(String[][] fightGroups) {
        this.fightGroups = fightGroups;
    }

    public HeroProperty getPrimaryHero() {
        String id = fightGroups[lastFidx][captain];
        return findHeroProperty(id);
    }

    @Override
    public List<Troop> getTroops() {
        return troops;
    }

    @Override
    public Troop getTroop(Type type) {
        return troops.get(type.getPos());
    }

    @Override
    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }

    public long getLastLogoutTime() {
        return lastLogoutTime;
    }

    public void setLastLogoutTime(long lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public int getSocietyId() {
        return societyId;
    }

    public void setSocietyId(int societyId) {
        this.societyId = societyId;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public Set<HeroProperty> getHeroProperties() {
        return heroProperties;
    }

    public HeroProperty findHeroProperty(String id) {
        for (HeroProperty hero : heroProperties) {
            if (id.equals(hero.getId())) {
                return hero;
            }
        }
        throw new TyphonException("Not foudn HeroProperty[" + id + "]");
    }

    public void addHeroProperty(HeroProperty heroProperty) {
        this.heroProperties.add(heroProperty);
    }

    public void setHeroProperties(Set<HeroProperty> heroProperties) {
        this.heroProperties.addAll(heroProperties);
    }

    public String getPvpSuccorIid() {
        return pvpSuccorIid;
    }

    public void setPvpSuccorIid(String pvpSuccorIid) {
        this.pvpSuccorIid = pvpSuccorIid;
    }

    public int getFriendSize() {
        return friendSize;
    }

    public void setFriendSize(int friendSize) {
        this.friendSize = friendSize;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
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

    public List<RecordObject> getAidReceiveCounts() {
        return aidReceiveCounts;
    }

    public void setAidReceiveCounts(List<RecordObject> aidReceiveCounts) {
        this.aidReceiveCounts = aidReceiveCounts;
    }

    public void addAidReceiveCounts(RecordObject object) {
        this.aidReceiveCounts.add(object);
    }

    public int getCaptain() {
        return captain;
    }

    public void setCaptain(int captain) {
        this.captain = captain;
    }
}
