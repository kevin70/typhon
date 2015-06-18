package org.skfiy.typhon.spi.role;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.spi.task.TaskProvider;

public class TaskgGetVigorDatable implements RoleDatable {

    private static final String SESSION_SRT_KEY = "__refresh_task_getvigor";
    protected static final Timer REFRESH_STORE_TIMER = new Timer("Refresh-store-timer", true);
    @Inject
    private TaskProvider taskProvider;
    
    @Override
    public void initialize(Player player) {
        TaskGetVigorRefresh srt = new TaskGetVigorRefresh(player);
        srt.run();
        SessionContext.getSession().setAttribute(SESSION_SRT_KEY, srt);
    }

    @Override
    public void serialize(Player player, RoleData roleData) {
        Session session = player.getSession();
        if (session != null) {
            TaskGetVigorRefresh srt = (TaskGetVigorRefresh) session.getAttribute(SESSION_SRT_KEY);
            if (srt != null) {
                srt.cancel();
            }
        }
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {
        initialize(player);
    }



    private class TaskGetVigorRefresh extends TimerTask {

        private final Player player;

        TaskGetVigorRefresh(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            List<Integer> refreshTime = taskProvider.refreshVigorTime();
            Calendar calendar = Calendar.getInstance();
            Calendar beCalendar = Calendar.getInstance();
            for (int i = 0; i < refreshTime.size(); i++) {
                if (calendar.get(Calendar.HOUR_OF_DAY) - 2 >= refreshTime.get(i)) {
                    player.getNormal().getDailyTask().setTaskAccessVigor(i);
                }
            }

        }
    }
}
