/*
 * Copyright 2014 The Skfiy Open Association.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skfiy.typhon.domain;

import java.util.ArrayList;
import java.util.List;
import org.skfiy.typhon.util.DomainUtils;
import org.skfiy.util.Assert;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PveProgress extends AbstractIndexable implements Lootable {

    public static final int ONE_START = 1;
    public static final int TWO_STARTS = 2;
    public static final int THREE_STARTS = 3;

    private int cidx;
    private int pidx;
    // 成就
    private int fru1;
    private int fru2;
    private int fru3;
    // 领取奖励
    private int hrecd1;
    private int hrecd2;
    private int hrecd3;
    //每天PVE的次数
    private int count;
    private int resetCount;

    //
    private int total;
    private List<Record> records;

    public PveProgress() {
    }

    public PveProgress(int cidx, int pidx) {
        this.cidx = cidx;
        this.pidx = pidx;
    }

    public int getCidx() {
        return cidx;
    }

    public void setCidx(int cidx) {
        this.cidx = cidx;
    }

    public int getPidx() {
        return pidx;
    }

    public void setPidx(int pidx) {
        this.pidx = pidx;
    }

    public int getFru1() {
        return fru1;
    }

    public void setFru1(int fru1) {
        if (this.fru1 != fru1) {
            this.fru1 = fru1;
            DomainUtils.firePropertyChange(this, "fru1", this.fru1);
        }
    }

    public int getFru2() {
        return fru2;
    }

    public void setFru2(int fru2) {
        if (this.fru2 != fru2) {
            this.fru2 = fru2;
            DomainUtils.firePropertyChange(this, "fru2", this.fru2);
        }
    }

    public int getFru3() {
        return fru3;
    }

    public void setFru3(int fru3) {
        if (this.fru3 != fru3) {
            this.fru3 = fru3;
            DomainUtils.firePropertyChange(this, "fru3", this.fru3);
        }
    }

    public int getHrecd1() {
        return hrecd1;
    }

    public void setHrecd1(int hrecd1) {
        this.hrecd1 = hrecd1;
        DomainUtils.firePropertyChange(this, "hrecd1", this.hrecd1);
    }

    public int getHrecd2() {
        return hrecd2;
    }

    public void setHrecd2(int hrecd2) {
        this.hrecd2 = hrecd2;
        DomainUtils.firePropertyChange(this, "hrecd2", this.hrecd2);
    }

    public int getHrecd3() {
        return hrecd3;
    }

    public void setHrecd3(int hrecd3) {
        this.hrecd3 = hrecd3;
        DomainUtils.firePropertyChange(this, "hrecd3", this.hrecd3);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        DomainUtils.firePropertyChange(this, "count", this.count);
    }

    public int getResetCount() {
        return resetCount;
    }

    public void setResetCount(int resetCount) {
        this.resetCount = resetCount;
        DomainUtils.firePropertyChange(this, "resetCount", this.resetCount);
    }

    @Override
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    @Override
    public boolean addRecord(Record record) {
        if (records == null) {
            records = new ArrayList<>();
        }
        return records.add(record);
    }

    @Override
    public Record findRecord(String lid) {
        Assert.hasLength(lid);

        Record rs = null;
        if (records != null && records.size() > 0) {
            for (Record r : records) {
                if (lid.equals(r.getLid())) {
                    rs = r;
                    break;
                }
            }
        }
        return rs;
    }

}
