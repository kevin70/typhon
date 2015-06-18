package org.skfiy.typhon.spi.hero;

import java.util.ArrayList;
import java.util.List;

public class Herofactor {

    private List<Integer> exps = new ArrayList<>();
    private double factorNamber;
    private int level;

    public List<Integer> getExps() {
        return exps;
    }

    public void setExps(List<Integer> exps) {
        this.exps = exps;
    }

    public void addExps(int exp) {
        this.exps.add(exp);
    }

    public double getFactorNamber() {
        return factorNamber;
    }

    public void setFactorNamber(double factorName) {
        this.factorNamber = factorName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public enum starEnum {
        X1, X2, X3, X4, X5
    }
}
