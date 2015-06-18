/*
 * Copyright 2014 The Skfiy Open Association.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skfiy.typhon.spi.ranking;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class RankingObject {

    private int rid;
    private int level;
    private int powerGuess;
    private int pveProgresses;
    private String avatar;
    private String avatarBorder;
    private int star;
    private int hdPveProgresses;

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPveProgresses() {
        return pveProgresses;
    }

    public void setPveProgresses(int pveProgresses) {
        this.pveProgresses = pveProgresses;
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

    public int getPowerGuess() {
        return powerGuess;
    }

    public void setPowerGuess(int powerGuess) {
        this.powerGuess = powerGuess;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getHdPveProgresses() {
        return hdPveProgresses;
    }

    public void setHdPveProgresses(int hdPveProgresses) {
        this.hdPveProgresses = hdPveProgresses;
    }
}
