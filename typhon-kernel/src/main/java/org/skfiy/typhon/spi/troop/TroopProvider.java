package org.skfiy.typhon.spi.troop;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.domain.Bag.Node;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.dobj.TroopItemDobj;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.ITroop;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Troop;
import org.skfiy.typhon.domain.item.AcePack;
import org.skfiy.typhon.domain.item.SimpleItem;
import org.skfiy.typhon.domain.item.TroopItem;
import org.skfiy.typhon.packet.MultipleValue;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.packet.TroopStrengPacket;
import org.skfiy.typhon.spi.role.ExpLevel;
import org.skfiy.typhon.util.FastRandom;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TroopProvider extends AbstractComponent {

    private static final int TROOP_MAX_LEVEL = 15;
    private static final Random RANDOM = new FastRandom();
    private static final String[] STRONG_SECONDARY_KEYS = {"critRate", "critMagn", "decritRate",
            "parryRate", "parryValue", "deparryRate"};

    @Inject
    private RoleProvider roleProvider;

    private final Map<ITroop.Type, JSONArray> TROOP_CONFIGS = new HashMap<>();
    private final JSONArray COST_ARRAY = new JSONArray();

    private JSONObject strongSecondaryProperties;

    @Override
    protected void doInit() {
        TROOP_CONFIGS.put(ITroop.Type.XF,
                JSON.parseArray(ComponentUtils.readDataFile("troop_xf_property.json")));
        TROOP_CONFIGS.put(ITroop.Type.JS,
                JSON.parseArray(ComponentUtils.readDataFile("troop_js_property.json")));
        TROOP_CONFIGS.put(ITroop.Type.YB,
                JSON.parseArray(ComponentUtils.readDataFile("troop_yb_property.json")));
        TROOP_CONFIGS.put(ITroop.Type.ZJ,
                JSON.parseArray(ComponentUtils.readDataFile("troop_zj_property.json")));
        TROOP_CONFIGS.put(ITroop.Type.ZZ,
                JSON.parseArray(ComponentUtils.readDataFile("troop_zz_property.json")));

        COST_ARRAY.addAll(JSON.parseArray(ComponentUtils.readDataFile("troop_cost.json")));

        strongSecondaryProperties =
                JSON.parseObject(ComponentUtils
                        .readDataFile("troop_strong_secondary_properties.json"));
    }

    @Override
    protected void doDestroy() {

    }

    @Override
    protected void doReload() {}

    // New begin
    public boolean isMaxLevel(TroopItem troopItem) {
        return (troopItem.getLevel() >= TROOP_MAX_LEVEL);
    }

    /**
     * 战位强化的最大等级.
     *
     * @param roleLevel
     * @param troopLevel
     * @return
     */
    public boolean isMaxLevel(int roleLevel, int troopLevel) {
        ExpLevel expLevel = roleProvider.getExpLevel(roleLevel);
        return (troopLevel >= expLevel.getTroopMaxLevel());
    }

    private void isCost(int level, int exp) {
        int cost = exp * COST_ARRAY.getJSONObject(level).getIntValue("cost");
        SessionUtils.decrementCopper(cost);
    }

    /**
     * 升级强化锦囊.
     *
     * @param packet 协议包
     */
    public void harden(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Bag bag = player.getBag();
        int pos = (int) packet.getVal();

        Node node = bag.findNode(pos);
        if (node == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("No troopItem[" + pos + "]");
            player.getSession().write(error);
            return;
        }

        TroopItem troopItem = node.getItem();
        if (isMaxLevel(troopItem)) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("MaxLevel");
            player.getSession().write(error);
            return;
        }

        TroopItemDobj troopItemDobj = troopItem.getItemDobj();
        int upgradeTexp = troopItemDobj.getUpgradeTexp(troopItem.getLevel());
        if (player.getNormal().getTexp() < upgradeTexp) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Not enough texp");
            player.getSession().write(error);
            return;
        }

        int level = troopItem.getLevel();
        int masterLevel = troopItemDobj.getMasterLevel(level);
        if (player.getRole().getLevel() < masterLevel) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Don't enough master level");
            player.getSession().write(error);
            return;
        }

        //锦囊强化
        int count = player.getNormal().getDailyTask().getTaskHardenStreng();
        if (count >= 0) {
            player.getNormal().getDailyTask().setTaskHardenStreng(count + 1);
        }

        int copper = troopItemDobj.getCopper(level);
        SessionUtils.decrementCopper(copper);

        int newLevel = level + 1;
        Normal normal = player.getNormal();
        normal.setTexp(normal.getTexp() - upgradeTexp);

        int atk = troopItem.getAtk() + getUpVal(troopItemDobj.getAtkUps(), level);
        int def = troopItem.getDef() + getUpVal(troopItemDobj.getDefUps(), level);
        int matk = troopItem.getMatk() + getUpVal(troopItemDobj.getMatkUps(), level);
        int mdef = troopItem.getMdef() + getUpVal(troopItemDobj.getMdefUps(), level);
        int hp = troopItem.getHp() + getUpVal(troopItemDobj.getHpUps(), level);

        // 强化至满级时增加额外的属性
        if (newLevel >= TROOP_MAX_LEVEL) {
            double f = Typhons.getDouble("typhon.spi.troop.harden.maxLevel.factor", 1.5);
            atk *= f;
            def *= f;
            matk *= f;
            mdef *= f;
            hp *= f;
        }

        // 增加属性
        troopItem.setAtk(atk);
        troopItem.setDef(def);
        troopItem.setMatk(matk);
        troopItem.setMdef(mdef);
        troopItem.setHp(hp);

        troopItem.setLevel(newLevel);

        // 增加随机属性
        if (newLevel != TROOP_MAX_LEVEL && newLevel % 3 == 0) {
            String key = STRONG_SECONDARY_KEYS[RANDOM.nextInt(STRONG_SECONDARY_KEYS.length)];
            JSONObject json = strongSecondaryProperties.getJSONObject(key);
            int min = json.getIntValue("min");
            int max = json.getIntValue("max");
            int v = min + RANDOM.nextInt(max - min + 1);
            switch (key) {
                case "critRate":
                    troopItem.setCritRate(troopItem.getCritRate() + v);
                    break;
                case "critMagn":
                    troopItem.setCritMagn(troopItem.getCritMagn() + v);
                    break;
                case "decritRate":
                    troopItem.setDecritRate(troopItem.getDecritRate() + v);
                    break;
                case "parryRate":
                    troopItem.setParryRate(troopItem.getParryRate() + v);
                    break;
                case "parryValue":
                    troopItem.setParryValue(troopItem.getParryValue() + v);
                    break;
                case "deparryRate":
                    troopItem.setDeparryRate(troopItem.getDeparryRate() + v);
                    break;
            }
        }

        // 强化升级时重新计算属性
        if (troopItem.getActiveType() != null) {
            calculateTroopProps(player);
        }

        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 分解锦囊.
     *
     * @param packet 协议包
     */
    public void resolve(MultipleValue packet) {
        Player player = SessionUtils.getPlayer();
        Bag bag = player.getBag();
        Normal normal = player.getNormal();

        Node node;
        TroopItem troopItem;
        int sumTexp = 0;

        for (Object obj : packet.getVals()) {
            node = bag.findNode((int) obj);
            if (node == null) {
                continue;
            }

            troopItem = node.getItem();
            if (troopItem.getActiveType() != null) {
                continue;
            }

            sumTexp += troopItem.getItemDobj().getSplitTexp(troopItem.getLevel());
            bag.decrementTotal(node, 1);
        }

        normal.setTexp(normal.getTexp() + sumTexp);
        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 计算锦囊属性.
     *
     * @param player 玩家信息
     */
    public void calculateTroopProps(Player player) {
        Normal normal = player.getNormal();
        for (Troop troop : normal.getTroops()) {
            AcePack acePack = new AcePack();
            troop.setAcePack(acePack);

            calculateTroopProps(player.getBag(), acePack, troop.getFirst());
            calculateTroopProps(player.getBag(), acePack, troop.getSecond());
            calculateTroopProps(player.getBag(), acePack, troop.getThird());
            calculateTroopProps(player.getBag(), acePack, troop.getFour());
            calculateTroopProps(player.getBag(), acePack, troop.getFive());
        }
    }

    //==============================================================================================
    // 经验,等级
    private void pushExp(TroopStrengPacket packet, Troop troop, JSONArray array) {
        Player player = SessionUtils.getPlayer();
        int roleLevel = player.getRole().getLevel();

        // 站位强化等级限制
        if (isMaxLevel(roleLevel, troop.getLevel())) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Level limit");
            player.getSession().write(error);
            return;
        }

        ExpLevel expLevel = roleProvider.getExpLevel(troop.getLevel() + 1);
        // 站位强化等级限制
        if (troop.getExp() >= expLevel.getTroopExp()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Exp limit");
            player.getSession().write(error);
            return;
        }

        List<Integer> list = packet.getIntensities();
        Set<Integer> set = new HashSet<>();

        int expAdd = 0;

        // 把消耗的东西归类放到set中
        for (int i = 0; i < list.size(); i++) {
            set.add(list.get(i));
        }

        // 包消耗的同一类物品统一销毁
        for (int e : set) {
            int count = 0;
            Node node = player.getBag().findNode(e);
            SimpleItem simpleitem = (SimpleItem) node.getItem();
            for (int j = 0; j < list.size(); j++) {
                if (e == list.get(j)) {
                    count++;
                }
                if (j == list.size() - 1) {
                    expAdd += (int) simpleitem.getItemDobj().getAnnex() * count;
                    if (!player.getBag().decrementTotal(node, count)) {
                        PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
                        error.setText("node is enought troopStreng");
                        player.getSession().write(error);
                        return;
                    }
                }
            }
        }

        int newExp = troop.getExp() + expAdd;
        int newLevel = troop.getLevel();

        while (true) {
            expLevel = roleProvider.getExpLevel(newLevel + 1);
            if (newExp < expLevel.getTroopExp()) {
                break;
            }

            newExp -= expLevel.getTroopExp();
            newLevel++;

            // 超过部分经验清零不计算金币花销
            if (isMaxLevel(roleLevel, newLevel)) {
                newExp = 0;
                break;
            }
        }
        //站位强化
        int count = player.getNormal().getDailyTask().getTaskTroopStreng();
        if (count >= 0) {
            player.getNormal().getDailyTask().setTaskTroopStreng(count + 1);
        }

        // 玩家没有升级扣除金币
        if (troop.getLevel() == newLevel) {
            isCost(troop.getLevel(), expAdd);
        } else {
            // 玩家升级扣除最低级的金币
            isCost(troop.getLevel(), roleProvider.getExpLevel(troop.getLevel() + 1).getTroopExp()
                    - troop.getExp());
        }

        counting(newExp, newLevel, troop, array, roleLevel);
        player.getSession().write(Packet.createResult(packet));
    }

    // 站位升级
    public void rise(TroopStrengPacket packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        ITroop.Type type = ITroop.Type.valueOf(packet.getTid());

        Troop troop = normal.getTroop(type);
        JSONArray array = TROOP_CONFIGS.get(type);

        pushExp(packet, troop, array);
    }

    //计算升级的属性
    private void counting(int newExp, int newLevel, Troop troop, JSONArray array, int roleLevel) {
        int level = troop.getLevel();
        if (newLevel > level) {
            int a = newLevel - level;
            JSONObject json;
            for (int i = 0; i < a; i++) {
                json = array.getJSONObject(troop.getLevel() + i);

                troop.setAtk(troop.getAtk() + json.getIntValue("atkUp"));
                troop.setDef(troop.getDef() + json.getIntValue("defUp"));
                troop.setMatk(troop.getMatk() + json.getIntValue("matkUp"));
                troop.setMdef(troop.getMdef() + json.getIntValue("mdefUp"));
                troop.setHp(troop.getHp() + json.getIntValue("hpUp"));

                troop.setCritRate(troop.getCritRate() + json.getIntValue("critRateUp"));
                troop.setDecritRate(troop.getDecritRate() + json.getIntValue("decritRateUp"));
                troop.setCritMagn(troop.getCritMagn() + json.getIntValue("critMagn"));

                troop.setParryRate(troop.getParryRate() + json.getIntValue("parryRate"));
                troop.setDeparryRate(troop.getDeparryRate() + json.getIntValue("deparryRate"));
                troop.setParryValue(troop.getParryValue() + json.getIntValue("parryValue"));

                //扣去玩家升级的当前等级的金币
                if (0 < i && i <= a - 1) {
                    isCost(level + i, roleProvider.getExpLevel(level + i + 1).getTroopExp());
                }
            }

            troop.setLevel(newLevel);

            if (!isMaxLevel(roleLevel, newLevel)) {
                //扣去不足以升级的剩下的经验金币
                isCost(troop.getLevel(), newExp);
            }
        }

        troop.setExp(newExp);
    }

    private void calculateTroopProps(Bag bag, AcePack acePack, int pos) {
        Node node = bag.findNode(pos);
        if (node != null) {
            TroopItem troopItem = node.getItem();
            acePack.setAtk(acePack.getAtk() + troopItem.getAtk());
            acePack.setDef(acePack.getDef() + troopItem.getDef());
            acePack.setMatk(acePack.getMatk() + troopItem.getMatk());
            acePack.setMdef(acePack.getMdef() + troopItem.getMdef());
            acePack.setHp(acePack.getHp() + troopItem.getHp());

            acePack.setCritRate(acePack.getCritRate() + troopItem.getCritRate());
            acePack.setCritMagn(acePack.getCritMagn() + troopItem.getCritMagn());
            acePack.setDecritRate(acePack.getDecritRate() + troopItem.getDecritRate());

            acePack.setParryRate(acePack.getParryRate() + troopItem.getParryRate());
            acePack.setParryValue(acePack.getParryValue() + troopItem.getParryValue());
            acePack.setDeparryRate(acePack.getDeparryRate() + troopItem.getDeparryRate());
        }
    }

    //    public void update
    private int getUpVal(int[] ups, int level) {
        if (ups != null && ups.length > level) {
            return ups[level];
        }
        return 0;
    }
}
