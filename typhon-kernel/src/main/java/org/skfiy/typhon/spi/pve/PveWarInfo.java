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
package org.skfiy.typhon.spi.pve;

import java.util.ArrayList;
import java.util.List;
import org.skfiy.typhon.packet.PossibleAtlasloot;

/**
 *
 * @author Kevin
 */
 public class PveWarInfo {

    private String warId;
    private int subject;
    private int mode;
    private int cidx;
    private int pidx;
    private int fgidx;
    private Part part;
    private List<PossibleAtlasloot> atlasloots = new ArrayList<>();

    public String getWarId() {
        return warId;
    }

    public void setWarId(String warId) {
        this.warId = warId;
    }

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

    public int getFgidx() {
        return fgidx;
    }

    public void setFgidx(int fgidx) {
        this.fgidx = fgidx;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public List<PossibleAtlasloot> getAtlasloots() {
        return atlasloots;
    }

    public void setAtlasloots(List<PossibleAtlasloot> atlasloots) {
        this.atlasloots = atlasloots;
    }

    public void addAtlasloot(PossibleAtlasloot atlasloot) {
        this.atlasloots.add(atlasloot);
    }
    
    public void clearAtlasloots() {
        this.atlasloots.clear();
    }
}
