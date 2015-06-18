package org.skfiy.typhon.domain;

public class DargonObject {
    private String sid;
    private int count;

    public DargonObject()
    {
        
    }
    public DargonObject(String str , int count)
    {
        this.sid=str;
        this.count=count;
    }
    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
