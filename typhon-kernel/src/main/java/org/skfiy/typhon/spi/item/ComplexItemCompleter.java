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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import java.util.Iterator;
import java.util.Map;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.dobj.ComplexItemDobj;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.domain.item.Subitem;

/**
 *
 * @author kevin
 */
public class ComplexItemCompleter implements ItemCompleter {

    @Override
    public String getType() {
        return "Complex";
    }

    @Override
    public ItemDobj prepare(JSONObject json) {
        return TypeUtils.cast(json, ComplexItemDobj.class, Globals.NO_ENABLED_ASM_PARSE_CONFIG);
    }

    @Override
    public void complete(Map<String, ItemDobj> items, JSONObject json) {
        String id = json.getString("id");
        ComplexItemDobj complexItem = (ComplexItemDobj) items.get(id);
        JSONArray subitemArray = json.getJSONArray("subitems");

        for (Iterator<Object> it = subitemArray.iterator(); it.hasNext();) {
            JSONObject subitemJson = (JSONObject) it.next();
            ItemDobj subitem = items.get(subitemJson.getString("#item.id"));
            int count = subitemJson.getIntValue("count");
            complexItem.addSubitems(new Subitem(subitem, count));
        }
    }

}
