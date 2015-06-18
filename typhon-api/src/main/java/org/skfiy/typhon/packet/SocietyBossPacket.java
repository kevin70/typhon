package org.skfiy.typhon.packet;

import java.util.ArrayList;
import java.util.List;

public class SocietyBossPacket extends Packet {
    public String bossId;
    public int fidx;
    public List<SocietyBossHp> societyBossHp = new ArrayList<>();

    public String getBossId() {
        return bossId;
    }

    public void setBossId(String bossId) {
        this.bossId = bossId;
    }

    public List<SocietyBossHp> getSocietyBossHp() {
        return societyBossHp;
    }

    public void setSocietyBossHp(List<SocietyBossHp> societyBossHp) {
        this.societyBossHp = societyBossHp;
    }

    public int getFidx() {
        return fidx;
    }

    public void setFidx(int fidx) {
        this.fidx = fidx;
    }

}
