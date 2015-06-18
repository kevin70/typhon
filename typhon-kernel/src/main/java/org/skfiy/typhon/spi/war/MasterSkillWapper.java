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
package org.skfiy.typhon.spi.war;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class MasterSkillWapper {

    private WarInfo warInfo;
    private Direction direction;
    private int midx;

    public MasterSkillWapper(WarInfo warInfo, Direction direction, int midx) {
        this.warInfo = warInfo;
        this.direction = direction;
        this.midx = midx;
    }

    public WarInfo getWarInfo() {
        return warInfo;
    }

    public void setWarInfo(WarInfo warInfo) {
        this.warInfo = warInfo;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getMidx() {
        return midx;
    }

    public void setMidx(int midx) {
        this.midx = midx;
    }

}
