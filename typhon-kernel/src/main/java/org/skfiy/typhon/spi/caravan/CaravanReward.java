package org.skfiy.typhon.spi.caravan;

import java.util.ArrayList;
import java.util.List;

import org.skfiy.typhon.spi.store.Commoditied;

public class CaravanReward {

    private int level;
    private List<Commoditied> reward = new ArrayList<>();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Commoditied> getReward() {
        return reward;
    }

    public void setReward(List<Commoditied> reward) {
        this.reward = reward;
    }
}
