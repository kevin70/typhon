package org.skfiy.typhon.spi.store;

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
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SocietyStoreProvider extends StoreProvider {
    
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
        JSONArray array = JSON.parseArray(ComponentUtils.readDataFile("society_store.json"));
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

        array = JSON.parseArray(ComponentUtils.readDataFile("society_store_refresh_time.json"));
        List<Integer> tempRefreshTimes = new ArrayList<>();
        Collections.addAll(tempRefreshTimes, array.toArray(new Integer[] {}));
        refreshTimes = Collections.unmodifiableList(tempRefreshTimes);

        array = JSON.parseArray(ComponentUtils.readDataFile("society_store_refresh_cost.json"));
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
        if (level < 32) {
            return;
        }
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

        player.getNormal().setSocietyCommodities(myCommodities);
    }

    @Override
    public void refreshCommodity(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int count = player.getNormal().getSocietyRefreshCounts();
        int i = count;

        if (count >= refreshExpenses.size()) {
            i = refreshExpenses.size() - 1;
        }

        int cost = refreshExpenses.get(i);
        SessionUtils.decrementSocietyMoney(cost);
        player.getNormal().setSocietyRefreshCounts(count + 1);

        refreshCommodity(player);
        player.getSession().write(Packet.createResult(packet));
    }

    @Override
    public void buyCommodities(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        String cid = (String) packet.getVal();
        Commodity commodity = COMMODITY_MAP.get(cid);

        if (buyCommodities0(player.getNormal().getSocietyCommodities(), commodity)) {
            player.getSession().write(Packet.createResult(packet));
        } else {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Without the id" + cid + " on the table  OR the id count is more<SocietyStore> ");
            player.getSession().write(error);
        }
    }

    public int index() {
        return Typhons.getInteger("typhon.spi.society.store");
    }
}
