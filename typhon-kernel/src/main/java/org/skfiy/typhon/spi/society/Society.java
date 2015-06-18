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
package org.skfiy.typhon.spi.society;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.skfiy.typhon.packet.SocietyBossHp;

/**
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Society implements Comparable<Society> {

    // 公会ID
    private int sid;
    // 公会名称
    private String name;
    // 公会图标
    private String icon;
    // 公会简介
    private String summary;
    // 会长角色ID
    private int chairmanRid;
    // 公会最大成员数
    private int maxMember = 50;
    // 玩家等级限制
    private int levelLimit = 32;
    // 公会成员
    private List<Member> members = new ArrayList<>();
    // 公会申请人员
    private List<Member> requestMembers = new ArrayList<>();
    // 公会记录
    private List<Record> records = new ArrayList<>();
    // 许愿Boss Key:BossID value:许愿个数
    private Map<String, Integer> societyWishCounts = new LinkedHashMap<>();
    // Boss剩余血量
    private List<List<SocietyBossHp>> societyBosses = new ArrayList<>();
    // 下个Boss开启时间
    private long bossTime;
    // 许愿记录
    private List<Message> messages = new ArrayList<>();
    // 公会boss伤害排行榜
    private List<MemberInformation> hurtRanking = new ArrayList<>();
    // 标识是否是第一次进入公会的许愿值加成
    private boolean addWishCount;


    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getChairmanRid() {
        return chairmanRid;
    }

    public void setChairmanRid(int chairmanRid) {
        this.chairmanRid = chairmanRid;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public int getLevelLimit() {
        return levelLimit;
    }

    public void setLevelLimit(int levelLimit) {
        this.levelLimit = levelLimit;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public boolean addMember(Member member) {
        return members.add(member);
    }

    public boolean removeMember(Member member) {
        return members.remove(member);
    }

    public Member findMember(int rid) {
        for (Member m : members) {
            if (m.getRid() == rid) {
                return m;
            }
        }
        return null;
    }

    public List<Member> getRequestMembers() {
        return requestMembers;
    }

    public void setRequestMembers(List<Member> requestMembers) {
        this.requestMembers = requestMembers;
    }

    public boolean addRequestMember(Member requestMember) {
        return requestMembers.add(requestMember);
    }

    public boolean removeRequestMember(Member requestMember) {
        return requestMembers.remove(requestMember);
    }

    public Member findRequestMember(int rid) {
        for (Member m : requestMembers) {
            if (m.getRid() == rid) {
                return m;
            }
        }
        return null;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    @Override
    public int compareTo(Society o) {
        return Integer.compare(o.members.size(), members.size());
    }

    public List<List<SocietyBossHp>> getSocietyBosses() {
        return societyBosses;
    }

    public void setSocietyBosses(List<List<SocietyBossHp>> societyBosses) {
        this.societyBosses = societyBosses;
    }

    public Map<String, Integer> getSocietyWishCounts() {
        return societyWishCounts;
    }

    public void setSocietyWishCounts(Map<String, Integer> societyWishCounts) {
        this.societyWishCounts = societyWishCounts;
    }

    public long getBossTime() {
        return bossTime;
    }

    public void setBossTime(long bossTime) {
        this.bossTime = bossTime;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<MemberInformation> getHurtRanking() {
        return hurtRanking;
    }

    public void setHurtRanking(List<MemberInformation> hurtRanking) {
        this.hurtRanking = hurtRanking;
    }

    public boolean isAddWishCount() {
        return addWishCount;
    }

    public void setAddWishCount(boolean addWishCount) {
        this.addWishCount = addWishCount;
    }

}
