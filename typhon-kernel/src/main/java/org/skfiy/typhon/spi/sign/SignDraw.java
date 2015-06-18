package org.skfiy.typhon.spi.sign;

import org.skfiy.typhon.domain.AbstractIndexable;
import org.skfiy.typhon.util.DomainUtils;

public class SignDraw extends AbstractIndexable {

    // 签到时领取的份数
    private int count;
    // 签到时领取的VIp等级
    private int lastVip;
    // 签到时间
    private int drawTime;

    public SignDraw() {

    }

    public SignDraw(int count, int drawTime, int lastVip) {
        this.count = count;
        this.lastVip = lastVip;
        this.drawTime = drawTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;

        DomainUtils.firePropertyChange(this, "count", this.count);
    }

    public int getLastVip() {
        return lastVip;
    }

    public void setLastVip(int lastVip) {
        this.lastVip = lastVip;
        DomainUtils.firePropertyChange(this, "lastVip", this.lastVip);
    }

    public int getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(int drawTime) {
        this.drawTime = drawTime;
        DomainUtils.firePropertyChange(this, "drawTime", this.drawTime);
    }
}
