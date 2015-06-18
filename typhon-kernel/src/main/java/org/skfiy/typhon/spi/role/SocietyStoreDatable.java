package org.skfiy.typhon.spi.role;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.spi.store.SocietyStoreProvider;

public class SocietyStoreDatable extends StoreDatable {
    private static final String SESSION_SRT_KEY = "__society__store__refresh";

    @Inject
    private SocietyStoreProvider societyStoreProvider;

    @Override
    public void initialize(Player player) {
        societyStoreRefreshTask psrt = new societyStoreRefreshTask(player);
        psrt.run();

        SessionContext.getSession().setAttribute(SESSION_SRT_KEY, psrt);
    }

    @Override
    public void serialize(Player player, RoleData roleData) {}

    @Override
    public void deserialize(RoleData roleData, Player player) {
        initialize(player);
    }

    private class societyStoreRefreshTask implements Runnable {

        private final Player player;

        societyStoreRefreshTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            if (!player.getSession().isAvailable()) {
                return;
            }

            boolean isRefresh = false;
            long cdTime = 0;
            Calendar nextRefreshTime0;
            // 当前时间
            Calendar curCal = Calendar.getInstance();
            // 上次自动刷新时间
            Calendar lastCal = Calendar.getInstance();
            lastCal.setTimeInMillis(player.getNormal().getLastSocietyRestoreTime());

            // 初始帐号
            if (lastCal.getTimeInMillis() == 0) {
                isRefresh = true;
            } else {
                // 下次刷新时间点
                nextRefreshTime0 = nextRefreshTime(getRefreshTimes(), lastCal.getTimeInMillis());
                // isRefresh = !(lastCal.get(Calendar.DAY_OF_YEAR) ==
                // curCal.get(Calendar.DAY_OF_YEAR));
                if (nextRefreshTime0.getTimeInMillis() <= curCal.getTimeInMillis()) {
                    isRefresh = true;
                } else {
                    cdTime = nextRefreshTime0.getTimeInMillis() - curCal.getTimeInMillis();
                }
            }

            if (isRefresh) {
                societyStoreProvider.refreshCommodity(player);
                nextRefreshTime0 = nextRefreshTime(getRefreshTimes(), curCal.getTimeInMillis());
                cdTime = nextRefreshTime0.getTimeInMillis() - curCal.getTimeInMillis();
                player.getNormal().setLastSocietyRestoreTime(System.currentTimeMillis());
            }

            scheduler.schedule(new societyStoreRefreshTask(player), cdTime, TimeUnit.MILLISECONDS);
        }

        private List<Integer> getRefreshTimes() {
            return societyStoreProvider.getRefreshTimes();
        }
    }
}
