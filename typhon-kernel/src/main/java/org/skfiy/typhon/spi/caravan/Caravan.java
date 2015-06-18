package org.skfiy.typhon.spi.caravan;

import org.skfiy.typhon.domain.AbstractChangeable;
import org.skfiy.typhon.util.DomainUtils;

public class Caravan extends AbstractChangeable {
    private String monger;
    private String race;
    private String txtId;

    public String getMonger() {
        return monger;
    }

    public Caravan() {}

    public Caravan(String monger, String race, String txtId) {
        this.monger = monger;
        this.race = race;
        this.txtId = txtId;
    }

    public void setMonger(String monger) {
        this.monger = monger;
        DomainUtils.firePropertyChange(this, "monger", this.monger);
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
        DomainUtils.firePropertyChange(this, "race", this.race);
    }

    public String getTxtId() {
        return txtId;
    }

    public void setTxtId(String txtId) {
        this.txtId = txtId;
        DomainUtils.firePropertyChange(this, "txtId", this.txtId);
    }

}
