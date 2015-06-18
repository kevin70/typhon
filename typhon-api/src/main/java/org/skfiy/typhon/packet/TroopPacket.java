package org.skfiy.typhon.packet;

public class TroopPacket extends Packet {

    private int pos;
    private int target;
    private String casernType;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getCasernType() {
        return casernType;
    }

    public void setCasernType(String casernType) {
        this.casernType = casernType;
    }

}
