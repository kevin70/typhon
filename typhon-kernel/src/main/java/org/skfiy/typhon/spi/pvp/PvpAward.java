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
package org.skfiy.typhon.spi.pvp;

import org.skfiy.typhon.domain.item.Item;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
class PvpAward implements Comparable<PvpAward> {

    final int beginRanking;
    final Item item;
    final int count;

    PvpAward(int beginRanking, Item item, int count) {
        this.beginRanking = beginRanking;
        this.item = item;
        this.count = count;
    }

    @Override
    public int compareTo(PvpAward o) {
        return Integer.compare(o.beginRanking, beginRanking);
    }

}
