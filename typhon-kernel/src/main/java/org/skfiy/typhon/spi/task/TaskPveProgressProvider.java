package org.skfiy.typhon.spi.task;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.RecordObject;
import org.skfiy.typhon.domain.item.TaskPveProgressObject;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.pve.PveWarInfo;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TaskPveProgressProvider extends AbstractComponent {

    private final List<List<TaskObject>> pveProgressTasks = new ArrayList<>();
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private ItemProvider itemProvider;

    protected void doInit() {
        JSONArray array =
                JSONArray.parseArray(ComponentUtils.readDataFile("task_list_pveprogress.json"));
        for (int i = 0; i < array.size(); i++) {
            JSONArray arr = array.getJSONArray(i);
            List<TaskObject> tasks = new ArrayList<>();
            for (int j = 0; j < arr.size(); j++) {
                TaskObject obj = JSON.toJavaObject(arr.getJSONObject(j), TaskObject.class);
                if (arr.getJSONObject(j).getString("#item.id") != null) {
                    obj.setId(itemProvider.getItem(arr.getJSONObject(j).getString("#item.id")));
                }
                tasks.add(obj);
            }
            pveProgressTasks.add(tasks);
        }
    }

    @Override
    protected void doDestroy() {}

    @Override
    protected void doReload() {}

    public void taskEntrance(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();

        int taskIndex = (int) packet.getVal();
        TaskPveProgressObject object = new TaskPveProgressObject();
        TaskPveProgressObject taskPve = new TaskPveProgressObject();
        for (int i = 0; i < normal.getPveProgressCounts().size(); i++) {
            taskPve = normal.getPveProgressCounts().get(i);
            if (taskIndex == taskPve.getTid()) {
                object = taskPve;
                break;
            }
        }

        int index = object.getPveGrowth().size();
        if (object.getPveGrowth().get(index - 1).getState() != 1) {
            TaskObject taskObject = pveProgressTasks.get(taskIndex).get(index - 1);
            getPrize(taskObject, normal);
            object.getPveGrowth().get(index - 1).setState(1);
            player.getSession().write(Packet.createResult(packet));
        } else {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            player.getSession().write(error);
        }
    }


    public void update(Normal normal, PveWarInfo pveWarInfo) {
        TaskPveProgressObject taskPvelist = new TaskPveProgressObject();
        for (int i = 0; i < pveProgressTasks.size(); i++) {
            List<TaskObject> list = pveProgressTasks.get(i);

            if (normal.getPveProgressCounts().size() <= i) {
                taskPvelist = new TaskPveProgressObject();
                taskPvelist.setTid(i);
                normal.addPveProgressCounts(taskPvelist);
            }
            for (TaskPveProgressObject obj : normal.getPveProgressCounts()) {
                if (obj.getTid() == i) {
                    taskPvelist = obj;
                    break;
                }
            }
            int size = taskPvelist.getPveGrowth().size();
            if (size >= list.size()) {
                continue;
            }
            TaskObject taskObject = list.get(size);
            if (normal.getLevel() >= taskObject.getLevel()
                    && (taskObject.getCid() == pveWarInfo.getCidx()
                            && taskObject.getPid() == pveWarInfo.getPidx()
                            && taskObject.getSubject() == pveWarInfo.getSubject() && taskObject
                            .getMode() == pveWarInfo.getMode())) {
                if (size < 1) {
                    taskPvelist.addPveGrowth(new RecordObject(size, 0));
                } else {
                    RecordObject beTask = taskPvelist.getPveGrowth().get(size - 1);
                    if (beTask.getState() != 0) {
                        taskPvelist.addPveGrowth(new RecordObject(size, 0));
                    }
                }
            }
        }
    }

    private void getPrize(TaskObject taskObject, Normal normal) {
        if (taskObject.getCopper() != 0) {
            SessionUtils.incrementCopper(taskObject.getCopper());
        }
        if (taskObject.getDiamond() != 0) {
            JSONObject object = new JSONObject();
            object.put("place", "PveProgressTask");
            object.put("Subject", taskObject.getSubject());
            object.put("pid", taskObject.getPid());
            object.put("cid", taskObject.getCid());
            SessionUtils.incrementDiamond(taskObject.getDiamond(),object.toString());
        }
        if (taskObject.getExp() != 0) {
            roleProvider.pushExp(normal, taskObject.getExp());
        }
        if (taskObject.getVigor() != 0) {
            normal.setVigor(normal.getVigor() + taskObject.getVigor());
        }
        if (taskObject.getId() != null) {
            BagUtils.intoItem(taskObject.getId(), taskObject.getCount());
        }
    }
}
