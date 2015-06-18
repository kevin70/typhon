package org.skfiy.typhon.spi.dargon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Dargon;
import org.skfiy.typhon.domain.DargonObject;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.DargonPacket;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.Vip;
import org.skfiy.typhon.spi.pvp.DargonPvpRival;
import org.skfiy.typhon.spi.pvp.PvpProvider;
import org.skfiy.typhon.spi.task.TaskDargonProvider;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Comparator;

public class DargonProvider extends AbstractComponent {

    private static final Random RANDOM = new Random();
    private static final int[] NUCLEUS_RANKINGS = {4, 3, 2, 1};
    private final DargonComparator dargonComparator = new DargonComparator();
    private final List<DargonEventEnum> dargonEvents = new ArrayList<>();
    private final List<DargonDroplibrary> dargonBox = new ArrayList<>();
    private final List<DargonDroplibrary> dargonSoul = new ArrayList<>();
    private final List<DargonWar> dargonWar = new ArrayList<>();
    private JSONArray dargonQuestion;
    private List<Integer> buyCounts;
    
    private int answerCeiling;
    private int answerFloor;
    private int goldCeiling;
    private int goldFloor;
    private int luckey;
    private int bad;
    private int levelGold;
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private PvpProvider pvpProvider;
    @Inject
    private TaskDargonProvider taskDargonProvider;
    @Inject
    private RoleProvider roleProvider;

    @Override
    protected void doInit() {
        JSONArray array;
        dargonEvents.addAll(JSON.parseArray(ComponentUtils.readDataFile("dargon_events.json"),
                DargonEventEnum.class));
        dargonWar.addAll(JSON.parseArray(ComponentUtils.readDataFile("dargon_war.json"),
                DargonWar.class));

        array = JSON.parseArray(ComponentUtils.readDataFile("dargon_soul.json"));
        dargonSoul.addAll(total(array));
        array = JSON.parseArray(ComponentUtils.readDataFile("dargon_box.json"));
        dargonBox.addAll(total(array));
        dargonQuestion = JSON.parseArray(ComponentUtils.readDataFile("answerDatabase.json"));
        array = JSON.parseArray(ComponentUtils.readDataFile("dargon_buy_counts.json"));
        buyCounts = Arrays.asList(array.toArray(new Integer[]{}));

        answerCeiling = Typhons.getInteger("typhon.spi.dargon.answer.ceiling");
        answerFloor = Typhons.getInteger("typhon.spi.dargon.answer.floor");
        goldCeiling = Typhons.getInteger("typhon.spi.dargon.gold.ceiling");
        goldFloor = Typhons.getInteger("typhon.spi.dargon.gold.floor");
        luckey = -Typhons.getInteger("typhon.spi.dargon.luckey");
        bad = -Typhons.getInteger("typhon.spi.dargon.bad");
        levelGold = Typhons.getInteger("typhon.spi.dargon.gold.floor");
    }

    @Override
    protected void doReload() {

    }

    @Override
    protected void doDestroy() {

    }

    public void dargonStart(DargonPacket packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int count = player.getNormal().getDargonNumber();

        if (count >= Typhons.getInteger("typhon.spi.dargon.numbers")) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("not have Dargoncounts ");
            player.getSession().write(error);
            return;
        }

        Object obj = null;
        int id = packet.getAid();
        Dargon dargon = normal.getDargonEvent().get(id - 1);

        if (!checkDargonLocation(normal, id, dargon)) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("the dargon Location is wrong");
            player.getSession().write(error);
            return;
        }
        int event = dargon.getFlag();
        switch (DargonEventEnum.valueOf(event)) {
            case gold:
                obj = getGold(player);
                break;
            case box:
                obj = randomReward(dargonBox);
                break;
            case lucky:
                obj = luckyEvent(normal);
                break;
            case bad:
                obj = badEvent(normal);
                break;
            case hero_soul:
                obj = randomReward(dargonSoul);
                break;
            case vigor:
                obj = getVigor(player);
                break;
            case gmwz:
                obj = light(player, packet);
                break;
            case question:
                obj = questionAnswer(player, packet);
                break;
            default:
                break;
        }
        player.getNormal().changeDargonNumber(1);
        player.getNormal().setRoleLocation(id);
        dargon.setState(1);
        // 日常任务
        int task = normal.getDailyTask().getTaskDargonCounts();
        if (task >= 0) {
            normal.getDailyTask().setTaskDargonCounts(task + 1);
        }
        //发给客户端判断
        if (DargonEventEnum.valueOf(event).equals(DargonEventEnum.question) && obj == null) {
            obj = -1;
        }
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(obj);
        player.getSession().write(result);
    }

    private boolean checkDargonLocation(Normal normal, int id, Dargon dargon) {
        boolean bool = false;
        int last = normal.getRoleLocation();
        // id 下一步的位置,dargon,上一个格子的状态
        if ((id == 33 || (last == 0 || last - 1 == id || last + 1 == id || last + 4 == id || last - 4 == id))
                && dargon.getState() != 1) {
            bool = true;
        }
        return bool;
    }

    public void reset(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        reset(player);
        player.getSession().write(Packet.createResult(packet));
    }

    private void reset(Player player) {
        player.getInvisible().getDargonAdversary().clear();
        
        Normal normal = player.getNormal();
        normal.clearDargonEvent();
        normal.setBuyDargonCounts(0);
        normal.setOnceDargonMoney(0);
        normal.setRoleLocation(0);
        normal.setDargonNumber(0);
    }

    public void comeBack(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int id = (int) packet.getVal() - 1;
        player.getNormal().changeDargonNumber(1);
        player.getNormal().setRoleLocation(id + 1);
        player.getNormal().getDargonEvent().get(id).setState(1);
        player.getSession().write(Packet.createResult(packet));
    }

    public Object questionAnswer(Player player, DargonPacket packet) {
        JSONObject object = null;
        Object obj = null;
        for (int i = 0; i < dargonQuestion.size(); i++) {
            object = dargonQuestion.getJSONObject(i);
            if (packet.getQid().equals(object.getString("id"))
                    && packet.getAnswer().equals(object.getString("answer"))) {
                int get
                        = (int) (Math.round(Math.random() * (answerCeiling - answerFloor)) + answerFloor);
                int copper = player.getRole().getLevel() * (get + levelGold);
                SessionUtils.incrementCopper(copper);
                obj = copper;
                break;
            }
        }
        return obj;
    }

    public DargonPvpRival randomWar(Player player, Dargon dargon) {
        int random = 0;
        int pvpRanking = player.getNormal().getPvpRanking();
        int min = (int) Math.max(pvpRanking * 49 / 60, 0);
        int max = pvpRanking + 10;
        if (pvpRanking > 100) {
            max = pvpRanking;
        }
        random = RandomRival(min, max);
        return pvpProvider.loadRoleId(player, random, dargonWar.get(0).getFactor());
    }

    public DargonPvpRival fixedWar(Player player, Dargon dargon) {
        int nucleus = dargon.getNucleus();
        DargonWar dargonW = dargonWar.get(nucleus);
        double factor = dargonW.getFactor();
        int pvpRanking = player.getNormal().getPvpRanking();
        int max;
        int min;
        int random = 0;

        if (pvpRanking > 5) {
            switch (nucleus) {
                case 1:
                    min = (int) Math.max(pvpRanking * 49 / 60, 0);
                    max = pvpRanking + 10;
                    if (pvpRanking > 100) {
                        max = pvpRanking;
                    }
                    random = RandomRival(min, max);
                    break;
                case 2:
                    min = (int) Math.max(pvpRanking / 2, 0);
                    max = (int) Math.max(pvpRanking * 49 / 60 - 1, 1);
                    random = RandomRival(min, max);
                    break;
                case 3:
                    min = (int) Math.max(pvpRanking / 3, 0);
                    max = (int) Math.max(pvpRanking / 2 - 1, 1);
                    random = RandomRival(min, max);
                    break;
                case 4:
                    // MAX(ROUNDDOWN(pvpRanking/4-pvpRanking/30,0),1)
                    min = (int) Math.max(pvpRanking * 26 / 120, 1);
                    max = (int) Math.max(pvpRanking / 3 - 1, 1);
                    random = RandomRival(min, max);
                    break;
            }
        } else {
            random = NUCLEUS_RANKINGS[nucleus - 1];
        }

        return pvpProvider.loadRoleId(player, random, factor);
    }

    private int RandomRival(int min, int max) {
        int x = max - min;
        if (x > 0) {
            x = RANDOM.nextInt(x + 1);
        }
        return (x + min);
    }

    public void dargonPrepare(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        
        // 判断Vip相关的攻打次数
        Vip vip = roleProvider.getVip(normal.getVipLevel());
        if (normal.getDargonVipCount() >= vip.privileged.max_dargon_count) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("the dargonNumber is not enough ");
            player.getSession().write(error);
            return;
        }
        
        // 重置
        reset(player);
        
        List<Dargon> dargons = new ArrayList<>(32);
        for (DargonEventEnum dargonEvent : dargonEvents) {
            dargons.add(new Dargon(dargonEvent.getFlag()));
        }
        Collections.shuffle(dargons);

        // 核心战斗
        Dargon dargon;
        int t;
        for (int i = 1; i < 4; i++) {
            t = (int) (Math.random() * 7) + 8 * i;
            dargon = new Dargon(DargonEventEnum.fixed_war.getFlag(), i);
            dargons.add(t, dargon);
        }

        // Boss战斗
        dargon = new Dargon(DargonEventEnum.fixed_war.getFlag(), 4);
        dargons.add(dargon);

        List<Dargon> tempDargons = new ArrayList<>(dargons);
        Collections.sort(tempDargons, dargonComparator);
        
        initDargonWar(tempDargons, player);
        normal.setDargonEvent(dargons);
        normal.addDargonVipCount(1);
        
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(dargons);
        player.getSession().write(result);
        
    }

    /**
     * @param packet
     */
    public void warResults(DargonPacket packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int aid = packet.getAid();
        
        Dargon dargon = normal.getDargonEvent().get(aid - 1);
        int rewards = 0;
        if (packet.getResult() == 1) {
            dargon.setState(1);

            int flag = dargon.getFlag();
            DargonWar beDargonWar;
            if (DargonEventEnum.valueOf(flag).equals(DargonEventEnum.fixed_war)) {
                beDargonWar = dargonWar.get(dargon.getNucleus());
            } else {
                beDargonWar = dargonWar.get(0);
            }

            rewards = beDargonWar.getRewards();
            if (normal.getOnceDargonMoney() >= 0) {
                normal.setOnceDargonMoney(normal.getOnceDargonMoney() + rewards);
                taskDargonProvider.update(normal, normal.getOnceDargonMoney());
            }

            SessionUtils.incrementDargonMoney(rewards);

            // 如果当前位置是大龙则不记录
            if (aid != player.getNormal().getDargonEvent().size()) {
                player.getNormal().setRoleLocation(aid);
            }
        }
        
        normal.changeDargonNumber(1);
        
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(rewards);
        player.getSession().write(result);
    }

    public void buyDargonCounts(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        
        int counts = normal.getBuyDargonCounts();
        if (counts >= buyCounts.size()) {
            counts = buyCounts.size() - 1;
        }
        
        int cost = buyCounts.get(counts);
        
        JSONObject object = new JSONObject();
        object.put("place", "DargonBuyActionCounts");
        object.put("buyCounts", counts);
        SessionUtils.decrementDiamond(cost, object.toString());

        normal.setBuyDargonCounts(counts + 1);
        normal.changeDargonNumber(-1);
        
        player.getSession().write(Packet.createResult(packet));
    }

    private int getGold(Player player) {
        int random = (int) (Math.round(Math.random() * (goldCeiling - goldFloor)) + goldFloor);
        int copper = player.getRole().getLevel() * (random + levelGold);
        SessionUtils.incrementCopper(copper);
        return copper;
    }

    private int luckyEvent(Normal normal) {
        normal.changeDargonNumber(luckey);
        return luckey;
    }

    private int badEvent(Normal normal) {
        int number = Typhons.getInteger("typhon.spi.dargon.numbers");
        if (normal.getDargonNumber() + bad >= number) {
            normal.setDargonNumber(number - 1);
        } else {
            normal.changeDargonNumber(bad);
        }
        return bad;
    }

    private int getVigor(Player player) {
        int vigor = player.getNormal().getVigor();
        player.getNormal().setVigor(vigor + 2);
        return 2;
    }

    private int light(Player player, DargonPacket packet) {
        Dargon dargon = player.getNormal().getDargonEvent().get(packet.getGid() - 1);
        if (dargon.getState() == 0) {
            dargon.setState(2);
        }
        return packet.getGid();
    }

    private List<DargonDroplibrary> total(JSONArray jsonArray) {
        int count = 0;
        List<DargonDroplibrary> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            DargonDroplibrary dargon = new DargonDroplibrary();
            JSONObject obj = jsonArray.getJSONObject(i);
            dargon.setItemDobj(itemProvider.getItem(obj.getString("#item.id")));
            dargon.setPorb(obj.getIntValue("prob"));
            dargon.setMin(count);
            count += dargon.getPorb();
            dargon.setMax(count);
            list.add(dargon);
        }
        return list;
    }

    private Object randomReward(List<DargonDroplibrary> drops) {
        int random = RANDOM.nextInt(drops.get(drops.size() - 1).getMax());
        int count = 1;
        DargonObject obj = new DargonObject();
        for (DargonDroplibrary drop : drops) {
            if (random >= drop.getMin() && random < drop.getMax()) {
                if (!drop.getItemDobj().getId().startsWith("h")) {
                    int index = RANDOM.nextInt(100);
                    if (index >= 30) {
                        count = 3;
                    } else if (index >= 2) {
                        count = 2;
                    }
                }

                obj.setSid(drop.getItemDobj().getId());
                obj.setCount(count);
                BagUtils.intoItem(drop.getItemDobj(), count);
                break;
            }
        }
        return obj;
    }

    private void initDargonWar(List<Dargon> dargons, Player player) {
        DargonPvpRival rival;
        for (Dargon dargon : dargons) {
            
            if (dargon.getFlag() == DargonEventEnum.fixed_war.getFlag()) {
                for (;;) {
                    rival = fixedWar(player, dargon);
                    if (rival != null) {
                        break;
                    }
                }
            } else if (dargon.getFlag() == DargonEventEnum.random_war.getFlag()) {
                for (;;) {
                    rival = randomWar(player, dargon);
                    if (rival != null) {
                        break;
                    }
                }
            } else {
                break;
            }
            
            dargon.setHostInformation(rival);
        }
    }
    
    private class DargonComparator implements Comparator<Dargon> {

        @Override
        public int compare(Dargon o1, Dargon o2) {
            return Integer.compare(o1.getFlag(), o2.getFlag());
        }
    }
}
