package org.skfiy.typhon.spi.task;

import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.util.ComponentUtils;

public class TaskLevelProvider extends AbstractTaskProvider{
    
    @Override
    protected String loadTaskTable() {
        return ComponentUtils.readDataFile("task_list_level.json");
    }
    @Override
    protected boolean taskEntrance(Player player,SingleValue packet) {
        Normal normal=player.getNormal();
        boolean bool=getRewards(normal.getLevel(), normal.getRoleLevel(),"LevelContinuousTask");
        if(bool)
        {
            normal.setRoleLevel(normal.getRoleLevel()+1);
        }
        return bool;
    }
}
