package org.skfiy.typhon.spi.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.domain.CostType;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class StoreProvider extends AbstractComponent {

    protected static final Random RANDOM = new Random();
    private static final Map<String, Commodity> COMMODITY_MAP = new HashMap<>();
    private static final List<Commodity> COMMODITY_A = new ArrayList<>();
    private static final List<Commodity> COMMODITY_B = new ArrayList<>();
    private static final List<Commodity> COMMODITY_C = new ArrayList<>();

    private JSONArray array;
    private List<Integer> refreshTimes;

    @Inject
    protected ItemProvider itemProvider;

    @Override
    protected void doInit() {
        loadDataStore();
    }

    @Override
    protected void doReload() {

    }

    @Override
    protected void doDestroy() {

    }

    public List<Integer> getRefreshTimes() {
        return refreshTimes;
    }

    /**
     * 
     * @param player
     */
    public void refreshCommodity(Player player) {
        int level = player.getRole().getLevel();
        List<MyCommodity> myCommodities = new ArrayList<>();

        randomCommodity(COMMODITY_A, myCommodities, level);
        randomCommodity(COMMODITY_B, myCommodities, level);

        for (int i = 0; i < 4; i++) {
            randomCommodity(COMMODITY_C, myCommodities, level);
        }

        player.getNormal().setCommodities(myCommodities);
    }

    public void refreshCommodity(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int count = player.getNormal().getRefreshCount();

        if (count >= array.size()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Has the most number <refreshStore>");
            player.getSession().write(error);
            return;
        }

        int cost = array.getJSONObject(count).getIntValue("cost");
        JSONObject object = new JSONObject();
        object.put("place", "Store");
        object.put("refreshCounts", count);
        SessionUtils.decrementDiamond(cost, object.toString());
        player.getNormal().setRefreshCount(count + 1);
        refreshCommodity(player);
        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 
     * @param packet
     */
    public void buyCommodities(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        String cid = (String) packet.getVal();
        Commodity commodity = COMMODITY_MAP.get(cid);

        if (buyCommodities0(player.getNormal().getCommodities(), commodity)) {
            player.getSession().write(Packet.createResult(packet));
        } else {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Without the id" + cid + " on the table  OR the id count is more<Store> ");
            player.getSession().write(error);
        }
    }

    /**
     * 
     * @param commodities
     * @param commodity
     * @return
     */
    protected boolean buyCommodities0(List<MyCommodity> commodities, Commodity commodity) {
        int cost = (int) Math.ceil(commodity.getCost() * commodity.getDiscount());
        for (MyCommodity myComm : commodities) {
            if (myComm.getId().equals(commodity.getId())
                    && myComm.getCount() < commodity.getCount()) {
                if (commodity.getCostType() == CostType.C) {
                    SessionUtils.decrementCopper(cost);
                } else if (commodity.getCostType() == CostType.D) {
                    JSONObject object = new JSONObject();
                    object.put("place", "Store");
                    object.put("buyitems", commodity.getId());
                    SessionUtils.decrementDiamond(cost, object.toString());
                } else if (commodity.getCostType() == CostType.S) {
                    SessionUtils.decrementSocietyMoney(cost);
                } else if (commodity.getCostType() == CostType.E) {
                    SessionUtils.decrementDargonMoney(cost);
                } else {
                    SessionUtils.decrementExploit(cost);
                }

                BagUtils.intoItem(commodity.getItem(), commodity.getCount());
                myComm.setCount(commodity.getCount() + commodity.getCount());
                return true;
            }
        }
        return false;
    }

    protected void randomCommodity(List<Commodity> allCommodities, List<MyCommodity> myCommodities,
            int level) {
        if (allCommodities.size() == 0) {
            return;
        }
        int a = RANDOM.nextInt(allCommodities.size());
        Commodity comm;
        boolean bool = false;
        for (;;) {
            if (a >= allCommodities.size()) {
                a = 0;
            }

            comm = allCommodities.get(a++);

            for (MyCommodity myComm : myCommodities) {
                bool = myComm.getId().equals(comm.getId());
                if (bool) {
                    break;
                }
            }

            if (!bool && (comm.getLevel() - level) <= 5) {
                myCommodities.add(new MyCommodity(comm.getId()));
                break;
            }
        }
    }

    /**
     * 
     * @param json
     * @return
     */
    protected Commodity toCommodity(JSONObject json) {
        Commodity com = JSON.toJavaObject(json, Commodity.class);
        com.setItem(itemProvider.getItem(json.getString("#item.id")));
        return com;
    }

    private void loadDataStore() {
        JSONArray store = JSON.parseArray(ComponentUtils.readDataFile("store_basic.json"));
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

        JSONArray arr = JSONArray.parseArray(ComponentUtils.readDataFile("refresh_time.json"));
        List<Integer> tempRefreshTimes = new ArrayList<>();
        Collections.addAll(tempRefreshTimes, arr.toArray(new Integer[] {}));
        refreshTimes = Collections.unmodifiableList(tempRefreshTimes);

        array = JSON.parseArray(ComponentUtils.readDataFile("store_refresh.json"));
    }
}
