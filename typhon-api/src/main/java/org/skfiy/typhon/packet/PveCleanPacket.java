package org.skfiy.typhon.packet;

import java.util.ArrayList;
import java.util.List;

public class PveCleanPacket extends PvePacket {

    private int count = 1;
    private List<List<PossibleAtlasloot>> allAtlasloots;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<List<PossibleAtlasloot>> getAllAtlasloots() {
        return allAtlasloots;
    }

    public void setAllAtlasloots(List<List<PossibleAtlasloot>> allAtlasloots) {
        this.allAtlasloots = allAtlasloots;
    }


    public void addAllAtlasloots(List<PossibleAtlasloot> atlaslooties) {
        if (this.allAtlasloots == null) {
            this.allAtlasloots = new ArrayList<>();
        }
        this.allAtlasloots.add(atlaslooties);
    }
}
