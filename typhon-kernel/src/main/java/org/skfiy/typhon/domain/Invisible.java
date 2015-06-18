/*
 * Copyright 2014 The Skfiy Open Association.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.skfiy.typhon.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.skfiy.typhon.domain.IHeroEntity.Rabbet;
import org.skfiy.typhon.spi.pve.PveWarInfo;

/**
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Invisible {

    private PveWarInfo pveWarInfo;
    private List<String> singleAtlasloots;
    private Set<Integer> dargonAdversary = new HashSet<>();
    private Map<Integer, String> drawCDKEYs = new HashMap<>();
    private int upGradeGift;
    private List<Integer> Invite = new ArrayList<>();
    private int inviteUid;
    private boolean inviteBool;
    private long vipActivityStarTime;

    public long getVipActivityStarTime() {
        return vipActivityStarTime;
    }

    public void setVipActivityStarTime(long vipActivityStarTime) {
        this.vipActivityStarTime = vipActivityStarTime;
    }

    private Rabbet rabbet;
    // Vip当日充值礼包
    private int vipDate;

    // "土豪抽"奖次数
    private int tuhaoLotteryCount;

    public PveWarInfo getPveWarInfo() {
        return pveWarInfo;
    }

    public void setPveWarInfo(PveWarInfo pveWarInfo) {
        this.pveWarInfo = pveWarInfo;
    }

    public List<String> getSingleAtlasloots() {
        return singleAtlasloots;
    }

    public void setSingleAtlasloots(List<String> singleAtlasloots) {
        this.singleAtlasloots = singleAtlasloots;
    }

    public Set<Integer> getDargonAdversary() {
        return dargonAdversary;
    }

    public void setDargonAdversary(Set<Integer> dargonAdversary) {
        this.dargonAdversary = dargonAdversary;
    }

    public Map<Integer, String> getDrawCDKEYs() {
        return drawCDKEYs;
    }

    public void setDrawCDKEYs(Map<Integer, String> drawCDKEYs) {
        this.drawCDKEYs = drawCDKEYs;
    }

    public void addDrawCDKEYs(int index, String values) {
        this.drawCDKEYs.put(index, values);
    }

    public int getTuhaoLotteryCount() {
        return tuhaoLotteryCount;
    }

    public void setTuhaoLotteryCount(int tuhaoLotteryCount) {
        this.tuhaoLotteryCount = tuhaoLotteryCount;
    }

    public int getUpGradeGift() {
        return upGradeGift;
    }

    public void setUpGradeGift(int upGradeGift) {
        this.upGradeGift = upGradeGift;
    }

    public List<Integer> getInvite() {
        return Invite;
    }

    public void setInvite(List<Integer> invite) {
        Invite = invite;
    }

    public int getInviteUid() {
        return inviteUid;
    }

    public void setInviteUid(int inviteUid) {
        this.inviteUid = inviteUid;
    }

    public boolean isInviteBool() {
        return inviteBool;
    }

    public void setInviteBool(boolean inviteBool) {
        this.inviteBool = inviteBool;
    }

    public Rabbet getRabbet() {
        return rabbet;
    }

    public void setRabbet(Rabbet rabbet) {
        this.rabbet = rabbet;
    }

    public int getVipDate() {
        return vipDate;
    }

    public void setVipDate(int vipDate) {
        this.vipDate = vipDate;
    }
}
