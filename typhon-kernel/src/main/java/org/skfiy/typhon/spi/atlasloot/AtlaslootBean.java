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
package org.skfiy.typhon.spi.atlasloot;

import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.domain.Lootable;
import org.skfiy.typhon.session.Session;

/**
 *
 * @author Kevin
 */
public class AtlaslootBean extends RandomAtlaslootBean {

    private ItemDobj item;
    private int count;
    private double prob;

    public ItemDobj getItem() {
        return item;
    }

    public void setItem(ItemDobj item) {
        this.item = item;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    @Override
    void prepare() {
    }

    @Override
    AtlaslootBean calculate(Session session, Lootable lootable) {
        return calculate(prob);
    }

    /**
     *
     * @param p
     * @return
     */
    protected AtlaslootBean calculate(double p) {
        if (RANDOM.nextDouble() < p) {
            return this;
        }
        return null;
    }

}
