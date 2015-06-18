package org.skfiy.typhon.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.spi.task.TaskComboProvider;
import org.skfiy.typhon.spi.task.TaskProvider;
import org.skfiy.typhon.spi.task.TaskDargonProvider;
import org.skfiy.typhon.spi.task.TaskHerosProvider;
import org.skfiy.typhon.spi.task.TaskLevelProvider;
import org.skfiy.typhon.spi.task.TaskPveProgressProvider;
import org.skfiy.typhon.spi.task.TaskPveStarProvider;
import org.skfiy.typhon.spi.task.TaskPvpProvider;

@Singleton
public class TaskAction {

    @Inject
    private TaskProvider taskProvider;
    @Inject
    private TaskDargonProvider taskDargonProvider;
    @Inject
    private TaskLevelProvider taskLevelProvider;
    @Inject
    private TaskPvpProvider taskPvpProvider;
    @Inject
    private TaskHerosProvider taskHerosProvider;
    @Inject
    private TaskComboProvider taskComboProvider;
    @Inject
    private TaskPveProgressProvider taskPveProgressProvider;
    @Inject
    private TaskPveStarProvider taskPveStarProvider;

    @Action(Namespaces.TASK_DAILY)
    public void taskdaily(SingleValue packet) {
        taskProvider.dailyTask(packet);
    }

    @Action(Namespaces.TASK_ROLELEVEL)
    public void roleLevel(SingleValue packet) {
        taskLevelProvider.taskEntrance(packet);
    }

    @Action(Namespaces.TASK_ACCESSP_VIGOR)
    public void accessVigor(SingleValue packet) {
        taskProvider.taskAccessVigor(packet);
    }

    @Action(Namespaces.TASK_DARGONMONEY)
    public void dargonMoney(SingleValue packet) {
        taskDargonProvider.taskEntrance(packet);
    }

    @Action(Namespaces.TASK_HEROCOUNTS)
    public void heroCounts(SingleValue packet) {
        taskHerosProvider.taskEntrance(packet);
    }

    @Action(Namespaces.TASK_PVPWINCOUNTS)
    public void pvpWinCounts(SingleValue packet) {
        taskPvpProvider.taskEntrance(packet);
    }

    @Action(Namespaces.TASK_WARCOMBO)
    public void warCombos(SingleValue packet) {
        taskComboProvider.taskEntrance(packet);
    }

    @Action(Namespaces.TASK_COMBOVALUER)
    public void comboValue(SingleValue packet) {
        taskComboProvider.update(packet);
    }
    @Action(Namespaces.TASK_PVE)
    public void taskPve(SingleValue packet) {
        taskPveProgressProvider.taskEntrance(packet);
    }

    @Action(Namespaces.TASK_PVESTAR)
    public void taskPveStar(SingleValue packet) {
        taskPveStarProvider.taskEntrance(packet);
    }
}
