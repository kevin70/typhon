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
public class PacketNotice extends Packet {

    public static final int PVP_RANKING_TYPE = 1;
    public static final int TOP_UP_LUCKEY_DRAW = 2;

    private String name;
    private int ntype;
    private Object annex1;
    private Object annex2;
    private Object annex3;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNtype() {
        return ntype;
    }

    public void setNtype(int ntype) {
        this.ntype = ntype;
    }

    public Object getAnnex1() {
        return annex1;
    }

    public void setAnnex1(Object annex1) {
        this.annex1 = annex1;
    }

    public Object getAnnex2() {
        return annex2;
    }

    public void setAnnex2(Object annex2) {
        this.annex2 = annex2;
    }

    public Object getAnnex3() {
        return annex3;
    }

    public void setAnnex3(Object annex3) {
        this.annex3 = annex3;
    }

}
