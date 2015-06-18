package org.skfiy.typhon.domain.item;

import org.skfiy.typhon.domain.AbstractChangeable;
import org.skfiy.typhon.util.DomainUtils;

public class MonthCardObject extends AbstractChangeable {
    private long receiveTime;
    private long expiredTime;

    public MonthCardObject() {}


    public MonthCardObject(long expiredTime, long receiveTime) {
        this.expiredTime = expiredTime;
        this.receiveTime = receiveTime;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long time) {
        this.receiveTime = time;
        DomainUtils.firePropertyChange(this, "receiveTime", this.receiveTime);
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long endTime) {
        this.expiredTime = endTime;
        DomainUtils.firePropertyChange(this, "expiredTime", this.expiredTime);
    }

}
