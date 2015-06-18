package org.skfiy.typhon.spi.activity;

import org.skfiy.typhon.dobj.ItemDobj;

public class ItemsObject {

    private ItemDobj itemDobj;
    private int count;

    public ItemDobj getItemDobj() {
        return itemDobj;
    }

    public void setItemDobj(ItemDobj itemDobj) {
        this.itemDobj = itemDobj;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
