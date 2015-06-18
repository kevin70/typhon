package org.skfiy.typhon.packet;

public class ItemSell extends Packet {
  
    private int pos;
    private int count;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
