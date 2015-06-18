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
package org.skfiy.typhon.spi.pvp;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PvpObject {

    private int rid;
    private boolean robot;

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public boolean isRobot() {
        return robot;
    }

    public void setRobot(boolean robot) {
        this.robot = robot;
    }

    @Override
    public int hashCode() {
        return rid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PvpObject) {
            PvpObject po = (PvpObject) obj;
            return (rid == po.rid);
        }
        return false;
    }

}
