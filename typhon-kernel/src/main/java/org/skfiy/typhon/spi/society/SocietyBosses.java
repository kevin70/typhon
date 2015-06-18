package org.skfiy.typhon.spi.society;


public class SocietyBosses {
    private String id;
    private int energy;
    private String[] dogfaces;
    private String[] luckyHeros;
    private String sid;
    private String[] rewards;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public String[] getDogfaces() {
        return dogfaces;
    }

    public void setDogfaces(String[] dogfaces) {
        this.dogfaces = dogfaces;
    }

    public String[] getLuckyHeros() {
        return luckyHeros;
    }

    public void setLuckyHeros(String[] luckyHeros) {
        this.luckyHeros = luckyHeros;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String[] getRewards() {
        return rewards;
    }

    public void setRewards(String[] rewards) {
        this.rewards = rewards;
    }
}
