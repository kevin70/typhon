package org.skfiy.typhon.packet;

import java.util.ArrayList;
import java.util.List;

public class SchemePacket extends Packet {

    private String tid;
    private List<Scheme> schemes = new ArrayList<>();

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public List<Scheme> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<Scheme> schemes) {
        this.schemes = schemes;
    }
}
