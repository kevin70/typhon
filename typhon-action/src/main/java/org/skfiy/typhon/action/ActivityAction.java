package org.skfiy.typhon.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.packet.CaravanPacket;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.activity.ActivityProvider;
import org.skfiy.typhon.spi.caravan.CaravanProvider;

@Singleton
public class ActivityAction {

    @Inject
    private ActivityProvider activityProvider;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private CaravanProvider caravanProvider;

    @Action(Namespaces.DIAMOND_EXCHANGE_GOLD)
    public void cashCow(SingleValue packet) {
        activityProvider.cashCow(packet);
    }

    @Action(Namespaces.AID_RECEIVE_VIGOR)
    public void aidReceive(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int count = 0;
        int oldCount = 0;
        for (RecordObject object : normal.getAidReceiveCounts()) {
            if (object.getState() == 1) {
                oldCount++;
            }
        }

        for (RecordObject object : normal.getAidReceiveCounts()) {
            if (oldCount + count >= 10) {
                break;
            }
            if (object.getState() == 0) {
                object.setState(1);
                count++;
            }
        }
        normal.setVigor(normal.getVigor() + count
                * Typhons.getInteger("typhon.spi.DailyVigorFromFriend.PerNumber"));
        player.getSession().write(Packet.createResult(packet));
    }

    @Action(Namespaces.ACCESS_LOGIN_GIFT)
    public void accessLoginGift(SingleValue packet) {
        activityProvider.accessLoginGift(packet);
    }

    @Action(Namespaces.CDKEY_CASH_GIFT)
    public void drawCDKEY(SingleValue packet) {
        activityProvider.drawCDKEY(packet);
    }

    @Action(Namespaces.CHECK_ONLINE)
    public void checkOnline(SingleValue packet) {
        Session session = sessionManager.getSession((int) packet.getVal());
        boolean bool = false;
        if (session != null) {
            bool = true;
        }
        SingleValue result = new SingleValue();
        Packet.assignResult(packet, result);
        result.setVal(bool);
        SessionUtils.getPlayer().getSession().write(result);

    }

    @Action(Namespaces.RECEIVE_ATLAS_REWARD)
    public void atlasHeros(SingleValue packet) {
        activityProvider.atlasHeros(packet);
    }

    @Action(Namespaces.RECEIVE_MONTH_CARD)
    public void receiveMonthCard(SingleValue packet) {
        activityProvider.receiveMonthCard(packet);
    }

    @Action(Namespaces.RECEIVE_UPGRADE_GIFT)
    public void upgradeGift(SingleValue packet) {
        activityProvider.upgradeGift(packet);
    }

    @Action(Namespaces.FIRST_VIPCHARFGE_GIFT)
    public void firstVipRecharge(SingleValue packet) {
        activityProvider.firstVipRecharge(packet);
    }

    @Action(Namespaces.GROWTH_FUND)
    public void growthFound(SingleValue packet) {
        activityProvider.growthFound(packet);
    }

    @Action(Namespaces.BUY_GROWTH_FUND)
    public void buyGrowthFound(SingleValue packet) {
        activityProvider.buyGrowthFund(packet);
    }

    @Action(Namespaces.EXCHANGE_INVITATION_CODE)
    public void exchangeInvite(SingleValue packet) {
        activityProvider.exchangeInvite(packet);
    }

    @Action(Namespaces.RECEIVE_INVITATION_REWARDE)
    public void receiveInvite(SingleValue packet) {
        activityProvider.receiveInviteReward(packet);
    }

    @Action(Namespaces.RECEIVE_INVITATION_USERS)
    public void receiveInviteUsers(SingleValue packet) {
        activityProvider.receiveInviteUsers(packet);
    }

    @Action(Namespaces.TOPUP_LUCKEY_DRAW)
    public void lucketyDraw(SingleValue packet) {
        activityProvider.luckeyDraw(packet);
    }

    @Action(Namespaces.CARAVAN_REFRESH)
    public void caravanRefresh(SingleValue packet) {
        caravanProvider.refresh(packet);
    }

    @Action(Namespaces.CARAVAN_WAYGOING)
    public void caravanGoWaying(CaravanPacket packet) {
        caravanProvider.wayGoing(packet);
    }

    @Action(Namespaces.CARAVAN_RECALL)
    public void caravanRecall(SingleValue packet) {
        caravanProvider.recallCaravan(packet);
    }

    @Action(Namespaces.VIP_RECEIVE)
    public void vipReceive(SingleValue packet) {
        activityProvider.vipReceive(packet);
    }

    @Action(Namespaces.VIP_RECEIVE_ACTIVITY)
    public void vipReceiveActivity(SingleValue packet) {
        activityProvider.vipReceiveActivity(packet);
    }

    @Action(Namespaces.VIP_RECEIVE_DAY)
    public void vipReceiveDay(SingleValue packet) {
        activityProvider.vipReceiveDay(packet);
    }

    @Action(Namespaces.OPEN_BOX)
    public void openBox(SingleValue packet) {
        activityProvider.openBox(packet);
    }

    @Action(Namespaces.VIP_FREE_GIFT)
    public void vipFreeGift(SingleValue packet) {
        activityProvider.vipFreeGift(packet);
    }

    @Action(Namespaces.STAMP_EXCHANGE)
    public void stampExchange(SingleValue packet) {
        activityProvider.stampExchange(packet);
    }

    @Action(Namespaces.STAMP_BUY)
    public void stampBuy(SingleValue packet) {
        activityProvider.stampBuy(packet);
    }

    @Action(Namespaces.INTEGRAL_REFRESH)
    public void integralRefresh(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        player.getNormal().setIntegral(true);
        player.getSession().write(Packet.createResult(packet));
    }
}
