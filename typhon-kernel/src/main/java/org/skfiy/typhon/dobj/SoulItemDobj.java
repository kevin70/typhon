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
package org.skfiy.typhon.dobj;

import org.skfiy.typhon.domain.item.AbstractItem;
import org.skfiy.typhon.domain.item.SoulItem;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class SoulItemDobj extends SimpleItemDobj {

    private int depletion;
    private int count;
    private HeroItemDobj heroItemDobj;

    public int getDepletion() {
        return depletion;
    }

    public void setDepletion(int depletion) {
        this.depletion = depletion;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HeroItemDobj getHeroItemDobj() {
        return heroItemDobj;
    }

    public void setHeroItemDobj(HeroItemDobj heroItemDobj) {
        this.heroItemDobj = heroItemDobj;
    }

    @Override
    public AbstractItem toDomainItem() {
        SoulItem item = new SoulItem();
        item.setItemDobj(this);
        return item;
    }

}
