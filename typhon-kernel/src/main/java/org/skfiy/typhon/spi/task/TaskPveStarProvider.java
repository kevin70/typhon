package org.skfiy.typhon.spi.task;

import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.util.ComponentUtils;

public class TaskPveStarProvider extends AbstractTaskProvider {
    
    @Override
    protected String loadTaskTable() {
        return ComponentUtils.readDataFile("task_list_star.json");
    }

    @Override
    protected boolean taskEntrance(Player player, SingleValue packet) {
        Normal normal = player.getNormal();
        boolean bool =
                getRewards(normal.getPveStarCounts(), normal.getPveStar(), "PveStarsContinuousTask");
        if (bool) {
            normal.setPveStar(normal.getPveStar() + 1);
        }
        return bool;
    }

}
