package org.skfiy.typhon.spi.caravan;

import java.util.ArrayList;
import java.util.List;

import org.skfiy.typhon.domain.AbstractIndexable;
import org.skfiy.typhon.util.DomainUtils;

public class CaravanInformation extends AbstractIndexable {

    // 商家表示
    private String id;
    private long time;
    private int level;
    private List<String> troops = new ArrayList<>();
    // 通商通道倍率
    private double costFactor;
    //职业倍率
    private double raceFactor;

    public String getId() {
        return id;
    }

    public CaravanInformation() {}

    public CaravanInformation(String id, long time, int level, List<String> troops, double costFactor,double raceFactor) {
        this.id = id;
        this.time = time;
        this.level = level;
        this.troops = troops;
        this.costFactor = costFactor;
        this.raceFactor=raceFactor;
    }

    public void setId(String id) {
        this.id = id;
        DomainUtils.firePropertyChange(this, "id", this.id);
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        DomainUtils.firePropertyChange(this, "time", this.time);
    }

    public List<String> getTroops() {
        return troops;
    }

    public void setTroops(List<String> troops) {
        this.troops = troops;
//        for (int i = 0; i < troops.size(); i++) {
//            this.troops.get(i).set(this, "troops", i);
//        }
//        DomainUtils.firePropertyChange(this, "troops", this.troops);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        DomainUtils.firePropertyChange(this, "level", this.level);
    }
    
    public double getCostFactor() {
        return costFactor;
    }

    public void setCostFactor(double costFactor) {
        this.costFactor = costFactor;
        DomainUtils.firePropertyChange(this, "costFactor", this.costFactor);
    }

    public double getRaceFactor() {
        return raceFactor;
    }

    public void setRaceFactor(double raceFactor) {
        this.raceFactor = raceFactor;
        DomainUtils.firePropertyChange(this, "raceFactor", this.raceFactor);
    }
}
