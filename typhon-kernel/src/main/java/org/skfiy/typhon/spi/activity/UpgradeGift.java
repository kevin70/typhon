package org.skfiy.typhon.spi.activity;

import java.util.ArrayList;
import java.util.List;

public class UpgradeGift {
    private int levelLimit;
    private List<ItemsObject> items = new ArrayList<>();

    public int getLevelLimit() {
        return levelLimit;
    }

    public void setLevelLimit(int levelLimit) {
        this.levelLimit = levelLimit;
    }

    public List<ItemsObject> getItems() {
        return items;
    }

    public void setItems(List<ItemsObject> items) {
        this.items = items;
    }


}
