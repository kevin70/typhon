package org.skfiy.typhon.spi.society;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.domain.Mail;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.packet.SocietyBossHp;
import org.skfiy.typhon.packet.SocietyBossPacket;
import org.skfiy.typhon.repository.IncidentRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.Vip;
import org.skfiy.typhon.spi.pvp.PvpProvider;
import org.skfiy.typhon.spi.store.Commoditied;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SocietyBossProvider extends AbstractComponent {

    private final Map<String, SocietyBosses> allocationBossWish = new HashMap<>();
    private final Map<Integer, SocietyMonster> allocationMonsters = new HashMap<>();
    private final List<Commoditied> addwishCounts = new ArrayList<>();
    private final List<WishCost> wishCosts = new ArrayList<>();
    private int rewardVigor;
    private double monsterFactor;
    // 公会Boss出现的CD
    private int bossCDTime;
    // 攻打公会boss消耗体力
    private int pveBossVigor;
    // 攻打BossCD时间分钟
    private int pvebossCDTime;
    // 充值CD消耗钻石
    private int resetBossCD;


    @Inject
    private SocietyProvider societyProvider;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private PvpProvider pvpProvider;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private IncidentRepository incidentRepository;

    @Override
    protected void doInit() {
        JSONArray array;

        array = JSONArray.parseArray(ComponentUtils.readDataFile("society_monster.json"));
        SocietyMonster societyMonster = null;
        for (int i = 0; i < array.size(); i++) {
            societyMonster = array.getObject(i, SocietyMonster.class);
            allocationMonsters.put(societyMonster.getLevel(), societyMonster);
        }

        array = JSONArray.parseArray(ComponentUtils.readDataFile("society_boss.json"));
        SocietyBosses societyBosses = null;
        for (int i = 0; i < array.size(); i++) {
            societyBosses = array.getObject(i, SocietyBosses.class);
            allocationBossWish.put(societyBosses.getId(), societyBosses);
        }

        monsterFactor = Typhons.getDouble("typhon.spi.society.monsterFactor");
        rewardVigor = Typhons.getInteger("typhon.spi.society.vigor");
        bossCDTime = Typhons.getInteger("typhon.spi.society.bossTime");
        addwishCounts.addAll(JSONArray.parseArray(
                ComponentUtils.readDataFile("addwishCounts.json"), Commoditied.class));

        wishCosts.addAll(JSONArray.parseArray(ComponentUtils.readDataFile("wish_cost.json"),
                WishCost.class));

        pveBossVigor = Typhons.getInteger("typhon.spi.PveBossVigor");
        pvebossCDTime = Typhons.getInteger("typhon.spi.BossCDTime");
        resetBossCD = Typhons.getInteger("typhon.spi.ResetBossCD");

    }

    @Override
    protected void doReload() {}

    @Override
    protected void doDestroy() {

    }

    public synchronized void societyBossWish(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Society society = societyProvider.findBySid(normal.getSocietyId());
        // 公会是否为null
        if (society == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.level_limit);
            error.setText("Society is null");
            player.getSession().write(error);
            return;
        }
        // Object转换成List
        List<String> playerWish = (List<String>) packet.getVal();
        // 以召唤的Boss及血量
        List<List<SocietyBossHp>> bosses = society.getSocietyBosses();
        // 许愿boss
        Map<String, Integer> wishBosses = society.getSocietyWishCounts();
        Bag bag = player.getHeroBag();
        int count = 0;

        for (int i = 0; i < playerWish.size(); i++) {
            // 已经召唤的Boss
            if (findBoss(playerWish.get(i), bosses) != null) {
                PacketError error =
                        PacketError.createResult(packet, PacketError.Condition.level_limit);
                error.setText("The boss has been called");
                player.getSession().write(error);
                return;
            }

            // 许愿Boss是否玩家拥有
            for (int j = 0; j < bag.getNodes().size(); j++) {
                if (bag.getNodes().get(j).getItem().getId()
                        .equals(allocationBossWish.get(playerWish.get(i)).getId())) {
                    count++;
                    continue;
                }
            }
        }

        if (count != 2) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.level_limit);
            error.setText("The User not have heroes");
            player.getSession().write(error);
            return;
        }
        // 许愿扣取金币
        WishCost wishCost = null;
        if (normal.getSocietyWishs() >= wishCosts.size()) {
            wishCost = wishCosts.get(wishCosts.size() - 1);
        } else {
            wishCost = wishCosts.get(normal.getSocietyWishs());
        }

        if (wishCost.getCostType().equals("D")) {
            JSONObject object = new JSONObject();
            object.put("place", "SocietyWishCost");
            SessionUtils.decrementDiamond(wishCost.getCost(), object.toString());
        } else {
            SessionUtils.decrementCopper(wishCost.getCost());
        }

        for (int i = 0; i < playerWish.size(); i++) {
            int level = 0;
            SocietyBosses societyBosses = allocationBossWish.get(playerWish.get(i));
            String wishId = societyBosses.getId();

            if (wishBosses.containsKey(playerWish.get(i))) {
                wishBosses.put(wishId, wishBosses.get(playerWish.get(i)) + 1);
            } else {
                wishBosses.put(wishId, 1);
            }
            if (wishBosses.get(playerWish.get(i)) >= societyBosses.getEnergy()) {
                List<SocietyBossHp> monsterHp = new ArrayList<>();

                // BossHP计算
                for (Member member : society.getMembers()) {
                    level += member.getLevel();
                }
                level = level / society.getMembers().size();
                int hp = allocationMonsters.get(level).getHp() * society.getMembers().size() * 3;
                // 增加Boss
                monsterHp.add(new SocietyBossHp(hp, "boss", wishId, 0));
                // 增加小兵
                if (societyBosses.getDogfaces() != null) {
                    for (int j = 0; j < societyBosses.getDogfaces().length; j++) {
                        // FIXME hp=? 小怪
                        monsterHp.add(new SocietyBossHp((int) (hp * monsterFactor), "dogface",
                                societyBosses.getDogfaces()[j], 0));
                    }
                }
                bosses.add(monsterHp);
                wishBosses.remove(wishId);
            }
        }
        normal.setSocietyWishs(normal.getSocietyWishs() + 1);
        // 许愿通告
        society.getMessages().add(
                new Message(System.currentTimeMillis(), player.getRole().getName(), playerWish
                        .get(0), playerWish.get(1)));

        normal.setVigor(normal.getVigor() + rewardVigor);
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(societyMassage(society));
        player.getSession().write(result);
    }

    public void pveBosses(SocietyBossPacket packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        String bossId = packet.getBossId();
        Society society = societyProvider.findBySid(normal.getSocietyId());
        if (society == null) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("The User not have heroes");
            player.getSession().write(error);
            return;
        }

        if (normal.getSocietyPveBossCD() != 0
                && normal.getSocietyPveBossCD() > System.currentTimeMillis()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.time_over);
            error.setText("The PveBossCDTime is not over");
            player.getSession().write(error);
            return;
        }

        if (society.getBossTime() > System.currentTimeMillis()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.time_over);
            error.setText("BossCD");
            player.getSession().write(error);
            return;
        }

        List<List<SocietyBossHp>> societyBosses = society.getSocietyBosses();

        List<SocietyBossHp> societyBossHp = findBoss(bossId, societyBosses);

        if (societyBossHp == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.no_exist);
            error.setText("Boss does not exist");
            player.getSession().write(error);
            return;
        }
        List<SocietyBossHp> atkHp = packet.getSocietyBossHp();

        SocietyBossHp atkSocietyBossHp = null;
        int count = 0;
        int atkSum = 0;
        // FIXME Synchronized(society)
        synchronized (society) {
            for (int i = 0; i < atkHp.size(); i++) {
                atkSocietyBossHp = atkHp.get(i);

                for (int j = 0; j < societyBossHp.size(); j++) {
                    SocietyBossHp remainSocietyBossHp = societyBossHp.get(j);

                    if (atkSocietyBossHp.getId().equals(remainSocietyBossHp.getId())) {
                        societyBossHp.get(j).setAtk(
                                societyBossHp.get(j).getAtk() + atkSocietyBossHp.getAtk());
                        atkSum += atkSocietyBossHp.getAtk();

                        if (remainSocietyBossHp.getHp() <= remainSocietyBossHp.getAtk()) {
                            count++;
                        }
                        break;
                    }
                }
            }
        }
        List<SocietyBossHp> bossBool = findBoss(bossId, societyBosses);

        if (bossBool != null) {
            societyBossRanking(society, packet.getFidx(), atkSum, player);
        }
        // 判断boss是不是没血量
        if (count == atkHp.size() && bossBool != null) {
            societyBosses.remove(societyBossHp);
            society.setBossTime(System.currentTimeMillis() + (bossCDTime * 60 * 1000));
            sendEmail(bossId, society);
            society.getHurtRanking().clear();
        }

        int counts = normal.getDailyTask().getTaskSocietyBoss();
        if (counts >= 0) {
            normal.getDailyTask().setTaskSocietyBoss(counts + 1);
        }

        normal.setSocietyPveBossCD(System.currentTimeMillis() + (pvebossCDTime * 60 * 1000));
        normal.setVigor(normal.getVigor() - pveBossVigor);

        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(societyMassage(society));
        player.getSession().write(result);
    }

    // 获取公会Boss Wish Message 信息
    public void gettingWishInformation(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Society society = societyProvider.findBySid(normal.getSocietyId());

        if (society == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("The User not have heroes");
            player.getSession().write(error);
            return;
        }
        if (!society.isAddWishCount()) {
            for (Commoditied com : addwishCounts) {
                society.getSocietyWishCounts().put(com.getId(), com.getCount());
            }
            society.setAddWishCount(true);
        }
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(societyMassage(society));
        player.getSession().write(result);
    }

    private Object societyMassage(Society society) {
        JSONObject object = new JSONObject();
        object.put("wish", society.getSocietyWishCounts());
        object.put("boss", society.getSocietyBosses());
        object.put("message", society.getMessages());
        object.put("startTime", society.getBossTime());
        return object;
    }

    private void societyBossRanking(Society society, int fidx, int hurt, Player player) {
        synchronized (society) {
            int uid = player.getRole().getRid();
            List<MemberInformation> hurtRanking = society.getHurtRanking();
            boolean updateHero = false;
            MemberInformation oldMemberInformation = null;
            MemberInformation memberInformation = null;

            if (hurtRanking.isEmpty()) {
                memberInformation = returnMemberInformation(fidx, player, uid);
                memberInformation.setSocietyHurt(hurt);
                hurtRanking.add(memberInformation);
            } else {
                int index = hurtRanking.size();
                for (int i = index - 1; i >= 0; i--) {
                    if (hurtRanking.get(i).getUid() == uid) {
                        int oldHurt = hurtRanking.get(i).getSocietyHurt();
                        if (oldHurt > hurt) {
                            updateHero = true;
                            oldMemberInformation = hurtRanking.get(i);
                        }
                        hurt += oldHurt;
                        hurtRanking.remove(i);
                        break;
                    }
                }

                boolean added = false;
                for (int i = 0; i < hurtRanking.size(); i++) {
                    memberInformation = hurtRanking.get(i);
                    if (hurt > memberInformation.getSocietyHurt()) {
                        if (!updateHero) {
                            oldMemberInformation = returnMemberInformation(fidx, player, uid);
                        }
                        oldMemberInformation.setSocietyHurt(hurt);
                        hurtRanking.add(i, oldMemberInformation);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    if (!updateHero) {
                        oldMemberInformation = returnMemberInformation(fidx, player, uid);
                    }
                    oldMemberInformation.setSocietyHurt(hurt);
                    hurtRanking.add(oldMemberInformation);
                }
            }
        }
    }

    private void removeSpilthRanking(List<MemberInformation> hurtRanking, int rid) {
        MemberInformation member;
        for (int i = 0; i < hurtRanking.size(); i++) {
            member = hurtRanking.get(i);
            if (member.getUid() == rid) {
                hurtRanking.remove(i);
                break;
            }
        }
    }

    // 获取排行榜数据
    public void gettingRanking(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Society society = societyProvider.findBySid(normal.getSocietyId());
        if (society == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("The User not have heroes");
            player.getSession().write(error);
            return;
        }
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(society.getHurtRanking());
        player.getSession().write(result);
    }

    public void resetBossCDTime(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        JSONObject object = new JSONObject();
        object.put("place", "ResetBossCDTime");
        SessionUtils.decrementDiamond(resetBossCD, object.toString());
        normal.setSocietyPveBossCD(0);
        player.getSession().write(Packet.createResult(packet));
    }


    private List<Object> teamInformation(int fidx, Normal normal) {
        FightGroup fightGroup = normal.getFightGroup(fidx);
        List<Object> objects = new ArrayList<>();
        for (HeroItem heroItem : fightGroup.getHeroItems()) {
            objects.add(pvpProvider.newHeroJSONObject(heroItem));
        }
        return objects;
    }

    private MemberInformation returnMemberInformation(int fidx, Player player, int uid) {
        MemberInformation member = new MemberInformation();
        Normal normal = player.getNormal();
        member.setAvatar(normal.getAvatar());
        member.setAvatarBorder(normal.getAvatarBorder());
        member.setName(player.getRole().getName());
        member.setLevel(normal.getLevel());
        member.setUid(uid);
        member.setHeroes(teamInformation(fidx, normal));
        member.setPowerGuessSum(roleProvider.findHeroFighting(uid));
        return member;
    }

    // 奖励发邮件
    private void sendEmail(String bossid, Society society) {
        String[] str = allocationBossWish.get(bossid).getRewards();
        Mail mail;
        String id = null;
        for (int i = 0; i < society.getMembers().size(); i++) {

            int uid = society.getMembers().get(i).getRid();
            int number = -1;

            for (int j = 0; j < society.getHurtRanking().size(); j++) {
                if (uid == society.getHurtRanking().get(j).getUid()) {

                    if (j < 3) {
                        id = str[j];
                    } else if (j < 10) {
                        id = str[3];
                    } else if (j < 20) {
                        id = str[4];
                    } else {
                        id = str[5];
                    }
                    number = j;
                    break;
                }

                if (j == society.getHurtRanking().size() - 1) {
                    id = str[6];
                }
            }
            mail = new Mail();
            mail.setTitle("公会boss奖励");
            if (number != -1) {
                mail.setContent(String.format("怨灵BOSS已击杀，你总伤害排行%1d名，获得", number + 1));
            } else {
                mail.setContent(String.format("怨灵BOSS已击杀，获得公会基础奖励"));
            }
            mail.setAppendix(id);
            mail.setCount(1);
            mail.setType(Mail.SOCIETY_REWARD_TYPE);
            mail.setCreationTime(System.currentTimeMillis());
            roleProvider.sendMail(uid, mail);
        }
    }

    private List<SocietyBossHp> findBoss(String id, List<List<SocietyBossHp>> societyBosses) {
        for (int i = 0; i < societyBosses.size(); i++) {
            List<SocietyBossHp> beBosses = societyBosses.get(i);

            for (int j = 0; j < beBosses.size(); j++) {
                if (beBosses.get(j).getId().equals(id) && beBosses.get(j).getType().equals("boss")) {
                    return beBosses;
                }
            }
        }
        return null;
    }
}
