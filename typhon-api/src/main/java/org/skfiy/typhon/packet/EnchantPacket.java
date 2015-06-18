package org.skfiy.typhon.packet;

import java.util.List;

public class EnchantPacket extends Packet {
    private int pos;
    private int point;
    private String eid;
    private List<Integer> expendables;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public List<Integer> getExpendables() {
        return expendables;
    }

    public void setExpendables(List<Integer> expendables) {
        this.expendables = expendables;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

}
