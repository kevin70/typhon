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
import java.util.ArrayList;
import org.skfiy.typhon.domain.Invisible;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.util.StringUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class InvisibleRoleDatable implements RoleDatable {

    @Override
    public void initialize(Player player) {
        // init
        Invisible invi = new Invisible();
        player.setInvisible(invi);
    }

    @Override
    public void serialize(Player player, RoleData roleData) {
        roleData.setInvisibleData(JSON.toJSONString(player.getInvisible()));
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {
        if (!StringUtils.isEmpty(roleData.getInvisibleData())) {
            player.setInvisible(JSON.parseObject(roleData.getInvisibleData(), Invisible.class));
        } else {
            initialize(player);
        }
    }

}
