package org.skfiy.typhon.packet;

public class GeneralPacket extends Packet {

    private Object val;
    private Object rid;
    private Object general;

    public Object getVal() {
        return val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    public Object getGeneral() {
        return general;
    }

    public void setGeneral(Object general) {
        this.general = general;
    }

    public Object getRid() {
        return rid;
    }

    public void setRid(Object rid) {
        this.rid = rid;
    }

}
