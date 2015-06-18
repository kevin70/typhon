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

import javax.inject.Inject;
import org.apache.commons.lang3.ArrayUtils;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.hero.HeroProvider;
import org.skfiy.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class HeroBagDatable extends BagRoleDatable {

    @Inject
    private ItemProvider itemProvider;
    @Inject
    private HeroProvider heroProvider;
    
    @Override
    public void initialize(Player player) {
        Bag heroBag = new Bag();
        
        // 在初始背包中添加玩家初始的武将
        String[] heroIds = Typhons.getProperty("typhon.spi.player.initHeros").split(",");
        int[] poses = new int[heroIds.length];
        HeroItemDobj itemDobj;
        for (int i = 0; i < heroIds.length; i++) {
            String id = heroIds[i];
            itemDobj = itemProvider.getItem(id);
            poses[i] = heroBag.intoItem(itemDobj);
        }
        
        // 设置援军位武将
        // String succorId = Typhons.getProperty("typhon.spi.fightGroup.succorId");
        // int pos = heroBag.intoItem(itemProvider.getItem(succorId));
        
        // 初始化玩家的攻击组
        FightGroup[] fightGroups = new FightGroup[Typhons.getInteger("typhon.spi.fightGroupCount")];
        for (int i = 0; i < fightGroups.length; i++) {
            FightGroup fightGroup = new FightGroup();
            int[] clonePoses = ArrayUtils.clone(poses);
            fightGroup.setHeroPoses(clonePoses);
            fightGroups[i] = fightGroup;

            // fightGroup.setSuccor(pos);
            // fightGroup.setSuccorIid(succorId);
        }

        player.getNormal().setFightGroups(fightGroups);
        player.setHeroBag(heroBag);
    }

    @Override
    public void serialize(Player player, RoleData roleData) {
        String data = JSON.toJSONString(player.getHeroBag(),
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect);
        roleData.setHeroBagData(data);
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {
        String data = roleData.getHeroBagData();
        if (StringUtils.isEmpty(data)) {
            initialize(player);
            return;
        }
        
        Bag heroBag = parseData(data);
        for (Bag.Node node : heroBag.getNodes()) {
            node.getItem().setItemDobj(itemProvider.getItem(node.getItem().getId()));
        }
        
        player.setHeroBag(heroBag);
    }
    
}
