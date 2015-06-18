package org.skfiy.typhon.packet;

public class SocietyBossHp {
    
    private int hp;
    private int atk;
    private String type;
    private String id;

    public SocietyBossHp() {}

    public SocietyBossHp(int hp, String type, String id,int atk) {
        this.hp = hp;
        this.type = type;
        this.id = id;
        this.atk = atk;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }
}

