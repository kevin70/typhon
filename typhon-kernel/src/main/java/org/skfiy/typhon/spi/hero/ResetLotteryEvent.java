package org.skfiy.typhon.spi.hero;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.PveProgress;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.spi.Event;
import org.skfiy.typhon.spi.activity.ActivityProvider;
import org.skfiy.typhon.spi.society.Society;
import org.skfiy.typhon.spi.society.SocietyProvider;

public class ResetLotteryEvent implements Event<Player> {

    @Inject
    private SocietyProvider societyProvider;
    @Inject
    private ActivityProvider activityProvider;

    @Override
    public void invoke(Player player) {

        Normal normal = player.getNormal();
        normal.setFreeCount(Typhons.getInteger("typhon.spi.hero.copperLottery.freeCount"));
        // 重置管卡挑战次数
        reset(normal.getHdpveProgresses());
        reset(normal.getHpveProgresses());
        reset(normal.getSpveProgresses());
        // 重置每日任务
        normal.getDailyTask().cleanProperties();
        // 重置摇钱树次数
        normal.setCashCowCounts(0);
        // 签到
        normal.setNowSign(normal.getSigns().size() + 1);
        // 援军赠送体力
        for (int i = normal.getAidReceiveCounts().size() - 1; i >= 0; i--) {
            RecordObject object = normal.getAidReceiveCounts().get(i);
            if (object.getState() == 1) {
                normal.removeAidAccessVigor(object);
            }
        }
        // 玩家每天可以龙脉次数
        normal.setDargonVipCount(0);
        // 如果年份不同签到重置
        Calendar calendar = Calendar.getInstance();
        Calendar calendarSign = Calendar.getInstance();

        calendarSign.setTimeInMillis(player.getRole().getLastLoginedTime());
        if ((calendar.get(Calendar.YEAR) != calendarSign.get(Calendar.YEAR) || calendar
                .get(Calendar.MONTH) != calendarSign.get(Calendar.MONTH))) {
            normal.setNowSign(1);
            normal.getSigns().clear();
            normal.setSigned(0);
        }
        Society society = societyProvider.findBySid(player.getRole().getRid());
        if (society != null) {
            society.getMessages().clear();
        }
        normal.setLuckeyDrawFree(false);

        if (calendar.get(Calendar.DAY_OF_WEEK) == 2) {
            if (Typhons.getBoolean("typhon.spi.first.vipRechargingFlags")) {
                normal.getVipRechargingFlags().clear();
            }
            normal.getVipReceive().clear();
            normal.setVipFreeGift(-1);
        }

        // 活动期间内每天充值领取奖励
        normal.getVipReceiveDay().clear();
        if (!(player.getInvisible().getVipDate() == calendar.get(Calendar.DAY_OF_YEAR))) {
            normal.setVipSavingsDay(0);
            player.getInvisible().setVipDate(calendar.get(Calendar.DAY_OF_YEAR));
        }
        normal.setSocietyRefreshCounts(0);

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTimeInMillis(activityProvider.getVipActivityEnd());

        if (calendar.get(Calendar.DAY_OF_YEAR) >= calendarEnd.get(Calendar.DAY_OF_YEAR)
                || (player.getInvisible().getVipActivityStarTime() != activityProvider
                        .getVipActivityStar() && player.getInvisible().getVipActivityStarTime() != 0)) {
            normal.setVipSavingsActivity(0);
            normal.getVipReceiveActivity().clear();
        }
        // 印花当天购买个数
        normal.setStampBuyLimit(0);
    }

    private void reset(List<PveProgress> list) {
        for (PveProgress pve : list) {
            pve.setCount(0);
            pve.setResetCount(0);
        }
    }
}
