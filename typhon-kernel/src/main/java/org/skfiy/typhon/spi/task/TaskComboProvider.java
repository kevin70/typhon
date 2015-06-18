package org.skfiy.typhon.spi.task;

import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.util.ComponentUtils;

public class TaskComboProvider extends AbstractTaskProvider {

    @Override
    protected String loadTaskTable() {
        return ComponentUtils.readDataFile("task_list_combo.json");
    }
    @Override
    protected boolean taskEntrance(Player player, SingleValue packet) {
        Normal normal = player.getNormal();
        int indext = normal.getTaskPveCombos().size();
        boolean bool = false;
        RecordObject recordObject = normal.getTaskPveCombos().get(indext-1);
        if (recordObject.getState() != 1) {
            bool = getRewards(recordObject.getCount(), indext-1,"ComboContinuousTask");
            if (bool) {
                recordObject.setState(1);
            }
        }
        return bool;
    }

    public void update(SingleValue packet) {
        int number = (int) packet.getVal();
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = normal.getTaskPveCombos().size();
        if (index < 1) {
            normal.addTaskPveCombos(new RecordObject(number, 0));
        } else {
            RecordObject beTask = normal.getTaskPveCombos().get(index - 1);
            if (beTask.getState() != 0 && index < taskObjects().size()) {
                normal.addTaskPveCombos(new RecordObject(number, 0));
            } else if (number > beTask.getCount()) {
                beTask.setCount(number);
            } else {
                return;
            }
        }
    }
}
