package org.skfiy.typhon.spi.dargon;

public enum DargonEventEnum {

    /**
     * 固定战斗.
     */
    fixed_war(0),
    /**
     * 随机战斗.
     */
    random_war(1),
    /**
     * 问答.
     */
    question(2),
    /**
     * 宝箱.
     */
    box(3), 
    /**
     * 金币.
     */
    gold(4),
    /**
     * 幸运.
     */
    lucky(5),
    /**
     * 倒霉.
     */
    bad(6), 
    /**
     * 英雄/灵魂石.
     */
    hero_soul(7), 
    /**
     * 光芒万丈.
     */
    gmwz(8),
    /**
     * 体力.
     */
    vigor(9);

    private final int flag;

    DargonEventEnum(int flag) {
        this.flag = flag;
    }

    /**
     * 事件标识.
     * @return 标识
     */
    public int getFlag() {
        return flag;
    }
    
    /**
     * 
     * @param flag
     * @return
     */
    public static DargonEventEnum valueOf(int flag) {
        return values()[flag];
    }
    
}
