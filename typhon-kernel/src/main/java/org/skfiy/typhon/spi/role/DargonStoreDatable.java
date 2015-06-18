package org.skfiy.typhon.spi.role;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.spi.dargon.DargonStoreProvider;

public class DargonStoreDatable extends StoreDatable {

    private static final String SESSION_SRT_KEY = "__Hydrovalve__store__refresh__task";
    @Inject
    private DargonStoreProvider hyStoreProvider;

    @Override
    public void initialize(Player player) {
        HyStoreRefreshTask psrt = new HyStoreRefreshTask(player);
        psrt.run();
        SessionContext.getSession().setAttribute(SESSION_SRT_KEY, psrt);
    }

    @Override
    public void serialize(Player player, RoleData roleData) {
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {
        initialize(player);
    }

    public class HyStoreRefreshTask implements Runnable {

        private final Player player;

        HyStoreRefreshTask(Player player) {
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
            //上次自动刷新时间
            Calendar lastCal = Calendar.getInstance();
            lastCal.setTimeInMillis(player.getNormal().getLastHyRestoreTime());

            //初始帐号
            if (lastCal.getTimeInMillis() == 0) {
                isRefresh = true;
            } else {
                //下次刷新时间点
                nextRefreshTime0 = nextRefreshTime(getRefreshTimes(), lastCal.getTimeInMillis());
//              isRefresh = !(lastCal.get(Calendar.DAY_OF_YEAR) == curCal.get(Calendar.DAY_OF_YEAR));
                if (nextRefreshTime0.getTimeInMillis() <= curCal.getTimeInMillis()) {
                    isRefresh = true;
                } else {
                    cdTime = nextRefreshTime0.getTimeInMillis() - curCal.getTimeInMillis();
                }
            }

            if (isRefresh) {
                hyStoreProvider.refreshCommodity(player);
                nextRefreshTime0 = nextRefreshTime(getRefreshTimes(), curCal.getTimeInMillis());
                cdTime = nextRefreshTime0.getTimeInMillis() - curCal.getTimeInMillis();
                player.getNormal().setLastHyRestoreTime(System.currentTimeMillis());
            }
            scheduler.schedule(new HyStoreRefreshTask(player), cdTime, TimeUnit.MILLISECONDS);
        }

        private List<Integer> getRefreshTimes() {
            return hyStoreProvider.getRefreshTimes();
        }
    }

}
