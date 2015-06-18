/*
 * Copyright 2014 The Skfiy Open Association.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.skfiy.typhon.packet;

/**
 * 
 * @author Administrator
 */
public class PacketSuccor extends Packet {

    private int rid;
    private String name;
    private String iid;
    private int level;
    private String star;
    private int powerGuess;
    private int playerLevel;
    private int ladder;
    private String societyName;
    private String avatar;
    private String avatarBorder;

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public int getPowerGuess() {
        return powerGuess;
    }

    public void setPowerGuess(int powerGuess) {
        this.powerGuess = powerGuess;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLadder() {
        return ladder;
    }

    public void setLadder(int ladder) {
        this.ladder = ladder;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarBorder() {
        return avatarBorder;
    }

    public void setAvatarBorder(String avatarBorder) {
        this.avatarBorder = avatarBorder;
    }

    /**
     * 
     * @param packet
     * @return
     */
    public static PacketSuccor createResult(Packet packet) {
        PacketSuccor result = new PacketSuccor();
        Packet.assignResult(packet, result);
        return result;
    }

}
