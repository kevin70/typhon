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
package org.skfiy.typhon.packet;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class HeroLottery extends Packet {

    private String iid;
    private int count;
    private boolean souled;

    public HeroLottery() {
    }

    public HeroLottery(String iid, int count) {
        this.iid = iid;
        this.count = count;
    }

    public HeroLottery(String iid, int count, boolean souled) {
        this(iid, count);
        this.souled = souled;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isSouled() {
        return souled;
    }

    public void setSouled(boolean souled) {
        this.souled = souled;
    }

    /**
     *
     * @param packet
     * @return
     */
    public static HeroLottery createResult(Packet packet) {
        HeroLottery result = new HeroLottery();
        result.setId(packet.getId());
        result.setType(Type.rs);
        return result;
    }

}
