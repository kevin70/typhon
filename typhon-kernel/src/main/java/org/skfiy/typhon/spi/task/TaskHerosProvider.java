package org.skfiy.typhon.spi.task;

import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.util.ComponentUtils;

public class TaskHerosProvider extends AbstractTaskProvider {

    @Override
    protected String loadTaskTable() {
        return ComponentUtils.readDataFile("task_list_heros.json");
    }

    @Override
    protected boolean taskEntrance(Player player,SingleValue packet) {
        Normal normal = player.getNormal();
        boolean bool=getRewards(player.getHeroBag().size(), normal.getHeros(),"CollectHerosContinuousTask");
        if(bool)
        {
            normal.setHeros(normal.getHeros()+1);
        }
        return bool;
    }
}
