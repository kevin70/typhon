package org.skfiy.typhon.packet;

import java.util.ArrayList;
import java.util.List;

public class CaravanPacket extends Packet {
    private String costType;
    private List<String> heroes = new ArrayList<>();

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public List<String> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<String> heroes) {
        this.heroes = heroes;
    }

}
