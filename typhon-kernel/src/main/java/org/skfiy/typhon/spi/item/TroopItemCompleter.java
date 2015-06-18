package org.skfiy.typhon.spi.item;

import org.skfiy.typhon.Globals;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.dobj.TroopItemDobj;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;

public class TroopItemCompleter extends EquipmentItemCompleter {
    @Override
    public String getType() {
        return "Troop";
    }

    @Override
    public ItemDobj prepare(JSONObject json) {
        return TypeUtils.cast(json, TroopItemDobj.class, Globals.NO_ENABLED_ASM_PARSE_CONFIG);
    }
}
