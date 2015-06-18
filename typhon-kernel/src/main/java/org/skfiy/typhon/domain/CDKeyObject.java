package org.skfiy.typhon.domain;

import org.skfiy.typhon.packet.Platform;

public class CDKeyObject {

    private String cdkey;
    private Platform platform;
    private int batch;
    private long creationTime;
    private long beginTime;
    private long endTime;
    private String itemId;
    private int state;

    public String getCdkey() {
        return cdkey;
    }

    public void setCdkey(String cDKEY) {
        cdkey = cDKEY;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String id) {
        this.itemId = id;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
