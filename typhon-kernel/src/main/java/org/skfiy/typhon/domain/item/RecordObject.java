package org.skfiy.typhon.domain.item;

import org.skfiy.typhon.domain.AbstractIndexable;
import org.skfiy.typhon.util.DomainUtils;

public class RecordObject extends AbstractIndexable {
    private int count;
    private int state;

    RecordObject() {}

    public RecordObject(int index, int state) {
        this.count = index;
        this.state = state;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int index) {
        this.count = index;
        DomainUtils.firePropertyChange(this, "count", this.count);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        DomainUtils.firePropertyChange(this, "state", this.state);
    }
}
