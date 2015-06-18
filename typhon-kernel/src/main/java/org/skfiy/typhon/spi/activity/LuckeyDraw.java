package org.skfiy.typhon.spi.activity;

import org.skfiy.typhon.dobj.ItemDobj;

public class LuckeyDraw {

    private String id;
    private ItemDobj item;
    private int count;
    private int prob;
    private int randBegin;
    private int randEnd;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ItemDobj getItem() {
        return item;
    }

    public void setItem(ItemDobj item) {
        this.item = item;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getProb() {
        return prob;
    }

    public void setProb(int prob) {
        this.prob = prob;
    }

    public int getRandBegin() {
        return randBegin;
    }

    public void setRandBegin(int randBegin) {
        this.randBegin = randBegin;
    }

    public int getRandEnd() {
        return randEnd;
    }

    public void setRandEnd(int randEnd) {
        this.randEnd = randEnd;
    }

}
