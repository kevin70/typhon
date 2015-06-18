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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PvePacket extends Packet {

    private int subject;
    private int mode;
    private int cidx;
    private int pidx;
    private int sidx = -1;
    private int fgidx;
    private int star;
    
    private int fru1;
    private int fru2;
    private int fru3;
   //援军ID
    private int aid;

    // result
    private List<PossibleAtlasloot> atlasloots;

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getCidx() {
        return cidx;
    }

    public void setCidx(int cidx) {
        this.cidx = cidx;
    }

    public int getPidx() {
        return pidx;
    }

    public void setPidx(int pidx) {
        this.pidx = pidx;
    }

    public int getSidx() {
        return sidx;
    }

    public void setSidx(int sidx) {
        this.sidx = sidx;
    }

    public int getFgidx() {
        return fgidx;
    }

    public void setFgidx(int fgidx) {
        this.fgidx = fgidx;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getFru1() {
        return fru1;
    }

    public void setFru1(int fru1) {
        this.fru1 = fru1;
    }

    public int getFru2() {
        return fru2;
    }

    public void setFru2(int fru2) {
        this.fru2 = fru2;
    }

    public int getFru3() {
        return fru3;
    }

    public void setFru3(int fru3) {
        this.fru3 = fru3;
    }

    public List<PossibleAtlasloot> getAtlasloots() {
        return atlasloots;
    }

    public void setAtlasloots(List<PossibleAtlasloot> atlasloots) {
        addAtlasloots(atlasloots);
    }

    public void addAtlasloot(PossibleAtlasloot atlasloot) {
        if (this.atlasloots == null) {
            this.atlasloots = new ArrayList<>();
        }
        this.atlasloots.add(atlasloot);
    }

    public void addAtlasloots(List<PossibleAtlasloot> atlasloots) {
        if (atlasloots == null) {
            return;
        }
        
        if (this.atlasloots == null) {
            this.atlasloots = new ArrayList<>();
        }
        this.atlasloots.addAll(atlasloots);
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }
    
}
