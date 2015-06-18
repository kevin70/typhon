package org.skfiy.typhon.spi.pvp;

import java.util.List;

import org.skfiy.typhon.spi.pvp.PvpRobot.Hero;

public class DargonPvpRival extends PvpRival {
    private List<Hero> heros;
    private String societyName;

    public List<Hero> getHeros() {
        return heros;
    }

    public void setHeros(List<Hero> heros) {
        this.heros = heros;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

}
