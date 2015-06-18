package org.skfiy.typhon.spi.dargon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.store.Commodity;
import org.skfiy.typhon.spi.store.MyCommodity;
import org.skfiy.typhon.spi.store.StoreProvider;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DargonStoreProvider extends StoreProvider {

    private static final Map<String, Commodity> HYCOMMODITY_MAP = new HashMap<>();
    private static final List<Commodity> HYCOMMODITIESA = new ArrayList<>();
    private static final List<Commodity> HYCOMMODITIESB = new ArrayList<>();
    private static final List<Commodity> HYCOMMODITIESC = new ArrayList<>();
    private static final List<Commodity> HYCOMMODITIESD = new ArrayList<>();
    private static final List<Commodity> HYCOMMODITIESE = new ArrayList<>();
    private static final List<Commodity> HYCOMMODITIESF = new ArrayList<>();
    private static final List<Commodity> HYCOMMODITIES = new ArrayList<>();

    private List<Integer> refreshExpenses;
    private List<Integer> refreshTimes;

    @Inject
    protected ItemProvider itemProvider;

    @Override
    protected void doInit() {
        JSONArray array = JSON.parseArray(ComponentUtils.readDataFile("dargon_store.json"));
        for (int i = 0; i < array.size(); i++) {
            JSONObject json = array.getJSONObject(i);
            Commodity commodity = toCommodity(json);
            commodity.setItem(itemProvider.getItem(json.getString("#item.id")));
            if (commodity.getPos() == 1) {
                HYCOMMODITIESA.add(commodity);
            } else if (commodity.getPos() == 2) {
                HYCOMMODITIESB.add(commodity);
            } else if (commodity.getPos() == 3) {
                HYCOMMODITIESC.add(commodity);
            } else if (commodity.getPos() == 4) {
                HYCOMMODITIESD.add(commodity);
            } else if (commodity.getPos() == 5) {
                HYCOMMODITIESE.add(commodity);
            } else if (commodity.getPos() == 6) {
                HYCOMMODITIESF.add(commodity);
            } else {
                HYCOMMODITIES.add(commodity);
            }
            HYCOMMODITY_MAP.put(commodity.getId(), commodity);
        }
        // 打乱顺序
        Collections.shuffle(HYCOMMODITIESA);
        Collections.shuffle(HYCOMMODITIESB);
        Collections.shuffle(HYCOMMODITIESC);
        Collections.shuffle(HYCOMMODITIESD);
        Collections.shuffle(HYCOMMODITIESE);
        Collections.shuffle(HYCOMMODITIESF);
        Collections.shuffle(HYCOMMODITIES);
        

        array = JSON.parseArray(ComponentUtils.readDataFile("dargon_store_refresh_time.json"));
        List<Integer> tempRefreshTimes = new ArrayList<>();
        Collections.addAll(tempRefreshTimes, array.toArray(new Integer[] {}));
        refreshTimes = Collections.unmodifiableList(tempRefreshTimes);

        array = JSON.parseArray(ComponentUtils.readDataFile("dargon_store_refresh.json"));
        refreshExpenses = Arrays.asList(array.toArray(new Integer[] {}));
    }

    @Override
    protected void doDestroy() {}

    @Override
    protected void doReload() {}

    @Override
    public List<Integer> getRefreshTimes() {
        return refreshTimes;
    }

    @Override
    public void refreshCommodity(Player player) {
        int level = player.getRole().getLevel();
        List<MyCommodity> myCommodities = new ArrayList<>();
        randomCommodity(HYCOMMODITIESA, myCommodities, level);
        randomCommodity(HYCOMMODITIESB, myCommodities, level);
        randomCommodity(HYCOMMODITIESC, myCommodities, level);
        randomCommodity(HYCOMMODITIESD, myCommodities, level);
        randomCommodity(HYCOMMODITIESE, myCommodities, level);
        randomCommodity(HYCOMMODITIESF, myCommodities, level);
        int index = index() - myCommodities.size();
        for (int i = 0; i < index; i++) {
            randomCommodity(HYCOMMODITIES, myCommodities, level);
        }

        player.getNormal().setDargonCommodities(myCommodities);
    }

    @Override
    public void refreshCommodity(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int count = player.getNormal().getRefreshDargonStore();
        int i = count;

        if (count >= refreshExpenses.size()) {
            i = refreshExpenses.size() - 1;
        }

        int cost = refreshExpenses.get(i);
        SessionUtils.decrementDargonMoney(cost);
        player.getNormal().setRefreshDargonStore(count + 1);
        refreshCommodity(player);
        player.getSession().write(Packet.createResult(packet));
    }

    @Override
    public void buyCommodities(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        String cid = (String) packet.getVal();
        Commodity commodity = HYCOMMODITY_MAP.get(cid);

        if (buyCommodities0(player.getNormal().getDargonCommodities(), commodity)) {
            player.getSession().write(Packet.createResult(packet));
        } else {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Without the id" + cid + " on the table  OR the id count is more<Store> ");
            player.getSession().write(error);
        }
    }

    public int index() {
        return Typhons.getInteger("typhon.spi.dargon.store");
    }
}
