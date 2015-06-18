package org.skfiy.typhon.spi.hero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.IHeroEntity.Rabbet;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.packet.EnchantPacket;
import org.skfiy.typhon.packet.ExclusivePacket;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.store.Commoditied;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class HeroExclusiveProvider extends AbstractComponent {
    protected static final Random RANDOM = new Random();
    // 打造专属武器属性加成
    private final Map<String, ExclusiveBuild> exclusiveBuilds = new HashMap<>();
    // 打造消耗的物品
    private final List<ExclusiveBuildLimit> exclusiveBuildLimits = new ArrayList<>();
    // 专属武器初始属性
    private final Map<String, ExclusiveWeapon> exclusiveWeapons = new HashMap<>();
    // 强化消耗物品
    private final List<ExclusiveBuildLimit> strengCosts = new ArrayList<>();
    // 洗练消耗
    private final List<ExclusiveBuildLimit> exclusiveWashs = new ArrayList<>();
    @Inject
    private HeroProvider heroProvider;

    @Override
    protected void doInit() {
        JSONArray array;
        array = JSONArray.parseArray(ComponentUtils.readDataFile("weapons_build.json"));
        ExclusiveBuild build = null;
        for (int i = 0; i < array.size(); i++) {
            build = array.getObject(i, ExclusiveBuild.class);
            exclusiveBuilds.put(build.getId(), build);
        }

        array = JSONArray.parseArray(ComponentUtils.readDataFile("exclusive_weapons.json"));
        ExclusiveWeapon weapon = null;
        for (int i = 0; i < array.size(); i++) {
            weapon = array.getObject(i, ExclusiveWeapon.class);
            exclusiveWeapons.put(weapon.getId(), weapon);
        }

        exclusiveBuildLimits.addAll(JSONArray.parseArray(
                ComponentUtils.readDataFile("weapons_build_property.json"),
                ExclusiveBuildLimit.class));

        strengCosts.addAll(JSONArray.parseArray(
                ComponentUtils.readDataFile("weapons_intensify.json"), ExclusiveBuildLimit.class));

        exclusiveWashs.addAll(JSONArray.parseArray(
                ComponentUtils.readDataFile("weapons_wash.json"), ExclusiveBuildLimit.class));
    }

    @Override
    protected void doDestroy() {}

    @Override
    protected void doReload() {}



    public void exclusiveRabber(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Bag heroBag = player.getHeroBag();
        ExclusiveWeapon weapon = exclusiveWeapons.get((String) packet.getVal());
        HeroItem heroItem = heroBag.findNode(weapon.getHeroId()).getItem();
        if (heroItem.getWeaponsRabbets() != null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.no_exist);
            error.setText("WeaponsRabbets is exist");
            player.getSession().write(error);
            return;
        }
        Rabbet rabbet = new Rabbet();
        rabbet.setAtk(weapon.getAtk());
        rabbet.setMatk(weapon.getMatk());
        rabbet.setDef(weapon.getDef());
        rabbet.setMdef(weapon.getMdef());
        rabbet.setHp(weapon.getHp());
        heroItem.setWeaponsRabbets(rabbet);
        heroItem.setWeaponsEnchant(new Rabbet());
        heroItem.setWeaponsBuild(new Rabbet());
        heroItem.setWeaponsStreng(new Rabbet());
        heroProvider.calculatorPowerGuess(heroItem);
        player.getBag().decrementTotal((String) packet.getVal(), 1);
        player.getSession().write(Packet.createResult(packet));
    }

    public void exclusiveBuild(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Bag heroBag = player.getHeroBag();
        ExclusiveBuild build = exclusiveBuilds.get((String) packet.getVal());
        HeroItem heroItem =
                heroBag.findNode(exclusiveWeapons.get(build.getId()).getHeroId()).getItem();
        List<Commoditied> commoditieds = new ArrayList<>();
        if (heroItem == null || heroItem.getWeaponsRabbets() == null) {

            PacketError error = PacketError.createResult(packet, PacketError.Condition.no_exist);
            error.setText("WeaponsRabbets no exist");
            player.getSession().write(error);
            return;
        } else {
            if (heroItem.getBuildLevel() >= 5) {
                PacketError error =
                        PacketError.createResult(packet, PacketError.Condition.level_limit);
                error.setText("WeaponsBuild is top class");
                player.getSession().write(error);
                return;
            }
            ExclusiveBuildLimit buildLimit = exclusiveBuildLimits.get(heroItem.getBuildLevel());
            if (normal.getLevel() < buildLimit.getLevellimit()) {
                PacketError error =
                        PacketError.createResult(packet, PacketError.Condition.level_limit);
                error.setText("WeaponsBuild Level Limit");
                player.getSession().write(error);
                return;
            } else {
                if (cost(player, buildLimit)) {
                    PacketError error =
                            PacketError.createResult(packet, PacketError.Condition.item_not_found);
                    error.setText("WeaponsBuild CostItem is not enough");
                    player.getSession().write(error);
                    return;
                }
            }
            buildInformation(heroItem, build, (String) packet.getVal());

            if (heroItem.getWeaponsEnchant() != null) {
                commoditieds =
                        heroProvider.enchantReturn1(heroItem.getWeaponsEnchant().getExpSum());
                heroItem.setWeaponsEnchant(new Rabbet());
                player.getBag().decrementTotal(build.getSoulId(),
                        exclusiveBuildLimits.get(heroItem.getBuildLevel() - 1).getSoulCounts());
            }
        }
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);

        result.setVal(commoditieds);
        player.getSession().write(result);
    }

    private void buildInformation(HeroItem heroItem, ExclusiveBuild build, String eid) {
        Rabbet buildRabbet = heroItem.getWeaponsBuild();
        Rabbet rabbet = heroItem.getWeaponsStreng();
        if (rabbet == null) {
            rabbet = new Rabbet();
        }
        ExclusiveWeapon weapon = exclusiveWeapons.get(eid);
        ExclusiveBuildInformation information = build.getAttribute().get(heroItem.getBuildLevel());
        if (information.getName().equals("atk")) {
            buildRabbet.setAtk(information.getNumber()
                    + (int) (information.getCount() * (rabbet.getAtk() + weapon.getAtk()))
                    + buildRabbet.getAtk());
        }
        if (information.getName().equals("matk")) {
            buildRabbet.setMatk(information.getNumber()
                    + (int) (information.getCount() * (rabbet.getMatk() + weapon.getMatk()))
                    + buildRabbet.getMatk());
        }
        if (information.getName().equals("def")) {
            buildRabbet.setDef(information.getNumber()
                    + (int) (information.getCount() * (rabbet.getDef() + weapon.getDef()))
                    + buildRabbet.getDef());
        }
        if (information.getName().equals("mdef")) {
            buildRabbet.setMdef(information.getNumber()
                    + (int) (information.getCount() * (rabbet.getMdef() + weapon.getMdef()))
                    + buildRabbet.getMdef());
        }
        if (information.getName().equals("hp")) {
            buildRabbet.setHp(information.getNumber()
                    + (int) (information.getCount() * (rabbet.getHp() + weapon.getHp()))
                    + buildRabbet.getHp());
        }
        if (information.getName().equals("critMagn")) {
            buildRabbet.setCritMagn(information.getNumber()
                    + (int) (information.getCount() * rabbet.getCritMagn())
                    + buildRabbet.getCritMagn());
        }
        if (information.getName().equals("critRate")) {
            buildRabbet.setCritRate(information.getNumber()
                    + (int) (information.getCount() * rabbet.getCritRate())
                    + buildRabbet.getCritRate());
        }
        if (information.getName().equals("decritRate")) {
            buildRabbet.setDecritRate(information.getNumber()
                    + (int) (information.getCount() * rabbet.getDecritRate())
                    + buildRabbet.getDecritRate());
        }
        if (information.getName().equals("deparryRate")) {
            buildRabbet.setDeparryRate(information.getNumber()
                    + (int) (information.getCount() * rabbet.getDeparryRate())
                    + buildRabbet.getDeparryRate());
        }
        if (information.getName().equals("parryRate")) {
            buildRabbet.setParryRate(information.getNumber()
                    + (int) (information.getCount() * rabbet.getParryRate())
                    + buildRabbet.getParryRate());
        }
        if (information.getName().equals("parryValue")) {
            buildRabbet.setParryValue(information.getNumber()
                    + (int) (information.getCount() * rabbet.getParryValue())
                    + buildRabbet.getParryValue());
        }
        heroItem.setWeaponsBuild(buildRabbet);
        heroItem.setBuildLevel(heroItem.getBuildLevel() + 1);
        heroProvider.calculatorPowerGuess(heroItem);
    }

    public void exclusiveStreng(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Bag heroBag = player.getHeroBag();

        ExclusiveWeapon weapon = exclusiveWeapons.get((String) packet.getVal());
        HeroItem heroItem = heroBag.findNode(weapon.getHeroId()).getItem();
        Rabbet rabbet = heroItem.getWeaponsStreng();

        if (rabbet == null) {
            rabbet = new Rabbet();
        }
        ExclusiveBuildLimit strengCost = strengCosts.get(heroItem.getStrengLevel());
        if (normal.getLevel() < strengCost.getLevellimit()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.level_limit);
            error.setText("WeaponsBuild Level Limit");
            player.getSession().write(error);
            return;
        } else {
            if (cost(player, strengCost)) {
                PacketError error =
                        PacketError.createResult(packet, PacketError.Condition.item_not_found);
                error.setText("WeaponsBuild CostItem is not enough");
                player.getSession().write(error);
                return;
            }
        }

        switch ((heroItem.getStrengLevel() + 1) % 3) {
            case 1:
                rabbet.setAtk(rabbet.getAtk() + weapon.getAtkUp());
                rabbet.setMatk(rabbet.getMatk() + weapon.getMatkUp());
                break;
            case 2:
                rabbet.setDef(rabbet.getDef() + weapon.getDefUp());
                rabbet.setMdef(rabbet.getMdef() + weapon.getMdefUp());
                break;
            case 0:
                rabbet.setHp(rabbet.getHp() + weapon.getHpUp());
                break;
        }
        heroItem.setWeaponsStreng(rabbet);
        heroItem.setStrengLevel(heroItem.getStrengLevel() + 1);
        heroProvider.calculatorPowerGuess(heroItem);
        player.getSession().write(Packet.createResult(packet));
    }



    public void exclusiveEnchem(EnchantPacket packet) {
        Player player = SessionUtils.getPlayer();
        HeroItem heroItem = player.getHeroBag().findNode(packet.getPos()).getItem();
        if (heroItem.getWeaponsRabbets() == null) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("the hore is not equipment");
            player.getSession().write(error);
            return;
        }
        Rabbet rabbet =
                heroProvider.enchant(heroItem, player, packet, packet.getEid(),
                        heroItem.getWeaponsEnchant());
        if (rabbet != null) {
            heroItem.setWeaponsEnchant(rabbet);
            heroProvider.calculatorPowerGuess(heroItem);
        }
        player.getSession().write(Packet.createResult(packet));
    }

    public boolean limit(int level, String eid, HeroItem heroItem) {
        if (exclusiveBuilds.containsKey(eid)) {
            return level >= heroItem.getBuildLevel();
        }
        return false;
    }

    public void exclusiveEnchemOnce(EnchantPacket packet) {
        Player player = SessionUtils.getPlayer();
        HeroItem heroItem = player.getHeroBag().findNode(packet.getPos()).getItem();
        if (heroItem.getWeaponsRabbets() == null) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("the hore is not equipment");
            player.getSession().write(error);
            return;
        }
        int expAdd = 0;
        int number = 0;
        for (int a : packet.getExpendables()) {
            number += a;
            expAdd = (int) (a * Typhons.getDouble("typhon.spi.role.enchant.diamond.factor", 1));
        }

        Rabbet rabbet =
                heroProvider.start(expAdd, heroItem, packet.getEid(), heroItem.getWeaponsEnchant());

        if (rabbet != null) {
            heroItem.setWeaponsEnchant(rabbet);
            heroProvider.calculatorPowerGuess(heroItem);
            JSONObject object = new JSONObject();
            object.put("place", "ExclusiveEnchemOnce");
            object.put("heroId", heroItem.getId());
            object.put("heroStar", heroItem.getStar());
            object.put("equipmentPoint", packet.getPoint());
            SessionUtils.decrementDiamond(number, object.toString());
        }
        player.getSession().write(Packet.createResult(packet));
    }


    public void exclusiveWash(ExclusivePacket packet) {
        Player player = SessionUtils.getPlayer();
        Bag heroBag = player.getHeroBag();
        ExclusiveWeapon weapon = exclusiveWeapons.get(packet.getEid());
        HeroItem item = heroBag.findNode(weapon.getHeroId()).getItem();
        Rabbet rabbet = item.getWeaponsRabbets();
        Rabbet strengRabbet = item.getWeaponsStreng();
        Rabbet newWash = new Rabbet();

        if (strengRabbet == null) {
            strengRabbet = new Rabbet();
        }
        if (rabbet == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.no_exist);
            error.setText("WeaponsRabbets no exist");
            player.getSession().write(error);
            return;
        }
        ExclusiveBuildLimit exclusiveCost = exclusiveWashs.get(packet.getLevel());
        int factor = 0;
        if (!cost(player, exclusiveCost)) {
            factor = exclusiveCost.getFactor();
        } else {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("WeaponsBuild CostItem is not enough");
            player.getSession().write(error);
            return;
        }
        do {
            newWash.setAtk((RANDOM.nextInt(10) - 5) * factor);
            newWash.setMatk((RANDOM.nextInt(10) - 5) * factor);
            newWash.setDef((RANDOM.nextInt(10) - 5) * factor);
            newWash.setMdef((RANDOM.nextInt(10) - 5) * factor);
            newWash.setHp((RANDOM.nextInt(40) - 20) * factor);
        } while (!washBoolean(rabbet, newWash, strengRabbet, weapon, item.getBuildLevel()));
        player.getInvisible().setRabbet(newWash);
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(newWash);
        player.getSession().write(result);
    }

    public void exclusiveEnhancement2(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Bag heroBag = player.getHeroBag();
        ExclusiveWeapon weapon = exclusiveWeapons.get((String) packet.getVal());
        HeroItem heroItem = heroBag.findNode(weapon.getHeroId()).getItem();
        Rabbet rabbet = heroItem.getWeaponsRabbets();
        Rabbet rabbetWash = player.getInvisible().getRabbet();
        rabbet.setAtk(Math.max(rabbet.getAtk() + rabbetWash.getAtk(), weapon.getAtk()));
        rabbet.setMatk(Math.max(rabbet.getMatk() + rabbetWash.getMatk(), weapon.getMatk()));
        rabbet.setDef(Math.max(rabbet.getDef() + rabbetWash.getDef(), weapon.getDef()));
        rabbet.setMdef(Math.max(rabbet.getMdef() + rabbetWash.getMdef(), weapon.getMdef()));
        rabbet.setHp(Math.max(rabbet.getHp() + rabbetWash.getHp(), weapon.getHp()));
        heroItem.setWeaponsRabbets(rabbet);
        heroProvider.calculatorPowerGuess(heroItem);
        player.getSession().write(Packet.createResult(packet));
    }

    private boolean cost(Player player, ExclusiveBuildLimit cost) {
        Bag bag = player.getBag();
        boolean bool = false;
        if (cost.getCostType().equals("D")) {
            JSONObject object = new JSONObject();
            object.put("place", "ExclusiveBuild/Streng/Wash");
            SessionUtils.decrementDiamond(cost.getCounts(), object.toString());
        } else {
            SessionUtils.decrementCopper(cost.getCounts());
        }
        if (!cost.getCostItems().isEmpty()) {
            for (ExclusiveBuildInformation information : cost.getCostItems()) {
                if (!player.getBag().decrementTotal(bag.findNode(information.getName()),
                        information.getNumber())) {
                    bool = true;
                    break;
                }
            }
        }
        return bool;
    }


    private boolean washBoolean(Rabbet oldRabbet, Rabbet newRabbet, Rabbet strengRabbet,
            ExclusiveWeapon weapon, int level) {
        return oldRabbet.getAtk() + newRabbet.getAtk() - weapon.getAtk() <= weapon.getAtk()
                + weapon.getAtkUp() * Math.max(level / 3, 0)

                && oldRabbet.getMatk() + newRabbet.getMatk() - weapon.getMatk() <= weapon.getMatk()
                        + weapon.getMatkUp() * Math.max(level / 3, 0)

                && oldRabbet.getDef() + newRabbet.getDef() - weapon.getDef() <= weapon.getDef()
                        + weapon.getDefUp() * Math.max(level / 3, 0)

                && oldRabbet.getMdef() + newRabbet.getMdef() - weapon.getMdef() <= weapon.getMdef()
                        + weapon.getMdefUp() * Math.max(level / 3, 0)

                && oldRabbet.getHp() + newRabbet.getHp() - weapon.getHp() <= weapon.getHp()
                        + weapon.getHpUp() * Math.max(level / 3, 0);
    }
}
