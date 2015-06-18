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
package org.skfiy.typhon.domain.item;

import com.alibaba.fastjson.annotation.JSONType;
import org.skfiy.typhon.dobj.EquipmentItemDobj;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"levelLimit", "tong", "wu", "zhi",
    "atk", "def", "hp", "critRate",
    "critMagn", "decritRate", "parryRate",
    "deparryRate", "subitems"})
public class EquipmentItem extends AbstractItem<EquipmentItemDobj> {

    public int getLevelLimit() {
        return getItemDobj().getLevelLimit();
    }
    
    public int getTong() {
        return getItemDobj().getTong();
    }

    public int getWu() {
        return getItemDobj().getWu();
    }

    public int getZhi() {
        return getItemDobj().getZhi();
    }

    public int getAtk() {
        return getItemDobj().getAtk();
    }

    public int getDef() {
        return getItemDobj().getDef();
    }

    public int getHp() {
        return getItemDobj().getHp();
    }

    public int getCritRate() {
        return getItemDobj().getCritRate();
    }

    public int getCritMagn() {
        return getItemDobj().getCritMagn();
    }

    public int getDecritRate() {
        return getItemDobj().getDecritRate();
    }

    public int getParryRate() {
        return getItemDobj().getParryRate();
    }

    public int getParryValue() {
        return getItemDobj().getParryValue();
    }

    public int getDeparryRate() {
        return getItemDobj().getDeparryRate();
    }

    public Subitem[] getSubitems() {
        return getItemDobj().getSubitems();
    }

}
