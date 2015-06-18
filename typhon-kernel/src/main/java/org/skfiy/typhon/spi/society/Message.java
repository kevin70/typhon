package org.skfiy.typhon.spi.society;

public class Message {

    private long time;
    private String heroOne;
    private String heroTwo;
    private String name;

    
    public Message() {

    };

    public Message(long time, String name, String heroOne, String heroTwo) {
        this.time = time;
        this.name = name;
        this.heroOne = heroOne;
        this.heroTwo = heroTwo;
    }
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getHeroOne() {
        return heroOne;
    }

    public void setHeroOne(String heroOne) {
        this.heroOne = heroOne;
    }

    public String getHeroTwo() {
        return heroTwo;
    }

    public void setHeroTwo(String heroTwo) {
        this.heroTwo = heroTwo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
