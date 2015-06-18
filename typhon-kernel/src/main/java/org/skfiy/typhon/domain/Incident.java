package org.skfiy.typhon.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Incident {

    //对方UID=uid
    private int pid;
    private int uid;
    private String eventName;
    private String data;
    private long creationTime;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getData() {
        return data;
    }

    public JSONObject getJSONData() {
        return JSON.parseObject(getData());
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
