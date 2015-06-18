package org.skfiy.typhon.spi.activity;

import java.util.ArrayList;
import java.util.List;

import org.skfiy.typhon.spi.hero.ExclusiveBuildInformation;


public class VipGift {
    private int value;
    private List<ExclusiveBuildInformation> items = new ArrayList<>();

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<ExclusiveBuildInformation> getItems() {
        return items;
    }

    public void setItems(List<ExclusiveBuildInformation> items) {
        this.items = items;
    }

}
