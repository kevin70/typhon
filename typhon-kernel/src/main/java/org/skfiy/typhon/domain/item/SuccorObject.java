package org.skfiy.typhon.domain.item;

public class SuccorObject {
    private int rid;
    private long time;

    SuccorObject() {}

    public SuccorObject(int rid, long time) {
        this.rid = rid;
        this.time = time;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}
