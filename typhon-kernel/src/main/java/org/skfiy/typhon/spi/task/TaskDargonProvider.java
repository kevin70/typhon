package org.skfiy.typhon.spi.task;

import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.util.ComponentUtils;

public class TaskDargonProvider extends AbstractTaskProvider {
    @Override
    protected String loadTaskTable() {
        return ComponentUtils.readDataFile("task_list_dargon.json");
    }

    @Override
    protected boolean taskEntrance(Player player, SingleValue packet) {
        Normal normal = player.getNormal();
        int indext = normal.getTaskDargonMoney().size();
        boolean bool = false;
        RecordObject recordObject = normal.getTaskDargonMoney().get(indext - 1);
        if (recordObject.getState() != 1) {
            bool = getRewards(recordObject.getCount(), indext - 1,"DargonContinuousTask");
            if (bool) {
                recordObject.setState(1);
                normal.setOnceDargonMoney(-1);
            }
        }
        return bool;
    }

    public void update(Normal normal, int number) {
        int index = normal.getTaskDargonMoney().size();
        if (index < 1) {
            normal.addTaskDargonMoney(new RecordObject(number, 0));
        } else {
            RecordObject beTask = normal.getTaskDargonMoney().get(index - 1);
            if (beTask.getState() != 0 && index < taskObjects().size()) {
                normal.addTaskDargonMoney(new RecordObject(number, 0));
            } else if (number > beTask.getCount()) {
                beTask.setCount(number);
            } else {
                return;
            }
        }
    }
}
