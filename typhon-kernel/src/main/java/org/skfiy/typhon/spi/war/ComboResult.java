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
package org.skfiy.typhon.spi.war;

import java.util.ArrayList;
import java.util.List;
import org.skfiy.typhon.domain.item.IFightItem;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class ComboResult {

    private IFightItem.Shot shot;
    private int count;
    private List<Object> targets = new ArrayList<>();

    public IFightItem.Shot getShot() {
        return shot;
    }

    public void setShot(IFightItem.Shot shot) {
        this.shot = shot;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Object> getTargets() {
        return targets;
    }

    public boolean addTarget(AttackEntry ae) {
        return this.targets.add(ae);
    }

    public void setTargets(List<Object> targets) {
        this.targets = targets;
    }

}
