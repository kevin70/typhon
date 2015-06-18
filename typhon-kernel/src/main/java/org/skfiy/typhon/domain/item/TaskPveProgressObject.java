package org.skfiy.typhon.domain.item;

import java.util.ArrayList;
import java.util.List;

import org.skfiy.typhon.domain.AbstractIndexable;
import org.skfiy.typhon.util.DomainUtils;
import org.skfiy.util.Assert;

public class TaskPveProgressObject extends AbstractIndexable {

    private int tid;
    private List<RecordObject> pveGrowth = new ArrayList<>();

    public List<RecordObject> getPveGrowth() {
        return pveGrowth;
    }

    public void setPveGrowth(List<RecordObject> pveProgress) {
        this.pveGrowth = pveProgress;
        for (int i = 0; i < this.pveGrowth.size(); i++) {
            this.pveGrowth.get(i).set(this, "pveGrowth", i);
        }
        DomainUtils.firePropertyChange(this, "pveGrowth", this.pveGrowth);
    }

    public void addPveGrowth(RecordObject obj) {
        Assert.notNull(obj);
        obj.set(this, "pveGrowth", pveGrowth.size());
        this.pveGrowth.add(obj);
        DomainUtils.fireIndexPropertyAdd(this, "pveGrowth", obj);
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
        DomainUtils.firePropertyChange(this, "tid", this.tid);
    }
}
