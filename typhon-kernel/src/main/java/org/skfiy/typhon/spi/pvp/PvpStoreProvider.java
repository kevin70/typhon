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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.store.Commodity;
import org.skfiy.typhon.spi.store.MyCommodity;
import org.skfiy.typhon.spi.store.StoreProvider;
import org.skfiy.typhon.util.ComponentUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PvpStoreProvider extends StoreProvider {

    private static final Map<String, Commodity> COMMODITY_MAP = new HashMap<>();
    private static final List<Commodity> COMMODITIESA = new ArrayList<>();
    private static final List<Commodity> COMMODITIESB = new ArrayList<>();
    private static final List<Commodity> COMMODITIESC = new ArrayList<>();
    private static final List<Commodity> COMMODITIESD = new ArrayList<>();
    private static final List<Commodity> COMMODITIESE = new ArrayList<>();
    private static final List<Commodity> COMMODITIESF = new ArrayList<>();
    private static final List<Commodity> COMMODITIES = new ArrayList<>();

    private List<Integer> refreshExpenses;
    private List<Integer> refreshTimes;

    @Override
    protected void doInit() {
        JSONArray array = JSON.parseArray(ComponentUtils.readDataFile("store_pvp.json"));
        for (int i = 0; i < array.size(); i++) {
            JSONObject json = array.getJSONObject(i);
            Commodity commodity = toCommodity(json);
            commodity.setItem(itemProvider.getItem(json.getString("#item.id")));
            if (commodity.getPos() == 1) {
                COMMODITIESA.add(commodity);
            } else if (commodity.getPos() == 2) {
                COMMODITIESB.add(commodity);
            } else if (commodity.getPos() == 3) {
                COMMODITIESC.add(commodity);
            } else if (commodity.getPos() == 4) {
                COMMODITIESD.add(commodity);
            } else if (commodity.getPos() == 5) {
                COMMODITIESE.add(commodity);
            } else if (commodity.getPos() == 6) {
                COMMODITIESF.add(commodity);
            } else {
                COMMODITIES.add(commodity);
            }
            COMMODITY_MAP.put(commodity.getId(), commodity);
        }
        // 打乱顺序
        Collections.shuffle(COMMODITIESA);
        Collections.shuffle(COMMODITIESB);
        Collections.shuffle(COMMODITIESC);
        Collections.shuffle(COMMODITIESD);
        Collections.shuffle(COMMODITIESE);
        Collections.shuffle(COMMODITIESF);
        Collections.shuffle(COMMODITIES);

        array = JSON.parseArray(ComponentUtils.readDataFile("store_pvp_refresh_time.json"));
        List<Integer> tempRefreshTimes = new ArrayList<>();
        Collections.addAll(tempRefreshTimes, array.toArray(new Integer[] {}));
        refreshTimes = Collections.unmodifiableList(tempRefreshTimes);

        array = JSON.parseArray(ComponentUtils.readDataFile("store_pvp_refresh.json"));
        refreshExpenses = Arrays.asList(array.toArray(new Integer[] {}));
    }

    @Override
    protected void doDestroy() {}

    @Override
    public List<Integer> getRefreshTimes() {
        return refreshTimes;
    }

    @Override
    public void refreshCommodity(Player player) {
        int level = player.getRole().getLevel();
        List<MyCommodity> myCommodities = new ArrayList<>();
        randomCommodity(COMMODITIESA, myCommodities, level);
        randomCommodity(COMMODITIESB, myCommodities, level);
        randomCommodity(COMMODITIESC, myCommodities, level);
        randomCommodity(COMMODITIESD, myCommodities, level);
        randomCommodity(COMMODITIESE, myCommodities, level);
        randomCommodity(COMMODITIESF, myCommodities, level);

        int index = index() - myCommodities.size();
        for (int i = 0; i < index; i++) {
            randomCommodity(COMMODITIES, myCommodities, level);
        }

        player.getNormal().setPvpCommodities(myCommodities);
    }

    @Override
    public void refreshCommodity(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int count = player.getNormal().getPvpStoreRecount();
        int i = count;

        if (count >= refreshExpenses.size()) {
            i = refreshExpenses.size() - 1;
        }

        int cost = refreshExpenses.get(i);
        SessionUtils.decrementExploit(cost);
        player.getNormal().setPvpStoreRecount(count + 1);

        refreshCommodity(player);
        player.getSession().write(Packet.createResult(packet));
    }

    @Override
    public void buyCommodities(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        String cid = (String) packet.getVal();
        Commodity commodity = COMMODITY_MAP.get(cid);

        if (buyCommodities0(player.getNormal().getPvpCommodities(), commodity)) {
            player.getSession().write(Packet.createResult(packet));
        } else {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Without the id" + cid + " on the table  OR the id count is more<Store> ");
            player.getSession().write(error);
        }
    }

    public int index() {
        return Typhons.getInteger("typhon.spi.pvp.store");
    }

}
