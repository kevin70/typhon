package org.skfiy.typhon.spi.activity;

import org.skfiy.typhon.dobj.ItemDobj;

public class InviteReward {
    private int limit;
    private ItemDobj itemId;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ItemDobj getItemId() {
        return itemId;
    }

    public void setItemId(ItemDobj itemId) {
        this.itemId = itemId;
    }
}

