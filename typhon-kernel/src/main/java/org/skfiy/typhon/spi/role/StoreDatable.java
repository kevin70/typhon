package org.skfiy.typhon.spi.role;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.spi.store.StoreProvider;
import org.skfiy.util.CustomizableThreadCreator;

public class StoreDatable implements RoleDatable {

    private static final String SESSION_SRT_KEY = "__store__refresh__task";
    protected static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {

        CustomizableThreadCreator threadCreator = new CustomizableThreadCreator("refresh-store-timer-");

        {
            threadCreator.setThreadGroupName("Refresh-Store-ThreadGroup");
            threadCreator.setDaemon(true);
        }

        @Override
        public Thread newThread(Runnable r) {
            return threadCreator.createThread(r);
        }
    });

    @Inject
    private StoreProvider storeProvider;

    @Override
    public void initialize(Player player) {
        StoreRefreshTask srt = new StoreRefreshTask(player);
        srt.run();

        SessionContext.getSession().setAttribute(SESSION_SRT_KEY, srt);
    }

    @Override
    public void serialize(Player player, RoleData roleData) {
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {
        initialize(player);
    }

    private class StoreRefreshTask implements Runnable {

        private final Player player;

        StoreRefreshTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            if (!player.getSession().isAvailable()) {
                return;
            }
            
            boolean isRefresh;
            long cdTime = 0;
            Calendar nextRefreshTime0;
            // 当前时间
            Calendar curCal = Calendar.getInstance();
            //上次自动刷新时间
            Calendar lastCal = Calendar.getInstance();
            lastCal.setTimeInMillis(getLastRefreshStoreTime());

            //初始帐号
            if (lastCal.getTimeInMillis() == 0) {
                isRefresh = true;
            } else {
                //下次刷新时间点
                nextRefreshTime0 = nextRefreshTime(getRefreshTimes(), lastCal.getTimeInMillis());
                isRefresh = !(lastCal.get(Calendar.DAY_OF_YEAR) == curCal.get(Calendar.DAY_OF_YEAR));
                if (nextRefreshTime0.getTimeInMillis() <= curCal.getTimeInMillis()) {
                    isRefresh = true;
                } else {
                    cdTime = nextRefreshTime0.getTimeInMillis() - curCal.getTimeInMillis();
                }
            }

            if (isRefresh) {
                storeProvider.refreshCommodity(player);
                nextRefreshTime0 = nextRefreshTime(getRefreshTimes(), curCal.getTimeInMillis());
                cdTime = nextRefreshTime0.getTimeInMillis() - curCal.getTimeInMillis();
                player.getNormal().setLastRefreshStoreTime(System.currentTimeMillis());
            }

            scheduler.schedule(new StoreRefreshTask(player), cdTime, TimeUnit.MILLISECONDS);
        }

        private List<Integer> getRefreshTimes() {
            return storeProvider.getRefreshTimes();
        }

        private long getLastRefreshStoreTime() {
            return player.getNormal().getLastRefreshStoreTime();
        }
    }

    protected Calendar nextRefreshTime(List<Integer> refreshTimes, long lastTime) {
        Calendar nextCal = Calendar.getInstance();
        nextCal.setTimeInMillis(lastTime);
        clearMSMS(nextCal);

        int cur = nextCal.get(Calendar.HOUR_OF_DAY);
        for (int i = 0; i < refreshTimes.size(); i++) {
            if (cur < refreshTimes.get(i)) {
                nextCal.set(Calendar.HOUR_OF_DAY, refreshTimes.get(i));
                return nextCal;
            }

            if (i >= (refreshTimes.size() - 1)) {
                nextCal.add(Calendar.DATE, 1);
                nextCal.set(Calendar.HOUR_OF_DAY, refreshTimes.get(0));
            }
        }
        return nextCal;
    }

    private void clearMSMS(Calendar c) {
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }
}
