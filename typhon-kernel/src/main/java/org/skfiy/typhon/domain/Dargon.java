package org.skfiy.typhon.domain;

import org.skfiy.typhon.util.DomainUtils;

public class Dargon extends AbstractIndexable {

    private int flag;
    private int state;
    private Object hostInformation;
    private int nucleus;

    public Dargon() {
    }

    public Dargon(int flag) {
        this.flag = flag;
    }

    public Dargon(int flag, int nucleus) {
        this.flag = flag;
        this.nucleus = nucleus;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        DomainUtils.firePropertyChange(this, "state", this.state);
    }

    public Object getHostInformation() {
        return hostInformation;
    }

    public void setHostInformation(Object hostInformation) {
        this.hostInformation = hostInformation;
    }

    public int getNucleus() {
        return nucleus;
    }

    public void setNucleus(int nucleus) {
        this.nucleus = nucleus;
    }
}
