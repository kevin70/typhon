package org.skfiy.typhon.packet;

public class ExclusivePacket extends Packet {
    private String eid;
    private int level;

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}