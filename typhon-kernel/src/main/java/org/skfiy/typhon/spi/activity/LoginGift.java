package org.skfiy.typhon.spi.activity;

import java.util.ArrayList;
import java.util.List;


public class LoginGift {

    private String id;
    private List<ItemsObject> giftList= new ArrayList<>();
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ItemsObject> getGiftList() {
        return giftList;
    }

    public void setGiftList(List<ItemsObject> giftList) {
        this.giftList = giftList;
    }
}