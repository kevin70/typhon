package org.skfiy.typhon.spi.caravan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.packet.CaravanPacket;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.Vip;
import org.skfiy.typhon.spi.store.Commoditied;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class CaravanProvider extends AbstractComponent {

    protected static final Random RANDOM = new Random();
    private final Map<String, CaravanType> caravanTypes = new HashMap<>();
    private final List<CaravanRace> caravanRaces = new ArrayList<>();
    // 商队CD时间
    private int caravanCDTime;
    // 钻石通道
    private int caravanDiamond;
    // 金币通道
    private int caravanCopper;
    // 金币倍率
    private double copperFactor;
    // 钻石倍率
    private double diamondFactor;
    // 职业倍率
    private double raceFactor;
    // 召回物品倍率
    private double backFactor;
    // 召回N分钟算一钻
    private int backTimeDiamond;
    // 刷新商队信息钻石消费
    private int refreshCost;

    @Inject
    private ItemProvider itemProvider;
    @Inject
    private RoleProvider roleProvider;

    @Override
    protected void doInit() {
        JSONArray array;
        array = JSON.parseArray(ComponentUtils.readDataFile("caravan_sorts.json"));
        CaravanType caravanType = null;
        for (int i = 0; i < array.size(); i++) {
            caravanType = array.getObject(i, CaravanType.class);
            caravanTypes.put(caravanType.getType(), caravanType);
        }

        caravanRaces.addAll(JSON.parseArray(ComponentUtils.readDataFile("caravan_race.json"),
                CaravanRace.class));
        caravanCDTime = Typhons.getInteger("typhon.spi.caravan.timeCD");
        caravanCopper = Typhons.getInteger("typhon.spi.caravan.copper");
        caravanDiamond = Typhons.getInteger("typhon.spi.caravan.diamond");
        copperFactor = Typhons.getDouble("typhon.spi.caravan.copper.factor");
        copperFactor = Typhons.getDouble("typhon.spi.caravan.copper.factor");
        diamondFactor = Typhons.getDouble("typhon.spi.caravan.diamond.factor");
        raceFactor = Typhons.getDouble("typhon.spi.caravan.race.factor");
        backFactor = Typhons.getDouble("typhon.spi.caravan.back.factor");
        backTimeDiamond = Typhons.getInteger("typhon.spi.caravan.back.timeDiamond");
        refreshCost = Typhons.getInteger("typhon.spi.caravan.refreshCost");
    }

    @Override
    protected void doDestroy() {}

    @Override
    protected void doReload() {}

    public void refresh(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        resetCaravan(normal);
        JSONObject object = new JSONObject();
        object.put("place", "CaravanRefresh");
        SessionUtils.decrementDiamond(refreshCost, object.toString());
        player.getSession().write(Packet.createResult(packet));
    }

    public void wayGoing(CaravanPacket packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        if (packet.getHeroes().size() < 3) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Heroes is not enough ");
            player.getSession().write(error);
            return;
        }

        double costFactor = 0;
        int count = 0;
        Vip vip = roleProvider.getVip(normal.getVipLevel());

        if (normal.getCaravans().size() >= vip.privileged.explore_count) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.size_limit);
            error.setText("Don't hava CaravanCounts");
            player.getSession().write(error);
            return;
        }

        if (packet.getCostType() == null) {
            costFactor = copperFactor;
            SessionUtils.decrementCopper(caravanCopper);
        } else {
            costFactor = diamondFactor;
            JSONObject object = new JSONObject();
            object.put("place", "CaravanDiamond");
            SessionUtils.decrementDiamond(caravanDiamond, object.toString());
        }

        Bag bag = player.getHeroBag();
        for (String str : packet.getHeroes()) {
            HeroItem heroItem = bag.findNode(str).getItem();
            for (CaravanInformation caravans : normal.getCaravans()) {
                if (caravans.getTroops().contains(str)) {
                    PacketError error =
                            PacketError.createResult(packet, PacketError.Condition.conflict);
                    error.setText("Heroes is not enough ");
                    player.getSession().write(error);
                    return;
                }
            }
            if (heroItem.getRace().toString().equals(normal.getCaravan().getRace())) {
                count++;
            }
        }

        int level = 0;
        List<CaravanReward> caravanRewards =
                caravanTypes.get(normal.getCaravan().getMonger()).getGrade();
        CaravanReward obj = null;
        for (int i = 0; i < caravanRewards.size(); i++) {
            obj = caravanRewards.get(i);
            if (normal.getLevel() < obj.getLevel() && i > 0) {
                level = caravanRewards.get(i - 1).getLevel();
                break;
            }
            if (i == caravanRewards.size() - 1) {
                level = caravanRewards.get(i).getLevel();
                break;
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, caravanCDTime);
        normal.addCaravans(new CaravanInformation(normal.getCaravan().getMonger(), calendar
                .getTimeInMillis(), level, packet.getHeroes(), costFactor, (1 + count * raceFactor)));
        resetCaravan(normal);

        int counts = normal.getDailyTask().getTaskCaravan();
        if (counts >= 0) {
            normal.getDailyTask().setTaskCaravan(counts + 1);
        }

        player.getSession().write(Packet.createResult(packet));
    }

    public void recallCaravan(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        if (normal.getCaravan() == null) {
            resetCaravan(normal);
        }

        if (packet.getVal() == null) {
            player.getSession().write(Packet.createResult(packet));
            return;
        }

        List<Integer> indexs = (List<Integer>) packet.getVal();
        List<Commoditied> commoditieds = new ArrayList<>();
        Commoditied commod = null;
        boolean bool = false;
        double number = 0;
        int cost = 0;
        for (int index : indexs) {
            CaravanInformation caravan = normal.getCaravans().get(index);
            // 判断时间
            bool = caravan.getTime() <= System.currentTimeMillis();
            if (!bool) {
                // time/1000按分钟算*100取小数点后两位/cdTime/
                double a = (caravan.getTime() - System.currentTimeMillis()) / 1000;
                cost = (int) Math.ceil(a / 60 / backTimeDiamond);
                number = 1 - (a / 60 / (caravanCDTime * 60));
            }
            JSONObject object = new JSONObject();
            object.put("place", "CaravanRecall");
            SessionUtils.decrementDiamond(cost, object.toString());

            for (CaravanReward caravanReward : caravanTypes.get(caravan.getId()).getGrade()) {
                // 判断等级
                if (caravan.getLevel() == caravanReward.getLevel()) {
                    // 获取奖品
                    for (Commoditied obj : caravanReward.getReward()) {
                        commod = new Commoditied();
                        int resultCount = (int) Math.ceil(obj.getCount() * caravan.getRaceFactor());
                        if (bool) {
                            commod.setCount((int) (resultCount * caravan.getCostFactor()));
                        } else {
                            commod.setCount((int) Math.ceil((resultCount * number * backFactor)
                                    * caravan.getCostFactor()));
                        }
                        commod.setId(obj.getId());
                        BagUtils.intoItem(itemProvider.getItem(obj.getId()), commod.getCount());
                        commoditieds.add(commod);
                    }
                }
            }
        }

        for (int i = indexs.size() - 1; i >= 0; i--) {
            normal.deleteCaravans(indexs.get(i));
        }

        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(commoditieds);
        player.getSession().write(result);
    }

    private void resetCaravan(Normal normal) {
        int random = RANDOM.nextInt(caravanTypes.size());
        int index = 0;
        String type = null;
        for (Entry<String, CaravanType> emery : caravanTypes.entrySet()) {
            if (index == random) {
                type = emery.getKey();
                break;
            }
            index++;
        }
        random = RANDOM.nextInt(caravanRaces.size());
        String race = caravanRaces.get(random).getRace();

        int otherRandom = RANDOM.nextInt(caravanRaces.get(random).getValue().length);
        String txtId = caravanRaces.get(random).getValue()[otherRandom];
        normal.setCaravan(new Caravan(type, race, txtId));
    }
}
