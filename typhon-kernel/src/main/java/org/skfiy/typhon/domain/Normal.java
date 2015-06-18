/*
 * Copyright 2013 The Skfiy Open Association.
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.skfiy.typhon.domain.item.MonthCardObject;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.domain.item.SuccorObject;
import org.skfiy.typhon.domain.item.TaskPveProgressObject;
import org.skfiy.typhon.spi.caravan.Caravan;
import org.skfiy.typhon.spi.caravan.CaravanInformation;
import org.skfiy.typhon.spi.sign.SignDraw;
import org.skfiy.typhon.spi.store.MyCommodity;
import org.skfiy.typhon.util.DomainUtils;
import org.skfiy.util.Assert;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"level", "diamond"})
public class Normal extends AbstractChangeable implements ITroop {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private String lead;
    private Set<String> leadShots;
    private long lastResetTime;
    // 头像
    private String avatar;
    private String avatarBorder;
    private int vigor;
    private long lastRevigorTime;
    private int buyVigorCount;
    private FightGroup[] fightGroups;
    private int accDiamond;
    /**
     * 铜币.
     */
    private int copper;
    private int exp;
    private int freeCount;
    private boolean firstC10Lottery;
    private boolean firstD1Lottery;
    private boolean firstD10Lottery;
    /**
     * 钻石抽卡总次数.
     */
    private int diamondLotteryCount;
    /**
     * 最后铜币抽卡时间.
     */
    private long lastBuyCopperTime;
    private long lastBuyDiamondTime;
    /**
     * 最后使用的攻击组索引.
     */
    private int lastFidx;

    private final List<Friend> friends = new ArrayList<>();

    /**
     * 史实篇.
     */
    private List<PveProgress> hpveProgresses = new ArrayList<>();
    /**
     * 史实精英篇.
     */
    private List<PveProgress> hdpveProgresses = new ArrayList<>();
    /**
     * 列传篇.
     */
    private List<PveProgress> spveProgresses = new ArrayList<>();
    /**
     * 活动篇.
     */
    private List<PveProgress> apveProgresses = new ArrayList<>();

    /**
     * 最后登出时间.
     */
    private long lastLogoutTime;

    // 商店物品列表
    private List<MyCommodity> commodities = new ArrayList<>();
    // 商店刷新次数
    private int refreshCount;
    // 商店自动刷新时间
    private long lastRefreshStoreTime;
    // 功勋
    private int exploit;
    // PVP排名
    private int pvpRanking;
    // PVP历史最高排名
    private int pvpHighRanking;
    // PVP挑战次数
    private int pvpCount;
    // PVP挑战CD
    private long pvpCd;
    // 购买PVP次数
    private int pvpBuyCount;
    // PVP胜利次数
    private int pvpWinCounts;
    // PVP战报
    private List<PvpReport> pvpReports = new ArrayList<>();

    private int pvpStoreRecount;
    private long lastPvpRestoreTime;
    private List<MyCommodity> pvpCommodities = new ArrayList<>();

    // 五个部队的强化
    private List<Troop> troops = new ArrayList<>(5);

    {
        for (int i = 0; i < MAX_TROOP_SIZE; i++) {
            troops.add(new Troop());
            troops.get(i).set(this, "troops", i);

        }
    }

    // 锦囊点数
    private int texp;

    // 签到次数
    private List<SignDraw> signs = new ArrayList<>();
    // 当天签到表示一天一更新
    private int nowSign = 1;
    // 补签次数
    private int signed;
    //
    private int vipLevel;
    private int vipSavings;

    private List<Mail> mails = new ArrayList<>();
    // 号角数量
    private int hornNum;
    // 公会ID
    private int societyId;
    // 公会Name
    private String societyName;

    /**
     * 龙脉
     */
    // 商店刷新时间
    private long lastHyRestoreTime;
    // 龙脉商店物品列表
    private List<MyCommodity> dargonCommodities = new ArrayList<>();
    // 龙脉商店刷新次数
    private int refreshDargonStore;
    // 龙币
    private int dargonMoney;
    // 核心战斗
    private int nucleus;
    // 玩家每次龙脉可以走的步数
    private int dargonNumber;
    // 玩家每天可以龙脉次数
    private int dargonVipCount;

    // 玩家龙脉踩的格子索引
    private List<Dargon> dargonEvent = new ArrayList<>();
    // 玩家当前位置
    private int roleLocation;
    // 玩家购买次数
    private int buyDargonCounts;

    /**
     * 任务
     */
    // 主公等级下标索引
    private int roleLevel;
    // 武将个数下标索引
    private int heros;
    // Pve星星任务下标
    private int pveStar;
    // 星星个数
    private int pveStarCounts;
    // 武将图鉴奖励的下表索引
    private List<Integer> atlasHeros = new ArrayList<>();
    // 拉霸下表索引
    private List<RecordObject> taskPveCombos = new ArrayList<>();
    // 竞技场胜利下标
    private int pvpWins;
    // 龙脉单次金币任务
    private List<RecordObject> taskDargonMoney = new ArrayList<>();
    // pve任务
    private List<TaskPveProgressObject> pveProgressCounts = new ArrayList<>();
    // 龙脉单次金币
    private int onceDargonMoney;
    /**
     * 每日任务
     */
    private DailyTask dailyTask;

    private int taskHpveCounts;
    // 集市商店
    private Store marketStore;
    // 西域商人
    private Store westernStore;

    // 摇钱树次数
    private int cashCowCounts;
    // 改名字次数
    private int updateNameCounts;
    // 援军领取体力次数
    private List<RecordObject> aidReceiveCounts = new ArrayList<>();
    /**
     * 魂匣
     */
    // 周热点val=1，2，3，4
    private int soulCartridgeWeeks;
    // 记录玩家抽奖次数及状态
    private List<Integer> soulCartridges = new ArrayList<>();
    // 新手玩家登录七天礼包
    private List<Long> loginGift = new ArrayList<>();
    // 好友援军
    private List<SuccorObject> succors = new ArrayList<>();

    /**
     * 充值月卡.
     */
    private MonthCardObject monthCardObject;
    /**
     * 冲级礼包.
     */
    private List<RecordObject> upgradeGifts = new ArrayList<>();
    /**
     * 首充礼包.
     */
    private boolean firstRecharge;
    /**
     * 图鉴.
     */
    private Set<String> equipmentAtlas = new HashSet<>();
    /**
     * 首充翻倍.
     */
    private List<Integer> vipRechargingFlags = new ArrayList<>();

    // 许愿次数
    private int societyWishs;

    // 成长基金
    private List<RecordObject> growthFund = new ArrayList<>();

    // 公会币
    private int societyMoney;
    // 公会商店物品列表
    private List<MyCommodity> societyCommodities = new ArrayList<>();
    // 公会刷新次数
    private int societyRefreshCounts;
    // 公会上次上次刷新时间
    private long lastSocietyRestoreTime;

    // 邀请码
    private int inviteNumber;
    // 邀请人
    private boolean invite;
    // 邀请领取奖励
    private List<RecordObject> inviteRewards = new ArrayList<>();

    // 充值抽奖每天免费次数
    private boolean luckeyDrawFree;
    // 充值抽奖次数
    private int luckeyDrawCounts;
    // 是否打过boss
    private int societyBossCounts;
    // 攻打BossCD结束时间
    private long societyPveBossCD;
    // 退工会24小时限制
    private long societyLeaveTime;

    /**
     * 商队
     */
    // 商队信息
    private List<CaravanInformation> caravans = new ArrayList<>();
    // 商人
    private Caravan caravan;

    // Vip每周可购买礼包
    private List<Integer> vipReceive = new ArrayList<>();
    //
    private int vipFreeGift = -1;

    // Vip活动期间内每天
    private List<Integer> vipReceiveActivity = new ArrayList<>();
    private int vipSavingsActivity;
    private List<Integer> vipReceiveDay = new ArrayList<>();
    private int vipSavingsDay;
    // 印花每天购买数量
    private int stampBuyLimit;
    // 积分刷新
    private boolean integral;

    /**
     * 
     * @return
     */
    public int getLevel() {
        return player().getRole().getLevel();
    }

    /**
     * 
     * @param level
     */
    public void setLevel(int level) {
        if (player() != null) {
            player().getRole().setLevel(level);
        }
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public Set<String> getLeadShots() {
        return leadShots;
    }

    public void setLeadShots(Set<String> leadShots) {
        this.leadShots = leadShots;
    }

    public boolean addLeadShot(String leadShot) {
        if (leadShots == null) {
            leadShots = new HashSet<>();
        }
        return this.leadShots.add(leadShot);
    }

    public long getLastResetTime() {
        return lastResetTime;
    }

    public void setLastResetTime(long lastResetTime) {
        this.lastResetTime = lastResetTime;
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

    public int getVigor() {
        return vigor;
    }

    /**
     * 
     * @param vigor
     */
    public synchronized void setVigor(int vigor) {
        if (this.vigor == vigor) {
            return;
        }

        int oldVigor = this.vigor;
        this.vigor = vigor;
        DomainUtils.firePropertyChange(this, "vigor", this.vigor);
        propertyChangeSupport.firePropertyChange("vigor", oldVigor, this.vigor);
    }

    public long getLastRevigorTime() {
        return lastRevigorTime;
    }

    public void setLastRevigorTime(long lastRevigorTime) {
        this.lastRevigorTime = lastRevigorTime;
        DomainUtils.firePropertyChange(this, "lastRevigorTime", this.lastRevigorTime);
    }

    public int getBuyVigorCount() {
        return buyVigorCount;
    }

    public void setBuyVigorCount(int buyVigorCount) {
        this.buyVigorCount = buyVigorCount;
        DomainUtils.firePropertyChange(this, "buyVigorCount", this.buyVigorCount);
    }

    /**
     * 
     * @return
     */
    public FightGroup[] getFightGroups() {
        return fightGroups;
    }

    /**
     * 
     * @param index
     * @return
     */
    public FightGroup getFightGroup(int index) {
        return fightGroups[index];
    }

    /**
     * 
     * @param fightGroups
     */
    public void setFightGroups(FightGroup[] fightGroups) {
        this.fightGroups = fightGroups;

        for (int i = 0; i < fightGroups.length; i++) {
            FightGroup fightGroup = fightGroups[i];
            fightGroup.set(this, "fightGroups", i);
        }
    }

    public int getAccDiamond() {
        return accDiamond;
    }

    public void setAccDiamond(int accDiamond) {
        this.accDiamond = accDiamond;
        DomainUtils.firePropertyChange(this, "accDiamond", this.accDiamond);
    }

    public int getCopper() {
        return copper;
    }

    public void setCopper(int copper) {
        this.copper = copper;
        DomainUtils.firePropertyChange(this, "copper", this.copper);
    }

    public int getFreeCount() {
        return freeCount;
    }

    public void setFreeCount(int freeCount) {
        this.freeCount = freeCount;
        DomainUtils.firePropertyChange(this, "freeCount", this.freeCount);
    }

    public boolean isFirstC10Lottery() {
        return firstC10Lottery;
    }

    public void setFirstC10Lottery(boolean firstC10Lottery) {
        this.firstC10Lottery = firstC10Lottery;
        DomainUtils.firePropertyChange(this, "firstC10Lottery", this.firstC10Lottery);
    }

    public boolean isFirstD1Lottery() {
        return firstD1Lottery;
    }

    public void setFirstD1Lottery(boolean firstD1Lottery) {
        this.firstD1Lottery = firstD1Lottery;
        DomainUtils.firePropertyChange(this, "firstD1Lottery", this.firstD1Lottery);
    }

    public boolean isFirstD10Lottery() {
        return firstD10Lottery;
    }

    public void setFirstD10Lottery(boolean firstD10Lottery) {
        this.firstD10Lottery = firstD10Lottery;
        DomainUtils.firePropertyChange(this, "firstD10Lottery", this.firstD10Lottery);
    }

    public int getDiamondLotteryCount() {
        return diamondLotteryCount;
    }

    public void setDiamondLotteryCount(int diamondLotteryCount) {
        this.diamondLotteryCount = diamondLotteryCount;
    }

    public long getLastBuyCopperTime() {
        return lastBuyCopperTime;
    }

    public void setLastBuyCopperTime(long lastBuyCopperTime) {
        this.lastBuyCopperTime = lastBuyCopperTime;
        DomainUtils.firePropertyChange(this, "lastBuyCopperTime", this.lastBuyCopperTime);
    }

    public long getLastBuyDiamondTime() {
        return lastBuyDiamondTime;
    }

    public void setLastBuyDiamondTime(long lastBuyDiamondTime) {
        this.lastBuyDiamondTime = lastBuyDiamondTime;
        DomainUtils.firePropertyChange(this, "lastBuyDiamondTime", this.lastBuyDiamondTime);
    }

    public long getLastLogoutTime() {
        return lastLogoutTime;
    }

    public void setLastLogoutTime(long lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;
    }

    public int getDiamond() {
        return player().getRole().getDiamond();
    }

    public void setDiamond(int diamond) {
        if (player() != null) {
            player().getRole().setDiamond(diamond);
        }
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
        DomainUtils.firePropertyChange(this, "exp", this.exp);
    }

    public List<Friend> getFriends() {
        return (new ArrayList<>(friends));
    }

    public void setFriends(List<Friend> friends) {
        this.friends.addAll(friends);
        for (int i = 0; i < friends.size(); i++) {
            friends.get(i).set(this, "friends", i);
        }
    }

    public void deleteFriend(int rid) {
        if (findFriend(rid) != null) {
            Friend f = findFriend(rid);

            this.friends.remove(f);

            for (int i = 0; i < friends.size(); i++) {
                this.friends.get(i).set(this, "friends", i);
            }
            DomainUtils.fireIndexPropertyRemove(f);
        }
    }

    public boolean addFriend(Friend friend) {
        boolean r = false;
        if (!friends.contains(friend)) {
            r = friends.add(friend);
            friend.set(this, "friends", friends.size() - 1);
            DomainUtils.fireIndexPropertyAdd(this, "friends", friend);
        }
        return r;
    }

    public Friend findFriend(int rid) {
        for (Friend f : friends) {
            if (f.getRid() == rid) {
                return f;
            }
        }
        return null;
    }

    public int getLastFidx() {
        return lastFidx;
    }

    public void setLastFidx(int lastFidx) {
        if (this.lastFidx == lastFidx) {
            return;
        }

        this.lastFidx = lastFidx;
        DomainUtils.firePropertyChange(this, "lastFidx", this.lastFidx);
    }

    public int getRefreshCount() {
        return refreshCount;
    }

    public void setRefreshCount(int commodityCount) {
        this.refreshCount = commodityCount;

        DomainUtils.firePropertyChange(this, "refreshCount", this.refreshCount);
    }

    public List<MyCommodity> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<MyCommodity> commodities) {
        this.commodities = commodities;

        for (int i = 0; i < this.commodities.size(); i++) {
            this.commodities.get(i).set(this, "commodities", i);
        }

        DomainUtils.firePropertyChange(this, "commodities", this.commodities);
    }

    public List<PveProgress> getHpveProgresses() {
        return hpveProgresses;
    }

    public void addHpveProgress(PveProgress pveProgress) {
        Assert.notNull(pveProgress);

        pveProgress.set(this, "hpveProgresses", hpveProgresses.size());
        this.hpveProgresses.add(pveProgress);
        DomainUtils.fireIndexPropertyAdd(this, "hpveProgresses", pveProgress);
    }

    public void setHpveProgresses(List<PveProgress> hpveProgresses) {
        this.hpveProgresses.addAll(hpveProgresses);

        int i = 0;
        for (PveProgress pveProgress : this.hpveProgresses) {
            pveProgress.set(this, "hpveProgresses", i++);
        }
    }

    public PveProgress findHpveProgress(int cidx, int pidx) {
        PveProgress rs = null;

        for (int i = pidx; i < hpveProgresses.size(); i++) {
            rs = hpveProgresses.get(i);
            if (rs.getCidx() == cidx && rs.getPidx() == pidx) {
                break;
            } else {
                rs = null;
            }
        }
        return rs;
    }

    public List<PveProgress> getHdpveProgresses() {
        return hdpveProgresses;
    }

    public void addHdpveProgress(PveProgress pveProgress) {
        Assert.notNull(pveProgress);

        pveProgress.set(this, "hdpveProgresses", hdpveProgresses.size());
        this.hdpveProgresses.add(pveProgress);
        DomainUtils.fireIndexPropertyAdd(this, "hdpveProgresses", pveProgress);
    }

    public PveProgress findHdpveProgress(int cidx, int pidx) {
        PveProgress rs = null;

        for (int i = pidx; i < hdpveProgresses.size(); i++) {
            rs = hdpveProgresses.get(i);
            if (rs.getCidx() == cidx && rs.getPidx() == pidx) {
                break;
            } else {
                rs = null;
            }
        }
        return rs;
    }

    public void setHdpveProgresses(List<PveProgress> hdpveProgresses) {
        this.hdpveProgresses.addAll(hdpveProgresses);

        int i = 0;
        for (PveProgress pveProgress : this.hdpveProgresses) {
            pveProgress.set(this, "hdpveProgresses", i++);
        }
    }

    public List<PveProgress> getSpveProgresses() {
        return spveProgresses;
    }

    public void setSpveProgresses(List<PveProgress> spveProgresses) {
        this.spveProgresses.addAll(spveProgresses);

        int i = 0;
        for (PveProgress pveProgress : this.spveProgresses) {
            pveProgress.set(this, "spveProgresses", i++);
        }
    }

    public void addSpveProgress(PveProgress pveProgress) {
        Assert.notNull(pveProgress);

        pveProgress.set(this, "spveProgresses", spveProgresses.size());
        this.spveProgresses.add(pveProgress);
        DomainUtils.fireIndexPropertyAdd(this, "spveProgresses", pveProgress);
    }

    public PveProgress findSpveProgress(int cidx, int pidx) {
        PveProgress rs = null;
        for (int i = pidx; i < spveProgresses.size(); i++) {
            rs = spveProgresses.get(i);
            if (rs.getCidx() == cidx && rs.getPidx() == pidx) {
                break;
            } else {
                rs = null;
            }
        }
        return rs;
    }

    public List<PveProgress> getApveProgresses() {
        return apveProgresses;
    }

    public void setApveProgresses(List<PveProgress> apveProgresses) {
        this.apveProgresses = new ArrayList<>(apveProgresses);

        int i = 0;
        for (PveProgress pveProgress : this.apveProgresses) {
            pveProgress.set(this, "apveProgresses", i++);
        }
    }

    public void addApveProgress(PveProgress pveProgress) {
        Assert.notNull(pveProgress);

        pveProgress.set(this, "apveProgresses", apveProgresses.size());
        this.apveProgresses.add(pveProgress);
        DomainUtils.fireIndexPropertyAdd(this, "apveProgresses", pveProgress);
    }

    public PveProgress findApveProgress(int cidx) {
        PveProgress rs = null;
        for (int i = 0; i < apveProgresses.size(); i++) {
            rs = apveProgresses.get(i);
            if (rs.getCidx() == cidx) {
                break;
            } else {
                rs = null;
            }
        }
        return rs;
    }

    public long getLastRefreshStoreTime() {
        return lastRefreshStoreTime;
    }

    public void setLastRefreshStoreTime(long lastRefreshStoreTime) {
        this.lastRefreshStoreTime = lastRefreshStoreTime;
    }

    public int getExploit() {
        return exploit;
    }

    public void setExploit(int exploit) {
        this.exploit = exploit;
        DomainUtils.firePropertyChange(this, "exploit", this.exploit);
    }

    public int getPvpRanking() {
        return pvpRanking;
    }

    public void setPvpRanking(int pvpRanking) {
        this.pvpRanking = pvpRanking;
        DomainUtils.firePropertyChange(this, "pvpRanking", this.pvpRanking);
    }

    public int getPvpHighRanking() {
        return pvpHighRanking;
    }

    public void setPvpHighRanking(int pvpHighRanking) {
        this.pvpHighRanking = pvpHighRanking;
        DomainUtils.firePropertyChange(this, "pvpHighRanking", this.pvpHighRanking);
    }

    public int getPvpCount() {
        return pvpCount;
    }

    public void setPvpCount(int pvpCount) {
        this.pvpCount = pvpCount;
        DomainUtils.firePropertyChange(this, "pvpCount", this.pvpCount);
    }

    public long getPvpCd() {
        return pvpCd;
    }

    public void setPvpCd(long pvpCd) {
        this.pvpCd = pvpCd;
        DomainUtils.firePropertyChange(this, "pvpCd", this.pvpCd);
    }

    public int getPvpBuyCount() {
        return pvpBuyCount;
    }

    public void setPvpBuyCount(int pvpBuyCount) {
        this.pvpBuyCount = pvpBuyCount;
        DomainUtils.firePropertyChange(this, "pvpBuyCount", this.pvpBuyCount);
    }

    public int getPvpWinCounts() {
        return pvpWinCounts;
    }

    public void setPvpWinCounts(int pvpWinCounts) {
        this.pvpWinCounts = pvpWinCounts;
        DomainUtils.firePropertyChange(this, "pvpWinCounts", this.pvpWinCounts);
    }

    public List<PvpReport> getPvpReports() {
        return pvpReports;
    }

    public void setPvpReports(List<PvpReport> pvpReports) {
        this.pvpReports = pvpReports;
        for (int i = 0; i < pvpReports.size(); i++) {
            pvpReports.get(i).set(this, "pvpReports", i);
        }
    }

    public boolean addPvpReport(PvpReport pvpReport) {
        if (pvpReports.size() >= 10) {
            PvpReport pr = pvpReports.remove(0);

            for (int i = 0; i < pvpReports.size(); i++) {
                pvpReports.get(i).set(this, "pvpReports", i);
            }
            DomainUtils.fireIndexPropertyRemove(pr);
        }

        pvpReport.set(this, "pvpReports", pvpReports.size());
        boolean r = pvpReports.add(pvpReport);
        DomainUtils.fireIndexPropertyAdd(this, "pvpReports", pvpReport);

        return r;
    }

    public int getPvpStoreRecount() {
        return pvpStoreRecount;
    }

    public void setPvpStoreRecount(int pvpStoreRecount) {
        this.pvpStoreRecount = pvpStoreRecount;
        DomainUtils.firePropertyChange(this, "pvpStoreRecount", this.pvpStoreRecount);
    }

    public long getLastPvpRestoreTime() {
        return lastPvpRestoreTime;
    }

    public void setLastPvpRestoreTime(long lastPvpRestoreTime) {
        this.lastPvpRestoreTime = lastPvpRestoreTime;
        DomainUtils.firePropertyChange(this, "lastPvpRestoreTime", this.lastPvpRestoreTime);
    }

    public List<MyCommodity> getPvpCommodities() {
        return pvpCommodities;
    }

    public void setPvpCommodities(List<MyCommodity> pvpCommodities) {
        this.pvpCommodities = pvpCommodities;

        for (int i = 0; i < this.pvpCommodities.size(); i++) {
            this.pvpCommodities.get(i).set(this, "pvpCommodities", i);
        }

        DomainUtils.firePropertyChange(this, "pvpCommodities", this.pvpCommodities);
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

        for (int i = 0; i < this.troops.size(); i++) {
            troops.get(i).set(this, "troops", i);
        }

        DomainUtils.firePropertyChange(this, "troops", this.troops);
    }

    public int getTexp() {
        return texp;
    }

    public void setTexp(int texp) {
        this.texp = texp;
        DomainUtils.firePropertyChange(this, "texp", this.texp);
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
        DomainUtils.firePropertyChange(this, "vipLevel", this.vipLevel);
    }

    public int getVipSavings() {
        return vipSavings;
    }

    public void setVipSavings(int vipSavings) {
        this.vipSavings = vipSavings;
        DomainUtils.firePropertyChange(this, "vipSavings", this.vipSavings);
    }

    public int getNowSign() {
        return nowSign;
    }

    public void setNowSign(int nowSign) {
        this.nowSign = nowSign;
        DomainUtils.firePropertyChange(this, "nowSign", this.nowSign);
    }

    public int getSigned() {
        return signed;
    }

    public void setSigned(int signed) {
        this.signed = signed;
        DomainUtils.firePropertyChange(this, "signed", this.signed);
    }

    public List<SignDraw> getSigns() {
        return signs;
    }

    public void setSigns(List<SignDraw> signs) {
        this.signs.addAll(signs);
        for (int i = 0; i < signs.size(); i++) {
            signs.get(i).set(this, "signs", i);
        }
    }

    public void addSigns(SignDraw signdraw) {
        this.signs.add(signdraw);
        signdraw.set(this, "signs", signs.size() - 1);
        DomainUtils.fireIndexPropertyAdd(this, "signs", signdraw);
    }

    /**
     * 
     * @return
     */
    public List<Mail> getMails() {
        return mails;
    }

    /**
     * 
     * @param mails
     */
    public void setMails(List<Mail> mails) {
        this.mails = mails;

        Mail m;
        for (int i = 0; i < mails.size(); i++) {
            m = mails.get(i);
            m.set(this, "mails", i);
        }
    }

    /**
     * 
     * @return
     */
    public int getMailSize() {
        return mails.size();
    }

    /**
     * 
     * @param i
     * @return
     */
    public Mail getMail(int i) {
        return mails.get(i);
    }

    /**
     * 
     * @param mail
     */
    public void addMail(Mail mail) {
        mail.set(this, "mails", mails.size());

        this.mails.add(mail);
        DomainUtils.fireIndexPropertyAdd(this, "mails", mail);
    }

    /**
     * 
     * @param mail
     * @return
     */
    public boolean removeMail(Mail mail) {
        boolean r = mails.remove(mail);
        for (int i = 0; i < mails.size(); i++) {
            mails.get(i).set(this, "mails", i);
        }
        DomainUtils.fireIndexPropertyRemove(mail);
        return r;
    }

    public int getHornNum() {
        return hornNum;
    }

    public void setHornNum(int hornNum) {
        this.hornNum = hornNum;
        DomainUtils.firePropertyChange(this, "hornNum", this.hornNum);
    }

    public int getSocietyId() {
        return societyId;
    }

    public void setSocietyId(int societyId) {
        this.societyId = societyId;
        DomainUtils.firePropertyChange(this, "societyId", this.societyId);
    }

    /**
     * 
     * @param propertyName
     * @param listener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public int getDargonNumber() {
        return dargonNumber;
    }

    public void setDargonNumber(int dargonNumber) {
        this.dargonNumber = dargonNumber;
        DomainUtils.firePropertyChange(this, "dargonNumber", this.dargonNumber);
    }

    public void changeDargonNumber(int number) {
        this.setDargonNumber(this.getDargonNumber() + number);
    }

    public int getDargonVipCount() {
        return dargonVipCount;
    }

    public void setDargonVipCount(int dargonVipCount) {
        this.dargonVipCount = dargonVipCount;
        DomainUtils.firePropertyChange(this, "dargonVipCount", this.dargonVipCount);
    }

    public void addDargonVipCount(int dargonVipCount) {
        this.setDargonVipCount(this.dargonVipCount + dargonVipCount);
    }

    public int getDargonMoney() {
        return dargonMoney;
    }

    public void setDargonMoney(int dargonMoney) {
        this.dargonMoney = dargonMoney;
        DomainUtils.firePropertyChange(this, "dargonMoney", this.dargonMoney);
    }

    public int getRefreshDargonStore() {
        return refreshDargonStore;
    }

    public void setRefreshDargonStore(int refreshDargonStore) {
        this.refreshDargonStore = refreshDargonStore;
        DomainUtils.firePropertyChange(this, "refreshDargonStore", this.refreshDargonStore);
    }

    public List<MyCommodity> getDargonCommodities() {
        return dargonCommodities;
    }

    public void setDargonCommodities(List<MyCommodity> dargonCommodity) {
        this.dargonCommodities = dargonCommodity;

        for (int i = 0; i < this.dargonCommodities.size(); i++) {
            this.dargonCommodities.get(i).set(this, "dargonCommodities", i);
        }

        DomainUtils.firePropertyChange(this, "dargonCommodities", this.dargonCommodities);
    }

    public long getLastHyRestoreTime() {
        return lastHyRestoreTime;
    }

    public void setLastHyRestoreTime(long lastHyRestoreTime) {
        this.lastHyRestoreTime = lastHyRestoreTime;
    }

    public List<RecordObject> getTaskPveCombos() {
        return taskPveCombos;
    }

    public void setTaskPveCombos(List<RecordObject> pveCombos) {
        this.taskPveCombos = pveCombos;
        for (int i = 0; i < this.taskPveCombos.size(); i++) {
            this.taskPveCombos.get(i).set(this, "taskPveCombos", i);
        }

        DomainUtils.firePropertyChange(this, "taskPveCombos", this.taskPveCombos);
    }

    public void addTaskPveCombos(RecordObject pveCombos) {
        Assert.notNull(pveCombos);
        pveCombos.set(this, "taskPveCombos", taskPveCombos.size());
        this.taskPveCombos.add(pveCombos);
        DomainUtils.fireIndexPropertyAdd(this, "taskPveCombos", pveCombos);
    }

    public List<RecordObject> getTaskDargonMoney() {
        return taskDargonMoney;
    }

    public void setTaskDargonMoney(List<RecordObject> taskDargonMoney) {
        this.taskDargonMoney = taskDargonMoney;
        for (int i = 0; i < this.taskDargonMoney.size(); i++) {
            this.taskDargonMoney.get(i).set(this, "taskDargonMoney", i);
        }
        DomainUtils.firePropertyChange(this, "taskDargonMoney", this.taskDargonMoney);
    }

    public void addTaskDargonMoney(RecordObject RecordObject) {
        Assert.notNull(taskDargonMoney);
        RecordObject.set(this, "taskDargonMoney", taskDargonMoney.size());
        this.taskDargonMoney.add(RecordObject);
        DomainUtils.fireIndexPropertyAdd(this, "taskDargonMoney", RecordObject);
    }

    public int getOnceDargonMoney() {
        return onceDargonMoney;
    }

    public void setOnceDargonMoney(int onceDargonMoney) {
        this.onceDargonMoney = onceDargonMoney;
    }

    public int getRoleLevel() {
        return roleLevel;

    }

    public void setRoleLevel(int roleLevel) {
        this.roleLevel = roleLevel;
        DomainUtils.firePropertyChange(this, "roleLevel", this.roleLevel);
    }

    public void setHeros(int heros) {
        this.heros = heros;
        DomainUtils.firePropertyChange(this, "heros", this.heros);
    }

    public List<Integer> getAtlasHeros() {
        return atlasHeros;
    }

    public void setAtlasHeros(List<Integer> atlasHeros) {
        this.atlasHeros = atlasHeros;
    }

    public void addAtlasHeros(int atlasHero) {
        this.atlasHeros.add(atlasHero);
        DomainUtils.fireIndexPropertyAdd(this, "atlasHeros", atlasHero);
    }

    public void setPvpWins(int pvpWins) {
        this.pvpWins = pvpWins;
        DomainUtils.firePropertyChange(this, "pvpWins", this.pvpWins);
    }

    public int getHeros() {
        return heros;
    }

    public int getPvpWins() {
        return pvpWins;
    }

    /**
     * 日常任务
     * 
     * @return
     */
    public DailyTask getDailyTask() {
        return dailyTask;
    }

    public void setDailyTask(DailyTask dailyTask) {
        this.dailyTask = dailyTask;
        this.dailyTask.set(this, "dailyTask");
        DomainUtils.firePropertyChange(this, "dailyTask", this.dailyTask);
    }

    public int getTaskHpveCounts() {
        return taskHpveCounts;
    }

    public void setTaskHpveCounts(int taskHpveCounts) {
        this.taskHpveCounts = taskHpveCounts;
        DomainUtils.firePropertyChange(this, "taskHpveCounts", this.taskHpveCounts);
    }

    public Store getMarketStore() {
        return marketStore;
    }

    public void setMarketStore(Store marketStore) {
        this.marketStore = marketStore;
        if (marketStore != null) {
            this.marketStore.set(this, "marketStore");
            DomainUtils.firePropertyChange(this, "marketStore", this.marketStore);
        }
    }

    public Store getWesternStore() {
        return westernStore;
    }

    public void setWesternStore(Store westernStore) {
        this.westernStore = westernStore;
        if (westernStore != null) {
            this.westernStore.set(this, "westernStore");
            DomainUtils.firePropertyChange(this, "westernStore", this.westernStore);
        }
    }

    public int getNucleus() {
        return nucleus;
    }

    public void setNucleus(int nucleus) {
        this.nucleus = nucleus;
        DomainUtils.firePropertyChange(this, "nucleus", this.nucleus);
    }

    public List<Dargon> getDargonEvent() {
        return dargonEvent;
    }

    public void clearDargonEvent() {
        this.dargonEvent.clear();
        DomainUtils.firePropertyChange(this, "dargonEvent", this.dargonEvent);
    }

    public void setDargonEvent(List<Dargon> dargonEvents) {
        this.dargonEvent = dargonEvents;
        for (int i = 0; i < this.dargonEvent.size(); i++) {
            this.dargonEvent.get(i).set(this, "dargonEvent", i);
        }
        DomainUtils.firePropertyChange(this, "dargonEvent", this.dargonEvent);
    }

    public int getRoleLocation() {
        return roleLocation;
    }

    public void setRoleLocation(int roleLocation) {
        this.roleLocation = roleLocation;
        DomainUtils.firePropertyChange(this, "roleLocation", this.roleLocation);
    }

    public int getBuyDargonCounts() {
        return buyDargonCounts;
    }

    public void setBuyDargonCounts(int buyDargonCounts) {
        this.buyDargonCounts = buyDargonCounts;
        DomainUtils.firePropertyChange(this, "buyDargonCounts", this.buyDargonCounts);
    }

    public void setPveProgressCounts(List<TaskPveProgressObject> pveProgressCounts) {
        this.pveProgressCounts = pveProgressCounts;
        for (int i = 0; i < this.pveProgressCounts.size(); i++) {
            this.pveProgressCounts.get(i).set(this, "pveProgressCounts", i);
        }
        DomainUtils.firePropertyChange(this, "pveProgressCounts", this.pveProgressCounts);
    }

    public void addPveProgressCounts(TaskPveProgressObject object) {
        Assert.notNull(object);
        object.set(this, "pveProgressCounts", pveProgressCounts.size());
        this.pveProgressCounts.add(object);
        DomainUtils.fireIndexPropertyAdd(this, "pveProgressCounts", object);
    }

    public List<TaskPveProgressObject> getPveProgressCounts() {
        return pveProgressCounts;
    }

    public int getCashCowCounts() {
        return cashCowCounts;
    }

    public void setCashCowCounts(int cashCowCounts) {
        this.cashCowCounts = cashCowCounts;
        DomainUtils.firePropertyChange(this, "cashCowCounts", this.cashCowCounts);
    }

    public int getUpdateNameCounts() {
        return updateNameCounts;
    }

    public void setUpdateNameCounts(int updateNameCounts) {
        this.updateNameCounts = updateNameCounts;
        DomainUtils.firePropertyChange(this, "updateNameCounts", this.updateNameCounts);
    }

    public int getSoulCartridgeWeeks() {
        return soulCartridgeWeeks;
    }

    public void setSoulCartridgeWeeks(int soulCartridgeWeeks) {
        this.soulCartridgeWeeks = soulCartridgeWeeks;
    }

    public List<Integer> getSoulCartridges() {
        return soulCartridges;
    }

    public void setSoulCartridges(List<Integer> soulCartridges) {
        this.soulCartridges = soulCartridges;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
        DomainUtils.firePropertyChange(this, "societyName", this.societyName);
    }

    public List<RecordObject> getAidReceiveCounts() {
        return aidReceiveCounts;
    }

    public void setAidReceiveCounts(List<RecordObject> aidReceiveCounts) {
        this.aidReceiveCounts = aidReceiveCounts;
        for (int i = 0; i < this.aidReceiveCounts.size(); i++) {
            this.aidReceiveCounts.get(i).set(this, "aidReceiveCounts", i);
        }

        DomainUtils.firePropertyChange(this, "aidReceiveCounts", this.aidReceiveCounts);
    }

    public void AddAidReceiveCounts(RecordObject object) {
        Assert.notNull(object);
        object.set(this, "aidReceiveCounts", aidReceiveCounts.size());
        this.aidReceiveCounts.add(object);
        DomainUtils.fireIndexPropertyAdd(this, "aidReceiveCounts", object);
    }

    public List<Long> getLoginGift() {
        return loginGift;
    }

    public void addLoginGift(long index) {
        this.loginGift.add(index);
        DomainUtils.fireIndexPropertyAdd(this, "loginGift", index);
    }

    public void setLoginGift(List<Long> loginGift) {
        this.loginGift = loginGift;
        DomainUtils.firePropertyChange(this, "loginGift", this.loginGift);
    }

    public void removeAidAccessVigor(RecordObject object) {
        aidReceiveCounts.remove(object);
        for (int i = 0; i < aidReceiveCounts.size(); i++) {
            aidReceiveCounts.get(i).set(this, "aidReceiveCounts", i);
        }
        DomainUtils.fireIndexPropertyRemove(object);
    }

    public List<SuccorObject> getSuccors() {
        return succors;
    }

    public void setSuccors(List<SuccorObject> succors) {
        this.succors = succors;
    }

    public void addSuccors(SuccorObject succors) {
        this.succors.add(succors);
    }

    /**
     * 图鉴
     */

    public void addEquipmentAtlas(String equipmentId) {
        this.equipmentAtlas.add(equipmentId);
        DomainUtils.fireIndexPropertyAdd(this, "equipmentAtlas", equipmentId);
    }

    public Set<String> getEquipmentAtlas() {
        return equipmentAtlas;
    }

    public void setEquipmentAtlas(Set<String> equipmentAtlas) {
        this.equipmentAtlas = equipmentAtlas;
    }

    public int getPveStar() {
        return pveStar;
    }

    public void setPveStar(int pveStar) {
        this.pveStar = pveStar;
        DomainUtils.firePropertyChange(this, "pveStar", this.pveStar);
    }

    public int getPveStarCounts() {
        return pveStarCounts;
    }

    public void setPveStarCounts(int pveStarCounts) {
        this.pveStarCounts = pveStarCounts;
        DomainUtils.firePropertyChange(this, "pveStarCounts", this.pveStarCounts);
    }

    public MonthCardObject getMonthCardObject() {
        return monthCardObject;
    }

    public void setMonthCardObject(MonthCardObject monthCardObject) {
        this.monthCardObject = monthCardObject;
        this.monthCardObject.set(this, "monthCardObject");
        DomainUtils.firePropertyChange(this, "monthCardObject", this.monthCardObject);
    }

    public List<Integer> getVipRechargingFlags() {
        return vipRechargingFlags;
    }


    public List<RecordObject> getUpgradeGifts() {
        return upgradeGifts;
    }

    public void setUpgradeGifts(List<RecordObject> upgradeGifts) {
        this.upgradeGifts = upgradeGifts;
        for (int i = 0; i < this.upgradeGifts.size(); i++) {
            this.upgradeGifts.get(i).set(this, "upgradeGifts", i);
        }

        DomainUtils.firePropertyChange(this, "upgradeGifts", this.upgradeGifts);
    }

    public void addUpgradeGifts(RecordObject upgradeGift) {
        Assert.notNull(upgradeGift);
        upgradeGift.set(this, "upgradeGifts", upgradeGifts.size());
        this.upgradeGifts.add(upgradeGift);
        DomainUtils.fireIndexPropertyAdd(this, "upgradeGifts", upgradeGift);
    }

    public void setVipRechargingFlags(List<Integer> vipRechargingFlags) {
        this.vipRechargingFlags = vipRechargingFlags;
        DomainUtils.firePropertyChange(this, "vipRechargingFlags", this.vipRechargingFlags);
    }

    public void addVipRecharginFlag(int vipRechargingFlags) {
        this.vipRechargingFlags.add(vipRechargingFlags);
        DomainUtils.fireIndexPropertyAdd(this, "vipRechargingFlags", vipRechargingFlags);
    }

    public boolean containsVigRechargingFlag(int vipRechargingFlag) {
        return vipRechargingFlags.contains(vipRechargingFlag);
    }

    public boolean isFirstRecharge() {
        return firstRecharge;
    }

    public void setFirstRecharge(boolean firstRecharge) {
        this.firstRecharge = firstRecharge;
        DomainUtils.firePropertyChange(this, "firstRecharge", this.firstRecharge);
    }

    public int getSocietyWishs() {
        return societyWishs;
    }

    public void setSocietyWishs(int societyWishs) {
        this.societyWishs = societyWishs;
        DomainUtils.firePropertyChange(this, "societyWishs", this.societyWishs);
    }

    public List<RecordObject> getGrowthFund() {
        return growthFund;
    }

    public void setGrowthFund(List<RecordObject> growthFund) {
        this.growthFund = growthFund;
        for (int i = 0; i < this.growthFund.size(); i++) {
            this.growthFund.get(i).set(this, "growthFund", i);
        }
        DomainUtils.firePropertyChange(this, "growthFund", this.growthFund);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public int getSocietyMoney() {
        return societyMoney;
    }

    public void setSocietyMoney(int societyMoney) {
        this.societyMoney = societyMoney;
        DomainUtils.firePropertyChange(this, "societyMoney", this.societyMoney);
    }

    public List<MyCommodity> getSocietyCommodities() {
        return societyCommodities;
    }

    public void setSocietyCommodities(List<MyCommodity> societyCommodities) {
        this.societyCommodities = societyCommodities;

        for (int i = 0; i < this.societyCommodities.size(); i++) {
            this.societyCommodities.get(i).set(this, "societyCommodities", i);
        }

        DomainUtils.firePropertyChange(this, "societyCommodities", this.societyCommodities);
    }


    public int getSocietyRefreshCounts() {
        return societyRefreshCounts;
    }

    public void setSocietyRefreshCounts(int societyRefreshCounts) {
        this.societyRefreshCounts = societyRefreshCounts;
        DomainUtils.firePropertyChange(this, "societyRefreshCounts", this.societyRefreshCounts);
    }

    public long getLastSocietyRestoreTime() {
        return lastSocietyRestoreTime;
    }

    public void setLastSocietyRestoreTime(long lastSocietyRestoreTime) {
        this.lastSocietyRestoreTime = lastSocietyRestoreTime;
        DomainUtils.firePropertyChange(this, "lastSocietyRestoreTime", this.lastSocietyRestoreTime);
    }

    public boolean isInvite() {
        return invite;
    }

    public void setInvite(boolean invite) {
        this.invite = invite;
        DomainUtils.firePropertyChange(this, "invite", this.invite);
    }

    public List<RecordObject> getInviteRewards() {
        return inviteRewards;
    }

    public void setInviteRewards(List<RecordObject> inviteRewards) {
        this.inviteRewards = inviteRewards;
        for (int i = 0; i < this.inviteRewards.size(); i++) {
            this.inviteRewards.get(i).set(this, "inviteRewards", i);
        }
        DomainUtils.firePropertyChange(this, "inviteRewards", this.inviteRewards);
    }

    public void addInviteReward(RecordObject recordObject) {
        Assert.notNull(recordObject);

        recordObject.set(this, "inviteRewards", inviteRewards.size());
        this.inviteRewards.add(recordObject);
        DomainUtils.fireIndexPropertyAdd(this, "inviteRewards", recordObject);
    }

    public int getInviteNumber() {
        return inviteNumber;
    }

    public void setInviteNumber(int inviteNumber) {
        this.inviteNumber = inviteNumber;
        DomainUtils.firePropertyChange(this, "inviteNumber", this.inviteNumber);
    }

    public boolean isLuckeyDrawFree() {
        return luckeyDrawFree;
    }

    public void setLuckeyDrawFree(boolean luckeyDrawFree) {
        this.luckeyDrawFree = luckeyDrawFree;
        DomainUtils.firePropertyChange(this, "luckeyDrawFree", this.luckeyDrawFree);
    }

    public int getLuckeyDrawCounts() {
        return luckeyDrawCounts;
    }

    public void setLuckeyDrawCounts(int luckeyDrawCounts) {
        this.luckeyDrawCounts = luckeyDrawCounts;
        DomainUtils.firePropertyChange(this, "luckeyDrawCounts", this.luckeyDrawCounts);
    }

    public int getSocietyBossCounts() {
        return societyBossCounts;
    }

    public void setSocietyBossCounts(int societyBossCounts) {
        this.societyBossCounts = societyBossCounts;
        DomainUtils.firePropertyChange(this, "societyBossCounts", this.societyBossCounts);
    }

    public long getSocietyLeaveTime() {
        return societyLeaveTime;
    }

    public void setSocietyLeaveTime(long societyLeavelTime) {
        societyLeaveTime = societyLeavelTime;
        DomainUtils.firePropertyChange(this, "societyLeaveTime", this.societyLeaveTime);
    }

    public List<CaravanInformation> getCaravans() {
        return caravans;
    }

    public void setCaravans(List<CaravanInformation> caravans) {
        this.caravans = caravans;
        for (int i = 0; i < this.caravans.size(); i++) {
            this.caravans.get(i).set(this, "caravans", i);
        }

        DomainUtils.firePropertyChange(this, "caravans", this.caravans);
    }

    public void addCaravans(CaravanInformation caravan) {
        Assert.notNull(caravan);
        caravan.set(this, "caravans", caravans.size());
        this.caravans.add(caravan);
        DomainUtils.fireIndexPropertyAdd(this, "caravans", caravan);
    }

    public void deleteCaravans(int index) {
        if (caravans.get(index) != null) {
            CaravanInformation f = caravans.get(index);
            this.caravans.remove(f);

            for (int i = 0; i < caravans.size(); i++) {
                caravans.get(i).set(this, "caravans", i);
            }
            DomainUtils.fireIndexPropertyRemove(f);
        }
    }

    public Caravan getCaravan() {
        return caravan;
    }

    public void setCaravan(Caravan caravan) {
        this.caravan = caravan;
        this.caravan.set(this, "caravan");
        DomainUtils.firePropertyChange(this, "caravan", this.caravan);
    }

    public List<Integer> getVipReceive() {
        return vipReceive;
    }

    public void setVipReceive(List<Integer> vipReceive) {
        this.vipReceive = vipReceive;
        DomainUtils.firePropertyChange(this, "vipReceive", this.vipReceive);
    }

    public void addVipReceive(int vipReceive) {
        this.vipReceive.add(vipReceive);
        DomainUtils.fireIndexPropertyAdd(this, "vipReceive", vipReceive);
    }

    public int getVipFreeGift() {
        return vipFreeGift;
    }

    public void setVipFreeGift(int vipFreeGift) {
        this.vipFreeGift = vipFreeGift;
        DomainUtils.firePropertyChange(this, "vipFreeGift", vipFreeGift);
    }

    public List<Integer> getVipReceiveActivity() {
        return vipReceiveActivity;
    }

    public void setVipReceiveActivity(List<Integer> vipReceiveActivity) {
        this.vipReceiveActivity = vipReceiveActivity;
    }

    public void addVipReceiveActivity(int vipReceiveActivity) {
        this.vipReceiveActivity.add(vipReceiveActivity);
        DomainUtils.fireIndexPropertyAdd(this, "vipReceiveActivity", vipReceiveActivity);
    }

    public int getVipSavingsActivity() {
        return vipSavingsActivity;
    }

    public void setVipSavingsActivity(int vipSavingsActivity) {
        this.vipSavingsActivity = vipSavingsActivity;
        DomainUtils.firePropertyChange(this, "vipSavingsActivity", vipSavingsActivity);
    }

    public List<Integer> getVipReceiveDay() {
        return vipReceiveDay;
    }

    public void setVipReceiveDay(List<Integer> vipReceiveDay) {
        this.vipReceiveDay = vipReceiveDay;
    }

    public void addVipReceiveDay(int vipReceiveDay) {
        this.vipReceiveDay.add(vipReceiveDay);
        DomainUtils.fireIndexPropertyAdd(this, "vipReceiveDay", vipReceiveDay);
    }

    public int getVipSavingsDay() {
        return vipSavingsDay;
    }

    public void setVipSavingsDay(int vipSavingsDay) {
        this.vipSavingsDay = vipSavingsDay;
        DomainUtils.firePropertyChange(this, "vipSavingsDay", vipSavingsDay);
    }

    public int getStampBuyLimit() {
        return stampBuyLimit;
    }

    public void setStampBuyLimit(int stampBuyLimit) {
        this.stampBuyLimit = stampBuyLimit;
        DomainUtils.firePropertyChange(this, "stampBuyLimit", stampBuyLimit);
    }

    public long getSocietyPveBossCD() {
        return societyPveBossCD;
    }

    public void setSocietyPveBossCD(long societyPveBossCD) {
        this.societyPveBossCD = societyPveBossCD;
        DomainUtils.firePropertyChange(this, "societyPveBossCD", societyPveBossCD);
    }

    public boolean isIntegral() {
        return integral;
    }

    public void setIntegral(boolean integral) {
        this.integral = integral;
        DomainUtils.firePropertyChange(this, "integral", integral);
    }
}
