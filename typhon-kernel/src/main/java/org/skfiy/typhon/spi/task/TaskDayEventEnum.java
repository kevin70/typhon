package org.skfiy.typhon.spi.task;


public enum TaskDayEventEnum {
    /**
     * 普通副本
     */
    hpve(0),
    /**
     * 精英副本.
     */
    hdpve(1),
    /**
     * 列传副本.
     */
    spve(2),
    /**
     * 竞技场.
     */
    pvp(3),
    /**
     * 附魔.
     */
    enchant(4),
    /**
     * 龙脉.
     */
    dargon(5),
    /**
     * 抽奖.
     */
    lotteries(6),
    /**
     * 活动.
     */
    activities(7),
    /**
     * 体力.
     */
    vigor(8),
    /**
     * 摇钱树.
     */
    tree(9),
    /**
     * 站位强化.
     */
    troopStreng(10),
    /**
     * 锦囊强化.
     */
    hardenStreng(11),
    /**
     * 月卡.
     */
    monthCard(12),
    /**
     * 公会boss.
     */
    societyBoss(13),
    /**
     * 通商.
     */
    caravan(14);



    private int flag;

    TaskDayEventEnum(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public static TaskDayEventEnum valueOf(int flag) {
        return values()[flag];
    }
}
