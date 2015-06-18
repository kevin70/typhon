package org.skfiy.typhon.spi.task;

import org.skfiy.typhon.dobj.ItemDobj;

public class TaskObject {
    //条件
    private int terms;
    //体力奖励
    private int vigor;
    //钻石奖励
    private int diamond;
    //经验奖励
    private int exp;
    //铜币奖励
    private int copper;
    //数量
    private int count;
    //英雄灵魂石奖励
    private ItemDobj id;
    //体力刷新时间
    private Integer[] time;
    
    //pve任务
    private int level;
    private int pid;
    private int cid;
    private int subject;
    private int mode;
    public int getTerms() {
        return terms;
    }

    public void setTerms(int terms) {
        this.terms = terms;
    }
    public int getVigor() {
        return vigor;
    }

    public void setVigor(int vigor) {
        this.vigor = vigor;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getCopper() {
        return copper;
    }

    public void setCopper(int copper) {
        this.copper = copper;
    }

    public ItemDobj getId() {
        return id;
    }

    public void setId(ItemDobj id) {
        this.id = id;
    }

    public Integer[] getTime() {
        return time;
    }

    public void setTime(Integer[] time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    
     public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
}
