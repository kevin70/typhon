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
package org.skfiy.typhon.spi.store;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Store;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.util.ComponentUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class WesternStoreProvider extends StoreProvider {

    private static final Map<String, Commodity> COMMODITY_MAP = new HashMap<>();
    private static final List<Commodity> COMMODITY_A = new ArrayList<>();
    private static final List<Commodity> COMMODITY_B = new ArrayList<>();
    private static final List<Commodity> COMMODITY_C = new ArrayList<>();
    private final JSONArray REFRESH_DIAMONDS = new JSONArray();

    @Override
    protected void doInit() {
        JSONArray store = JSON.parseArray(ComponentUtils.readDataFile("store_western.json"));
        for (int i = 0; i < store.size(); i++) {
            JSONObject obj = store.getJSONObject(i);
            Commodity commodity = toCommodity(obj);

            if (commodity.getPos() == 1) {
                COMMODITY_A.add(commodity);
            } else if (commodity.getPos() == 2) {
                COMMODITY_B.add(commodity);
            } else {
                COMMODITY_C.add(commodity);
            }
            COMMODITY_MAP.put(commodity.getId(), commodity);
        }
        // 打乱顺序
        Collections.shuffle(COMMODITY_A);
        Collections.shuffle(COMMODITY_B);
        Collections.shuffle(COMMODITY_C);
        
        REFRESH_DIAMONDS.addAll(JSON.parseArray(ComponentUtils.readDataFile("store_refresh.json")));
    }

    @Override
    public void refreshCommodity(Player player) {
        int level = player.getRole().getLevel();
        List<MyCommodity> myCommodities = new ArrayList<>();

        randomCommodity(COMMODITY_A, myCommodities, level);
        randomCommodity(COMMODITY_B, myCommodities, level);

        for (int i = 0; i < 10; i++) {
            randomCommodity(COMMODITY_C, myCommodities, level);
        }

        Store westernStore = player.getNormal().getWesternStore();
        if (westernStore != null) {
            westernStore.setCommodities(myCommodities);
        } else {
            westernStore = new Store();
            westernStore.setCommodities(myCommodities);
            westernStore.setLastRefreshTime(System.currentTimeMillis());

            player.getNormal().setWesternStore(westernStore);
        }
    }

    /**
     *
     * @param player
     */
    public void autoRefreshCommodity(Player player) {
        if (player.getRole().getLevel() < 50) {
            return;
        }
        
        Store westernStore = player.getNormal().getWesternStore();

        // 今天已经刷新过了
        if (westernStore != null) {
            return;
        }
        refreshCommodity(player);
    }
    
    @Override
    public void refreshCommodity(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int count = player.getNormal().getWesternStore().getCount();

        if (count >= REFRESH_DIAMONDS.size()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Has the most number <refreshStore>");
            player.getSession().write(error);
            return;
        }

        int cost = REFRESH_DIAMONDS.getJSONObject(count).getIntValue("cost");
        JSONObject object = new JSONObject();
        object.put("place", "RefreshWesternStore");
        object.put("refreshCounts", count);
        SessionUtils.decrementDiamond(cost,object.toString());
        
        refreshCommodity(player);
        player.getNormal().getWesternStore().setCount(count + 1);
        
        player.getSession().write(Packet.createResult(packet));
    }
    
    @Override
    public void buyCommodities(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        String cid = (String) packet.getVal();
        Commodity commodity = COMMODITY_MAP.get(cid);

        if (buyCommodities0(player.getNormal().getWesternStore().getCommodities(), commodity)) {
            player.getSession().write(Packet.createResult(packet));
        } else {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not found commodity[" + cid + "]");
            player.getSession().write(error);
        }
    }

}
