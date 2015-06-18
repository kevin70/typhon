package org.skfiy.typhon.spi.store;

import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.domain.CostType;

public class Commodity {

    private String id;
    private int level;
    private ItemDobj item;
    private CostType costType;
    private int cost;
    private int count;
    private int pos;
    private double discount = 1.0;

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public CostType getCostType() {
        return costType;
    }

    public void setCostType(CostType costType) {
        this.costType = costType;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
