package org.skfiy.typhon.spi.task;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.domain.DailyTask;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TaskProvider extends AbstractComponent {

    @Inject
    private RoleProvider roleProvider;
    @Inject
    private ItemProvider itemProvider;
    private Map<Integer, TaskObject> dailyTasks = new HashMap<>();

    @Override
    protected void doInit() {
        JSONObject object = JSON.parseObject(ComponentUtils.readDataFile("task_list_day.json"));
        TaskObject taskDailyObject;
        for (Entry<String, Object> str : object.entrySet()) {
            taskDailyObject = object.getObject(str.getKey(), TaskObject.class);
            JSONObject obj = object.getJSONObject(str.getKey());
            if (obj.getString("#item.id") != null) {
                taskDailyObject.setId(itemProvider.getItem(obj.getString("#item.id")));
            }
            dailyTasks.put(TaskDayEventEnum.valueOf(str.getKey()).getFlag(), taskDailyObject);
        }
    }

    @Override
    protected void doDestroy() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void doReload() {
        // TODO Auto-generated method stub

    }


    /**
     * 日常任务
     * 
     * @param packet
     */

    public void dailyTask(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = SessionUtils.getPlayer().getNormal();
        int index = (int) packet.getVal();
        TaskObject taskDailyObject = dailyTasks.get(index);
        int terms = 0;
        String type=null;
        switch (TaskDayEventEnum.valueOf(index)) {
            case hpve:
                terms = normal.getDailyTask().getTaskHpveCounts();
                type="HpveTask";
                break;
            case hdpve:
                terms = normal.getDailyTask().getTaskHdpveCounts();
                type="TaskHdpveCounts";
                break;
            case spve:
                terms = normal.getDailyTask().getTaskSpveCounts();
                type="TaskSpveCounts";
                break;
            case pvp:
                terms = normal.getDailyTask().getTaskPvpCounts();
                type="TaskPvpCounts";
                break;
            case enchant:
                terms = normal.getDailyTask().getTaskEnchants();
                type="TaskEnchants";
                break;
            case lotteries:
                terms = normal.getDailyTask().getTaskLotteries();
                type="TaskLotteries";
                break;
            case dargon:
                terms = normal.getDailyTask().getTaskDargonCounts();
                type="TaskDargonCounts";
                break;
            case activities:
                terms = normal.getDailyTask().getTaskActivities();
                type="TaskapveProgresses";
                break;
            case tree:
                terms = normal.getDailyTask().getTaskTree();
                type="TaskTree";
                break;
            case troopStreng:
                terms = normal.getDailyTask().getTaskTroopStreng();
                type="TaskTroopStreng";
                break;
            case hardenStreng:
                terms = normal.getDailyTask().getTaskHardenStreng();
                type="TaskHardenStreng";
                break;
            case societyBoss:
                terms = normal.getDailyTask().getTaskSocietyBoss();
                type="societyBoss";
                break;
            case caravan:
                terms = normal.getDailyTask().getTaskCaravan();
                type="societyBoss";
                break;
            default:
                break;
        }
        if (taskHpve(taskDailyObject, normal, terms,type)) {
            reset(TaskDayEventEnum.valueOf(index), normal);
        }
        player.getSession().write(Packet.createResult(packet));
    }

    private boolean taskHpve(TaskObject taskDailyObject, Normal normal, int terms,String type) {
        boolean bool = false;
        if (terms >= taskDailyObject.getTerms()) {
            dayRewards(taskDailyObject, normal,type);
            bool = true;
        }
        return bool;
    }

    public void taskAccessVigor(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        int index = (int) packet.getVal();
        TaskObject taskDailyObject = dailyTasks.get(TaskDayEventEnum.vigor.getFlag());
        if (index == normal.getDailyTask().getTaskAccessVigor()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Have to receive");
            player.getSession().write(error);
            return;
        }
        if (index > taskDailyObject.getTime().length) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("TaskDaily Beyond the time");
            player.getSession().write(error);
            return;
        }
        Calendar curCal = Calendar.getInstance();
        Calendar becurCal = Calendar.getInstance();
        dailyTasks.get(TaskDayEventEnum.vigor);
        becurCal.set(Calendar.HOUR_OF_DAY, taskDailyObject.getTime()[index - 1]);
        becurCal.set(Calendar.MINUTE, 0);
        becurCal.set(Calendar.SECOND, 0);
        long time = curCal.getTimeInMillis() - becurCal.getTimeInMillis();
        if (time >= 0 && time <= taskDailyObject.getTerms() * 60 * 60 * 1000) {
            normal.getDailyTask().setTaskAccessVigor(index);
            dayRewards(taskDailyObject, normal,"taskAccessVigor");
            player.getSession().write(Packet.createResult(packet));
        } else {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("have not to access vigor time");
            player.getSession().write(error);
            return;
        }
    }

    private void reset(TaskDayEventEnum taskDayEventEnum, Normal normal) {
        DailyTask dailyTask = normal.getDailyTask();
        switch (taskDayEventEnum) {
            case hpve:
                dailyTask.setTaskHpveCounts(-1);
                break;
            case hdpve:
                dailyTask.setTaskHdpveCounts(-1);
                break;
            case spve:
                dailyTask.setTaskSpveCounts(-1);
                break;
            case pvp:
                dailyTask.setTaskPvpCounts(-1);
                break;
            case enchant:
                dailyTask.setTaskEnchants(-1);
                break;
            case lotteries:
                dailyTask.setTaskLotteries(-1);
                break;
            case dargon:
                dailyTask.setTaskDargonCounts(-1);
                break;
            case activities:
                dailyTask.setTaskActivities(-1);
                break;
            case tree:
                dailyTask.setTaskTree(-1);
                break;
            case troopStreng:
                dailyTask.setTaskTroopStreng(-1);
                break;
            case hardenStreng:
                dailyTask.setTaskHardenStreng(-1);
                break;
            case societyBoss:
                dailyTask.setTaskSocietyBoss(-1);
                break;
            case caravan:
                dailyTask.setTaskCaravan(-1);
                break;
            default:
                break;
        }
    }

    private void dayRewards(TaskObject taskDailyObject, Normal normal,String type) {

        if (taskDailyObject.getCopper() != 0) {
            SessionUtils.incrementCopper(taskDailyObject.getCopper());
        }
        if (taskDailyObject.getDiamond() != 0) {
            JSONObject object = new JSONObject();
            object.put("place", "DailyTask");
            object.put("TaskType", type);
            SessionUtils.incrementDiamond(taskDailyObject.getDiamond(), object.toString());
        }
        if (taskDailyObject.getExp() != 0) {
            roleProvider.pushExp(normal, taskDailyObject.getExp());
        }
        if (taskDailyObject.getVigor() != 0) {
            normal.setVigor(normal.getVigor() + taskDailyObject.getVigor());
        }
        if (taskDailyObject.getId() != null) {
            BagUtils.intoItem(taskDailyObject.getId(), taskDailyObject.getCount());
        }
    }

    public List<Integer> refreshVigorTime() {
        TaskObject taskDailyObject = dailyTasks.get(TaskDayEventEnum.vigor.getFlag());
        List<Integer> refreshTime = Arrays.asList(taskDailyObject.getTime());
        return refreshTime;
    }
}
