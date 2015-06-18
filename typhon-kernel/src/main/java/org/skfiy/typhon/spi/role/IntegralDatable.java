package org.skfiy.typhon.spi.role;

import java.util.Calendar;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.util.CustomizableThreadCreator;

public class IntegralDatable implements RoleDatable {


    private static final String SESSION_SRT_KEY = "__Integral__refresh";
    protected static final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1,
            new ThreadFactory() {

                CustomizableThreadCreator threadCreator = new CustomizableThreadCreator(
                        "refresh-Integral-timer-");

                {
                    threadCreator.setThreadGroupName("Refresh-Integral-ThreadGroup");
                    threadCreator.setDaemon(true);
                }

                @Override
                public Thread newThread(Runnable r) {
                    return threadCreator.createThread(r);
                }
            });

    @Override
    public void initialize(Player player) {
        IntegralRefresh srt = new IntegralRefresh(player);
        srt.run();

        SessionContext.getSession().setAttribute(SESSION_SRT_KEY, srt);
    }

    @Override
    public void serialize(Player player, RoleData roleData) {}

    @Override
    public void deserialize(RoleData roleData, Player player) {
        initialize(player);
    }

    private class IntegralRefresh implements Runnable {

        private final Player player;

        IntegralRefresh(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            if (!player.getSession().isAvailable()) {
                return;
            }
            long CDTime = 0;
            Calendar calendar = Calendar.getInstance();
            clearMSMS(calendar);
            Calendar nowCalendar = Calendar.getInstance();
            if (calendar.getTimeInMillis() < nowCalendar.getTimeInMillis()) {
                CDTime = nowCalendar.getTimeInMillis() - calendar.getTimeInMillis();
            } else {
                player.getNormal().setIntegral(false);
                calendar.add(Calendar.DATE, 1);
                CDTime = calendar.getTimeInMillis() - nowCalendar.getTimeInMillis();
            }
            scheduler.schedule(new IntegralRefresh(player), CDTime, TimeUnit.MILLISECONDS);
        }
    }

    private void clearMSMS(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, Typhons.getInteger("typhon.spi.IntegralRefresh"));
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }
}
