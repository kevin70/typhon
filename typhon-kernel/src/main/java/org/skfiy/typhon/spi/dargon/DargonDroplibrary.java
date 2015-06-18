package org.skfiy.typhon.spi.dargon;

import org.skfiy.typhon.dobj.ItemDobj;

public class DargonDroplibrary {

    private ItemDobj itemDobj;
    private int porb;
    private int max;
    private int min;

    public ItemDobj getItemDobj() {
        return itemDobj;
    }

    public void setItemDobj(ItemDobj itemDobj) {
        this.itemDobj = itemDobj;
    }

    public int getPorb() {
        return porb;
    }

    public void setPorb(int prob) {
        this.porb = prob;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

}
