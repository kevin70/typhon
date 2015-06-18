package org.skfiy.typhon.spi.activity;

import java.util.ArrayList;
import java.util.List;

public class RandomBoxs {
    private String id;
    private List<RandomBoxItem> items = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RandomBoxItem> getItems() {
        return items;
    }

    public void setItems(List<RandomBoxItem> items) {
        this.items = items;
    }

}
