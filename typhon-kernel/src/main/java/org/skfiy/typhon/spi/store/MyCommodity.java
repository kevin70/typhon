package org.skfiy.typhon.spi.store;

import org.skfiy.typhon.domain.AbstractIndexable;
import org.skfiy.typhon.util.DomainUtils;

public class MyCommodity extends AbstractIndexable {

    private String id;
    private int count;

    public MyCommodity() {
    }

    public MyCommodity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        DomainUtils.firePropertyChange(this, "count", count);
    }

}
