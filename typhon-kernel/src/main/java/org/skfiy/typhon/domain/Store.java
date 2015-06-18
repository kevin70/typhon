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
import org.skfiy.typhon.spi.store.MyCommodity;
import org.skfiy.typhon.util.DomainUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Store extends AbstractChangeable {

    private List<MyCommodity> commodities = new ArrayList<>();
    private long lastRefreshTime;
    private int count;

    public List<MyCommodity> getCommodities() {
        return commodities;
    }

    public void setCommodities(List<MyCommodity> commodities) {
        this.commodities = commodities;
        for (int i = 0; i < commodities.size(); i++) {
            this.commodities.get(i).set(this, "commodities", i);
        }
        DomainUtils.firePropertyChange(this, "commodities", this.commodities);
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
        DomainUtils.firePropertyChange(this, "lastRefreshTime", this.lastRefreshTime);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        DomainUtils.firePropertyChange(this, "count", this.count);
    }

}
