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
package org.skfiy.typhon.spi.role;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.domain.item.AbstractItem;
import org.skfiy.typhon.spi.ItemProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class BagRoleDatable implements RoleDatable {

    @Inject
    private ItemProvider itemProvider;

    @Override
    public void initialize(Player player) {
        player.setBag(new Bag());
    }

    @Override
    public void serialize(Player player, RoleData roleData) {
        if (player.getBag() == null) {
            return;
        }

        roleData.setBagData(JSON.toJSONString(player.getBag(),
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect));
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {
        String data = roleData.getBagData();
        player.setBag(parseData(data));
    }

    protected Bag parseData(String data) {
        Bag big;
        if (StringUtils.isEmpty(data)) {
            big = new Bag();
        } else {
            big = JSON.parseObject(data, Bag.class, Feature.DisableASM);

            ItemDobj itemDobj;
            AbstractItem aitem;
            for (Node node : big.getNodes()) {
                aitem = node.getItem();
                itemDobj = itemProvider.getItem(aitem.getId());
                aitem.setItemDobj(itemDobj);
            }
        }
        return big;
    }

}
