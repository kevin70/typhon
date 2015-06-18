package org.skfiy.typhon.spi.hero;

import java.util.ArrayList;
import java.util.List;

public class ExclusiveBuildLimit {
    private int levellimit;
    private int soulCounts;
    private String costType;
    private int factor;
    private int counts;
    private List<ExclusiveBuildInformation> costItems = new ArrayList<>();


    public int getLevellimit() {
        return levellimit;
    }

    public void setLevellimit(int levellimit) {
        this.levellimit = levellimit;
    }

    public int getSoulCounts() {
        return soulCounts;
    }

    public void setSoulCounts(int soulCounts) {
        this.soulCounts = soulCounts;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public List<ExclusiveBuildInformation> getCostItems() {
        return costItems;
    }

    public void setCostItems(List<ExclusiveBuildInformation> costItems) {
        this.costItems = costItems;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }
}
