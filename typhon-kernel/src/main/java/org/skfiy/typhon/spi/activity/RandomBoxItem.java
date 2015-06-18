package org.skfiy.typhon.spi.activity;

import org.skfiy.typhon.dobj.ItemDobj;

public class RandomBoxItem {
    private ItemDobj id;
    private int count;
    private int pro;
    private int randBegain;
    private int randEnd;

    public ItemDobj getId() {
        return id;
    }

    public void setId(ItemDobj id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRandBegain() {
        return randBegain;
    }

    public void setRandBegain(int randBegain) {
        this.randBegain = randBegain;
    }

    public int getRandEnd() {
        return randEnd;
    }

    public void setRandEnd(int randEnd) {
        this.randEnd = randEnd;
    }

    public int getPro() {
        return pro;
    }

    public void setPro(int pro) {
        this.pro = pro;
    }
    
}
