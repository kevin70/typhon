package org.skfiy.typhon.spi.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Bag;
import org.skfiy.typhon.domain.CDKeyObject;
import org.skfiy.typhon.domain.Invisible;
import org.skfiy.typhon.domain.InviteUsers;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.domain.item.MonthCardObject;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.PacketNotice;
import org.skfiy.typhon.packet.Platform;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.repository.RoleRepository;
import org.skfiy.typhon.repository.UserRepository;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionConstants;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.NoticeBoardProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.Vip;
import org.skfiy.typhon.spi.activity.AtlasHeros.Type;
import org.skfiy.typhon.spi.hero.ExclusiveBuildInformation;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Singleton
public class ActivityProvider extends AbstractComponent {

    protected static final Random RANDOM = new Random();
    // 摇钱树表
    private final List<CashCow> cashCowlists = new ArrayList<>();
    // 摇钱树暴击表
    private final List<CashCowCrit> cashCowCrits = new ArrayList<>();
    // 图鉴
    private final List<AtlasHeros> atlasHeros = new ArrayList<>();
    // 七天登录礼包.
    private final List<LoginGift> loginGift = new ArrayList<>();
    // 开服三天冲级礼包.
    private final List<UpgradeGift> upgradeGifts = new ArrayList<>();
    // 首充礼包.
    private final List<ItemsObject> firstRecharg = new ArrayList<>();
    // 成长基金
    private final List<GrowthFund> growthFunds = new ArrayList<>();
    // 摇钱树表
    private final List<LuckeyDraw> luckeyDraws = new ArrayList<>();
    // 成长基金消费
    private int growthFundMoney;

    private final List<InviteReward> inviteRewards = new ArrayList<>();
    // Vip购买礼包
    private final List<VipExclusiveGift> vipExclusiveGifts = new ArrayList<>();
    // Vip免费礼包
    private final List<VipExclusiveGift> vipFreeGifts = new ArrayList<>();
    // Vip活动期间的礼包
    private final List<VipGift> vipActivityGifts = new ArrayList<>();
    // Vip活动期间内每天的礼包
    private final List<VipGift> vipDayGifts = new ArrayList<>();
    // 随机Box
    private final Map<String, List<RandomBoxItem>> randomBoxs = new HashMap<>();
    // 印花兑换
    private final List<StampItems> stamps = new ArrayList<>();
    // 邀请码兑换
    private String inviteReward;
    private int inviteLevelLimit;
    private int inital;
    private int levelInital;
    private int monthCardDiamond;

    private long vipActivityStar;
    private long vipActivityEnd;
    private long vipDayStar;
    private long vipDayEnd;
    // 印花开始/结束时间/每天购买限制/价格
    private long stampStar;
    private long stampEnd;
    private int stampLimit;
    private int stampCost;

    @Inject
    private RoleProvider roleProvider;
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private UserRepository userRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private NoticeBoardProvider noticeBoardProvider;


    @Override
    protected void doDestroy() {}

    @Override
    protected void doInit() {
        JSONArray array;
        inital = Typhons.getInteger("typhon.spi.cashCow.initial");
        levelInital = Typhons.getInteger("typhon.spi.cashCow.level.initial");
        monthCardDiamond = Typhons.getInteger("typhon.spi.draw.monthCard");

        atlasHeros.addAll(JSONArray.parseArray(ComponentUtils.readDataFile("atlas_heros.json"),
                AtlasHeros.class));

        array = JSONArray.parseArray(ComponentUtils.readDataFile("diamond_exchange_gold.json"));
        int count = 0;
        for (int i = 0; i < array.size(); i++) {
            CashCow obj = array.getObject(i, CashCow.class);
            obj.setRandBegin(count);
            count += obj.getProb();
            obj.setRandEnd(count);
            cashCowlists.add(obj);
        }
        // 暴击倍率表
        cashCowCrits.addAll(JSONArray.parseArray(
                ComponentUtils.readDataFile("diamond_exchange_gold_crit.json"), CashCowCrit.class));

        array = JSONArray.parseArray(ComponentUtils.readDataFile("login_gift.json"));
        for (int i = 0; i < array.size(); i++) {

            LoginGift login = new LoginGift();
            login.setId(array.getJSONObject(i).getString("lid"));
            JSONArray jsonArray = array.getJSONObject(i).getJSONArray("item");

            List<ItemsObject> list = new ArrayList<>();
            for (int j = 0; j < jsonArray.size(); j++) {

                ItemsObject item = new ItemsObject();
                item.setItemDobj(itemProvider.getItem(jsonArray.getJSONObject(j).getString(
                        "#item.id")));
                item.setCount(jsonArray.getJSONObject(j).getInteger("count"));
                list.add(item);
            }
            login.setGiftList(list);
            loginGift.add(login);
        }


        array = JSONArray.parseArray(ComponentUtils.readDataFile("upgrade_gift.json"));
        for (int i = 0; i < array.size(); i++) {

            UpgradeGift upgrade = array.getObject(i, UpgradeGift.class);
            JSONArray jsonArray = array.getJSONObject(i).getJSONArray("items");

            List<ItemsObject> list = new ArrayList<>();
            for (int j = 0; j < jsonArray.size(); j++) {

                ItemsObject item = new ItemsObject();
                item.setItemDobj(itemProvider.getItem(jsonArray.getJSONObject(j).getString(
                        "#item.id")));
                item.setCount(jsonArray.getJSONObject(j).getInteger("count"));
                list.add(item);
            }
            upgrade.setItems(list);
            upgradeGifts.add(upgrade);
        }
        array =
                JSONArray.parseArray(ComponentUtils
                        .readDataFile("first_recharging_diamond_gift.json"));
        for (int j = 0; j < array.size(); j++) {
            ItemsObject item = new ItemsObject();
            item.setItemDobj(itemProvider.getItem(array.getJSONObject(j).getString("#item.id")));
            item.setCount(array.getJSONObject(j).getInteger("count"));
            firstRecharg.add(item);
        }

        growthFunds.addAll(JSONArray.parseArray(ComponentUtils.readDataFile("growth_fund.json"),
                GrowthFund.class));
        growthFundMoney = Typhons.getInteger("typhon.spi.roleProvider.growthFund");

        array = JSONArray.parseArray(ComponentUtils.readDataFile("invite_reward.json"));
        for (int i = 0; i < array.size(); i++) {
            InviteReward inviteReward = array.getObject(i, InviteReward.class);
            inviteReward.setItemId(itemProvider.getItem(array.getJSONObject(i)
                    .getString("#item.id")));
            inviteRewards.add(inviteReward);
        }

        array = JSONArray.parseArray(ComponentUtils.readDataFile("topup_luckeydraw.json"));
        int number = 0;
        LuckeyDraw lucketyDraw = null;
        for (int i = 0; i < array.size(); i++) {
            lucketyDraw = array.getObject(i, LuckeyDraw.class);
            lucketyDraw.setItem(itemProvider.getItem(array.getJSONObject(i).getString("#item.id")));
            lucketyDraw.setRandBegin(number);
            number += lucketyDraw.getProb();
            lucketyDraw.setRandEnd(number);
            luckeyDraws.add(lucketyDraw);
        }

        inviteReward = Typhons.getProperty("typhon.spi.invite.reward");
        inviteLevelLimit = Typhons.getInteger("typhon.spi.invite.levelLimit");

        stampLimit = Typhons.getInteger("typhon.spi.BuyStempMaxCounts");
        stampCost = Typhons.getInteger("typhon.spi.stemp.cost");

        vipExclusiveGifts.addAll(JSONArray.parseArray(
                ComponentUtils.readDataFile("vip_exclusive_gift.json"), VipExclusiveGift.class));

        vipFreeGifts.addAll(JSONArray.parseArray(ComponentUtils.readDataFile("vip_free_gift.json"),
                VipExclusiveGift.class));

        vipActivityGifts.addAll(JSONArray.parseArray(
                ComponentUtils.readDataFile("vip_activity_gift.json"), VipGift.class));

        vipDayGifts.addAll(JSONArray.parseArray(ComponentUtils.readDataFile("vip_day_gift.json"),
                VipGift.class));


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(Typhons.getProperty("typhon.spi.vip.receiveActivity.star")));
            vipActivityStar = calendar.getTimeInMillis();

            calendar.setTime(sdf.parse(Typhons.getProperty("typhon.spi.vip.receiveActivity.end")));
            vipActivityEnd = calendar.getTimeInMillis();

            calendar.setTime(sdf.parse(Typhons.getProperty("typhon.spi.vip.receiveDay.star")));
            vipDayStar = calendar.getTimeInMillis();

            calendar.setTime(sdf.parse(Typhons.getProperty("typhon.spi.vip.receiveDay.end")));
            vipDayEnd = calendar.getTimeInMillis();

            calendar.setTime(sdf.parse(Typhons.getProperty("typhon.spi.stemp.start")));
            stampStar = calendar.getTimeInMillis();

            calendar.setTime(sdf.parse(Typhons.getProperty("typhon.spi.stemp.end")));
            stampEnd = calendar.getTimeInMillis();

        } catch (ParseException e) {
            throw new ComponentException("VIP: ReceiveActivityStarTime["
                    + Typhons.getProperty("typhon.spi.vip.receiveActivity.star") + "]", e);
        }

        array = JSONArray.parseArray(ComponentUtils.readDataFile("random_boxs.json"));
        JSONArray arrayNew;
        for (int i = 0; i < array.size(); i++) {
            arrayNew = array.getJSONObject(i).getJSONArray("items");
            int index = 0;
            List<RandomBoxItem> items = new ArrayList<>();
            for (int j = 0; j < arrayNew.size(); j++) {
                RandomBoxItem item = arrayNew.getObject(j, RandomBoxItem.class);
                item.setId(itemProvider.getItem(arrayNew.getJSONObject(j).getString("#item.id")));
                item.setRandBegain(index);
                index += item.getPro();
                item.setRandEnd(index);
                items.add(item);
            }
            randomBoxs.put(array.getJSONObject(i).getString("id"), items);
        }
        stamps.addAll(JSONArray.parseArray(ComponentUtils.readDataFile("stamp.json"),
                StampItems.class));
    }

    @Override
    protected void doReload() {

    }

    public void cashCow(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Vip vip = roleProvider.getVip(normal.getVipLevel());
        // 摇钱树次数
        if (normal.getCashCowCounts() >= vip.privileged.diamond_exchange_gold_counts) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("VipLevel is not enough");
            return;
        }
        int counts = normal.getCashCowCounts();
        if (normal.getCashCowCounts() >= cashCowCrits.size()) {
            counts = cashCowCrits.size() - 1;
        }

        CashCowCrit object = cashCowCrits.get(counts);

        int extGold = 0;
        for (int i = 0; i < counts; i++) {
            extGold += object.getGold();
        }

        // 暴击倍数
        int factor = 0;
        // 判断是不是暴击
        int criteRandom = RANDOM.nextInt(100);
        int copper = inital + normal.getLevel() * levelInital + extGold;
        if (criteRandom < object.getCritprob()) {
            int randomEnd = cashCowlists.get(cashCowlists.size() - 1).getRandEnd();
            int random = RANDOM.nextInt(randomEnd);
            for (CashCow cashCow : cashCowlists) {
                if (random >= cashCow.getRandBegin() && random < cashCow.getRandEnd()) {
                    factor = cashCow.getFactor();
                    copper = factor * copper;
                    break;
                }
            }
        }

        // 摇一摇每日任务
        counts = normal.getDailyTask().getTaskTree();
        if (counts >= 0) {
            normal.getDailyTask().setTaskTree(counts + 1);
        }

        // 扣钻石
        JSONObject obj = new JSONObject();
        obj.put("place", "CashCowTree");
        obj.put("buyCounts", counts);
        SessionUtils.decrementDiamond(object.getCost(), obj.toString());

        SessionUtils.incrementCopper(copper);
        normal.setCashCowCounts(normal.getCashCowCounts() + 1);

        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(factor);
        player.getSession().write(result);
    }

    /**
     * 新手登录七日礼包
     */
    public void accessLoginGift(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = normal.getLoginGift().size();
        String id = null;

        boolean bool = (index == 0);
        if (!bool) {
            Calendar nowCurcal = Calendar.getInstance();
            Calendar curCal = Calendar.getInstance();
            curCal.setTimeInMillis(normal.getLoginGift().get(index - 1));
            if (curCal.get(Calendar.DAY_OF_YEAR) != nowCurcal.get(Calendar.DAY_OF_YEAR)) {
                bool = true;
            }
        }

        if (bool) {
            LoginGift login = loginGift.get(index);
            for (ItemsObject object : login.getGiftList()) {
                if ("w036".equals(object.getItemDobj().getId())) {
                    JSONObject obj = new JSONObject();
                    obj.put("place", "SevenDaysLoginGift");
                    obj.put("loginDays", index);
                    SessionUtils.incrementDiamond(object.getCount(), object.toString());
                } else {
                    BagUtils.intoItem(object.getItemDobj(), object.getCount());
                }
            }
            id = login.getId();
            normal.addLoginGift(System.currentTimeMillis());
        }

        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(id);
        player.getSession().write(result);
    }

    /**
     * 
     * @param packet
     */
    public void drawCDKEY(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Session session = player.getSession();
        User user = (User) session.getAttribute(SessionConstants.ATTR_USER);

        String cdk = String.valueOf(packet.getVal());
        CDKeyObject object = userRepository.findByCDKEY(cdk);
        Invisible invisible = player.getInvisible();

        if (invisible.getDrawCDKEYs().containsKey(object.getBatch())) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.batch_error);
            error.setText("CDKEY batch is used");
            session.write(error);
            return;
        }

        if (object == null || object.getState() == 1) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.cdkey_error);
            error.setText("CDKEY not found/used");
            session.write(error);
            return;
        }

        long time = System.currentTimeMillis() / 1000;
        if (object.getBeginTime() > time || object.getEndTime() < time) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.time_platform_over);
            error.setText("CDKEY expired");
            session.write(error);
            return;
        }

        if (object.getPlatform() != Platform.none && object.getPlatform() != user.getPlatform()) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.time_platform_over);
            error.setText("platform not matching");
            session.write(error);
            return;
        }

        BagUtils.intoItem(itemProvider.getItem(object.getItemId()));
        invisible.addDrawCDKEYs(object.getBatch(), cdk);

        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(object.getItemId());
        session.write(result);

        userRepository.updateCDKey(cdk);
    }

    public void atlasHeros(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Session session = player.getSession();
        boolean bool = false;
        int id = (int) packet.getVal() - 1;

        if (normal.getAtlasHeros().contains(id)) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("atlasHeros is receive ");
            session.write(error);
            return;
        }

        AtlasHeros atlas = atlasHeros.get(id);
        Bag bag = player.getHeroBag();
        int count = 0;
        if (atlas.getType() == Type.a) {
            for (int i = 0; i < atlas.getRequirements().length; i++) {
                for (int j = 0; j < bag.getNodes().size(); j++) {
                    if (bag.getNodes().get(j).getItem().getId().equals(atlas.getRequirements()[i])) {
                        count++;
                        break;
                    }
                }
            }
            bool = count < atlas.getRequirements().length;
        } else {
            for (int i = 0; i < bag.getNodes().size(); i++) {
                HeroItem heroitem = bag.getNodes().get(i).getItem();
                if (atlas.getHeroArea() != null && heroitem.getArea().equals(atlas.getHeroArea())) {
                    count++;
                } else if (atlas.getHeroRace() != null
                        && heroitem.getRace().equals(atlas.getHeroRace())) {
                    count++;
                } else if (atlas.getGender() != null
                        && heroitem.getGender().equals(atlas.getGender())) {
                    count++;
                }
            }
            bool = count < atlas.getHeroCount();
        }

        if (bool) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("atlasHeros is not complete ");
            session.write(error);
            return;
        }
        normal.addAtlasHeros(id);
        SessionUtils.incrementCopper(atlas.getCopper());
        BagUtils.intoItem(itemProvider.getItem(atlas.getWid()), atlas.getCount());
        session.write(packet);
    }

    public void receiveMonthCard(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Session session = player.getSession();

        MonthCardObject monthCardObject = normal.getMonthCardObject();

        Calendar expiredCal = Calendar.getInstance();
        expiredCal.setTimeInMillis(monthCardObject.getExpiredTime());
        roleProvider.clearCalendar(expiredCal);

        // 当前服务器时间
        Calendar currentCal = Calendar.getInstance();
        roleProvider.clearCalendar(currentCal);

        if (monthCardObject.getExpiredTime() == 0
                || currentCal.getTimeInMillis() >= expiredCal.getTimeInMillis()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("MonthCard is expired");
            session.write(error);
            return;
        }

        Calendar receiveCal = Calendar.getInstance();
        receiveCal.setTimeInMillis(monthCardObject.getReceiveTime());
        roleProvider.clearCalendar(currentCal);
        // 领取时间
        if (currentCal.getTimeInMillis() == receiveCal.getTimeInMillis()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Today's receive complete");
            session.write(error);
            return;
        }

        normal.getMonthCardObject().setReceiveTime(currentCal.getTimeInMillis());
        JSONObject result = new JSONObject();
        result.put("place", "MonthCard");
        SessionUtils.incrementDiamond(monthCardDiamond, result.toString());
        session.write(packet);
    }

    public void upgradeGift(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = (int) packet.getVal() - 1;
        UpgradeGift upgradeGift = upgradeGifts.get(index);

        // 等级不够
        if (normal.getUpgradeGifts() == null || upgradeGift.getLevelLimit() > normal.getLevel()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.level_limit);
            error.setText("The levelLimit");
            player.getSession().write(error);
            return;
        }

        // 已经领取
        if (normal.getUpgradeGifts().get(index).getState() != 0) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("The upgradeGift is already receiving");
            player.getSession().write(error);
            return;
        }
        // 冲级奖励活动时间
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(normal.player().getRole().getCreationTime());
        roleProvider.clearCalendar(cal);
        cal.add(Calendar.DATE, 7);
        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.time_over);
            error.setText("The upgradeGift timeout");
            player.getSession().write(error);
            return;
        }
        for (ItemsObject object : upgradeGift.getItems()) {
            BagUtils.intoItem(object.getItemDobj(), object.getCount());
        }

        normal.getUpgradeGifts().get(index).setState(1);
        player.getSession().write(Packet.createResult(packet));
    }

    public void firstVipRecharge(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        // 是否首充
        if (normal.getVipRechargingFlags().size() == 0) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.not_allowed);
            error.setText("Not recharge");
            player.getSession().write(error);
            return;
        }
        // 是否已领取
        if (normal.isFirstRecharge()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("The FristvipChargetGift is already receiving");
            player.getSession().write(error);
            return;
        }

        for (ItemsObject object : firstRecharg) {
            BagUtils.intoItem(object.getItemDobj(), object.getCount());
        }

        normal.setFirstRecharge(true);
        player.getSession().write(Packet.createResult(packet));
    }

    public void buyGrowthFund(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Vip vip = roleProvider.getVip(normal.getVipLevel());

        if (!vip.privileged.buy_growth_fun) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.size_limit);
            error.setText("GrowthFund VipLevel limit");
            player.getSession().write(error);
            return;
        }
        if (normal.getGrowthFund().size() != 0) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.receive_over);
            error.setText("GrowthFund is already buy");
            player.getSession().write(error);
            return;
        }
        JSONObject obj = new JSONObject();
        obj.put("place", "BuyGrowthFund");
        SessionUtils.decrementDiamond(growthFundMoney, obj.toString());
        List<RecordObject> growthes = new ArrayList<>();

        for (int i = 0; i < growthFunds.size(); i++) {
            growthes.add(new RecordObject(growthFunds.get(i).getLevel(), 0));
        }
        normal.setGrowthFund(growthes);
        player.getSession().write(Packet.createResult(packet));
    }

    public void growthFound(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = (int) packet.getVal() - 1;

        List<RecordObject> growthFund = normal.getGrowthFund();
        if (growthFund.size() == 0) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("GrowthFund is not have");
            player.getSession().write(error);
            return;
        }

        if (growthFund.get(index).getState() != 0
                || growthFunds.get(index).getLevel() > normal.getLevel()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("The GrowthFund is already receiving/levelLimit");
            player.getSession().write(error);
            return;
        }

        growthFund.get(index).setState(1);
        JSONObject obj = new JSONObject();
        obj.put("place", "GrowthFund");
        SessionUtils.incrementDiamond(growthFunds.get(index).getReward(), obj.toString());
        player.getSession().write(Packet.createResult(packet));
    }

    public void exchangeInvite(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        String str = (String) packet.getVal();
        int uid = Integer.valueOf(str.substring(1, (str.length() - 2)));
        Role role = roleRepository.get(uid);
        if (role == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.no_exist);
            error.setText("The Invite is not exist");
            player.getSession().write(error);
            return;
        }
        if (player.getRole().getRid() == uid) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.receive_over);
            error.setText("Can't invite yourself");
            player.getSession().write(error);
            return;
        }
        if (normal.isInvite()) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.receive_over);
            error.setText("The Invite is already receiving");
            player.getSession().write(error);
            return;
        }
        if (normal.getLevel() > inviteLevelLimit) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.size_limit);
            error.setText("The Invite RoleLevel is limit");
            player.getSession().write(error);
            return;
        }
        player.getInvisible().setInviteUid(uid);
        normal.setInvite(true);
        BagUtils.intoItem(itemProvider.getItem(inviteReward));
        player.getSession().write(Packet.createResult(packet));
    }

    public void receiveInviteReward(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = (int) packet.getVal() - 1;
        int limit = inviteRewards.get(index).getLimit();

        if (player.getInvisible().getInvite().size() < limit) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.size_limit);
            error.setText("The Invite is Limit");
            player.getSession().write(error);
            return;
        }

        for (RecordObject reward : normal.getInviteRewards()) {
            if (reward.getCount() == limit) {
                PacketError error =
                        PacketError.createResult(packet, PacketError.Condition.receive_over);
                error.setText("The Invite is already receiving");
                player.getSession().write(error);
                return;
            }
        }
        BagUtils.intoItem(inviteRewards.get(index).getItemId());
        normal.addInviteReward(new RecordObject(limit, 1));
        player.getSession().write(Packet.createResult(packet));
    }

    public void receiveInviteUsers(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        if (normal.getInviteNumber() == 0) {
            StringBuffer sb = new StringBuffer("1");
            sb = sb.append(player.getRole().getRid());
            sb = sb.append("23");
            normal.setInviteNumber(Integer.valueOf(sb.toString()));
        }

        List<Integer> uids = player.getInvisible().getInvite();
        List<InviteUsers> inviteUsers = new ArrayList<>();
        Session otherSession = null;

        for (Integer uid : uids) {
            otherSession = sessionManager.getSession(uid);
            InviteUsers inviteUser = new InviteUsers();
            inviteUser.setUid(uid);
            if (otherSession != null) {
                Player bePlayer = SessionUtils.getPlayer(otherSession);
                Normal beNormal = bePlayer.getNormal();
                inviteUser.setUserName(bePlayer.getRole().getName());
                inviteUser.setAvatar(beNormal.getAvatar());
                inviteUser.setAvatarBorder(beNormal.getAvatarBorder());
                inviteUser.setLevel(beNormal.getLevel());
            } else {
                VacantData vacantData = roleProvider.loadVacantData(uid);
                inviteUser.setUserName(vacantData.getName());
                inviteUser.setAvatar(vacantData.getAvatar());
                inviteUser.setAvatarBorder(vacantData.getAvatarBorder());
                inviteUser.setLevel(vacantData.getLevel());
            }
            inviteUsers.add(inviteUser);
        }

        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(inviteUsers);
        player.getSession().write(result);
    }

    public int inviteLevelLimit() {
        return inviteLevelLimit;
    }

    public void luckeyDraw(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        LuckeyDraw luckeyDraw = null;
        if (!normal.isLuckeyDrawFree()) {
            normal.setLuckeyDrawFree(true);

        } else if (normal.getLuckeyDrawCounts() > 0) {
            normal.setLuckeyDrawCounts(normal.getLuckeyDrawCounts() - 1);
        } else {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.receive_over);
            error.setText("LuckeyDraw isn't have count");
            player.getSession().write(error);
            return;
        }
        String id = null;
        int random = RANDOM.nextInt(luckeyDraws.get(luckeyDraws.size() - 1).getRandEnd());
        for (int i = 0; i < luckeyDraws.size(); i++) {
            luckeyDraw = luckeyDraws.get(i);
            if (random >= luckeyDraw.getRandBegin() && random < luckeyDraw.getRandEnd()) {
                BagUtils.intoItem(luckeyDraw.getItem(), luckeyDraw.getCount());
                id = luckeyDraw.getId();
                break;
            }
        }
        if (id.equals("t01") || id.equals("t02") || id.equals("t03")) {
            PacketNotice notice = new PacketNotice();
            notice.setNtype(PacketNotice.TOP_UP_LUCKEY_DRAW);
            notice.setName(player.getRole().getName());
            notice.setAnnex1(id);
            noticeBoardProvider.announce(notice);
        }
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(id);
        player.getSession().write(result);
    }

    public void vipReceive(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = (int) packet.getVal();
        VipExclusiveGift gift = vipExclusiveGifts.get(index);
        if (normal.getVipReceive().contains(index)) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.receive_over);
            error.setText("The VipExclusiveGift is already receiving");
            player.getSession().write(error);
            return;
        }
        if (player.getNormal().getVipLevel() < gift.getVip()) {

            PacketError error = PacketError.createResult(packet, PacketError.Condition.level_limit);
            error.setText("The VipLevelLimit");
            player.getSession().write(error);
            return;
        } else {
            JSONObject obj = new JSONObject();
            obj.put("place", "VipExclusiveGift");
            SessionUtils.decrementDiamond(gift.getNowPrice(), obj.toString());
            normal.addVipReceive(index);
            BagUtils.intoItem(itemProvider.getItem(gift.getItem()), gift.getCount());
        }
        player.getSession().write(Packet.createResult(packet));
    }

    public void vipFreeGift(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = (int) packet.getVal();
        VipExclusiveGift gift = vipFreeGifts.get(index);
        if (normal.getVipFreeGift() >= 0) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.receive_over);
            error.setText("The VipExclusiveGift is already receiving");
            player.getSession().write(error);
            return;
        }
        if (player.getNormal().getVipLevel() != gift.getVip()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.level_limit);
            error.setText("The VipLevelLimit");
            player.getSession().write(error);
            return;
        } else {
            normal.setVipFreeGift(index);
            BagUtils.intoItem(itemProvider.getItem(gift.getItem()), gift.getCount());
        }
        player.getSession().write(Packet.createResult(packet));
    }

    public void vipReceiveDay(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        if (System.currentTimeMillis() < vipDayStar || System.currentTimeMillis() > vipDayEnd) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.time_over);
            error.setText("The VipDayGift time is over");
            player.getSession().write(error);
        }

        vipReceive1(normal.getVipReceiveDay(), packet, player, vipDayGifts);
        normal.addVipReceiveDay((int) packet.getVal());
        player.getSession().write(Packet.createResult(packet));
    }

    public void vipReceiveActivity(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        if (System.currentTimeMillis() < vipActivityStar
                || System.currentTimeMillis() > vipActivityEnd) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.time_over);
            error.setText("The VipActivityGift time is over");
            player.getSession().write(error);
        }

        vipReceive1(normal.getVipReceiveActivity(), packet, player, vipActivityGifts);
        normal.addVipReceiveActivity((int) packet.getVal());
        player.getSession().write(Packet.createResult(packet));
    }

    private void vipReceive1(List<Integer> receives, SingleValue packet, Player player,
            List<VipGift> receiveGifts) {
        int index = (int) packet.getVal();
        if (receives.contains(index)) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.receive_over);
            error.setText("The VipExclusiveGift is already receiving");
            player.getSession().write(error);
            return;
        }
        for (ExclusiveBuildInformation information : receiveGifts.get(index).getItems()) {
            BagUtils.intoItem(itemProvider.getItem(information.getName()), information.getNumber());
        }
    }

    public void openBox(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        String id = (String) packet.getVal();
        List<RandomBoxItem> items = randomBoxs.get(id);
        if (!player.getBag().decrementTotal(id, 1)) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("The Box's id is Error");
            player.getSession().write(error);
            return;
        }
        if (items.isEmpty()) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.item_not_found);
            error.setText("The Box's id is Error");
            player.getSession().write(error);
            return;
        }

        int random = RANDOM.nextInt(items.get(items.size() - 1).getRandEnd());
        JSONObject obj = new JSONObject();
        for (RandomBoxItem item : items) {
            if (random >= item.getRandBegain() && random < item.getRandEnd()) {
                BagUtils.intoItem(item.getId(), item.getCount());
                obj.put("id", item.getId().getId());
                obj.put("count", item.getCount());
                break;
            }
        }
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(obj);
        player.getSession().write(result);
    }

    public void stampExchange(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        int index = (int) packet.getVal();
        StampItems stamp = stamps.get(index);
        if (System.currentTimeMillis() < stampStar || System.currentTimeMillis() > stampEnd) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.time_over);
            error.setText("The VipActivityGift time is over");
            player.getSession().write(error);
        }
        if (!player.getBag().decrementTotal(stamp.getCostId(), stamp.getCostCounts())) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.no_exist);
            error.setText("The Stamp time is over");
            player.getSession().write(error);
            return;
        } else {
            BagUtils.intoItem(itemProvider.getItem(stamp.getItemId()), stamp.getItemCounts());
        }
        player.getSession().write(Packet.createResult(packet));
    }

    public void stampBuy(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = (int) packet.getVal();

        if (System.currentTimeMillis() < stampStar || System.currentTimeMillis() > stampEnd) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.time_over);
            error.setText("The Stamp time is over");
            player.getSession().write(error);
        }
        if (index + normal.getStampBuyLimit() > stampLimit) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.level_limit);
            error.setText("The StampBuy Limit");
            player.getSession().write(error);
            return;
        } else {
            JSONObject obj = new JSONObject();
            obj.put("place", "StampCost");
            SessionUtils.decrementDiamond((stampCost * index), obj.toString());
            player.getBag().intoItem(itemProvider.getItem(stamps.get(0).getCostId()), index);
            normal.setStampBuyLimit(normal.getStampBuyLimit() + index);
        }
        player.getSession().write(Packet.createResult(packet));
    }


    public long getVipActivityStar() {
        return vipActivityStar;
    }

    public long getVipActivityEnd() {
        return vipActivityEnd;
    }

    public long getVipDayStar() {
        return vipDayStar;
    }

    public long getVipDayEnd() {
        return vipDayEnd;
    }
}
