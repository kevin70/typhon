package org.skfiy.typhon.spi.caravan;

import java.util.ArrayList;
import java.util.List;

public class CaravanType {

    private String type;
    private List<CaravanReward> grade = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CaravanReward> getGrade() {
        return grade;
    }

    public void setGrade(List<CaravanReward> grade) {
        this.grade = grade;
    }

}
