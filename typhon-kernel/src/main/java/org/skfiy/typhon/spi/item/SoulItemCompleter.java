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

package org.skfiy.typhon.spi.item;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import java.util.Map;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.dobj.HeroItemDobj;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.dobj.SoulItemDobj;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class SoulItemCompleter implements ItemCompleter {

    @Override
    public String getType() {
        return "Soul";
    }

    @Override
    public ItemDobj prepare(JSONObject json) {
        return TypeUtils.cast(json, SoulItemDobj.class, Globals.NO_ENABLED_ASM_PARSE_CONFIG);
    }

    @Override
    public void complete(Map<String, ItemDobj> items, JSONObject json) {
        SoulItemDobj soulItemDobj = (SoulItemDobj) items.get(json.getString("id"));
        HeroItemDobj heroItemDobj = (HeroItemDobj) items.get(json.getString("#hero.id"));
        soulItemDobj.setHeroItemDobj(heroItemDobj);
    }
    
}
