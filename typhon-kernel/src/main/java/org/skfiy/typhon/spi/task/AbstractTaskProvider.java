package org.skfiy.typhon.spi.task;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.RoleProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public abstract class AbstractTaskProvider extends AbstractComponent {

    private final List<TaskObject> taskObjects = new ArrayList<>();
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private ItemProvider itemProvider;

    @Override
    protected void doInit() {
        JSONArray array = JSON.parseArray(loadTaskTable());
        for (int i = 0; i < array.size(); i++) {
            TaskObject obj = array.getObject(i, TaskObject.class);
            JSONObject jsonObject = array.getJSONObject(i);
            if (jsonObject.getString("#item.id") != null) {
                obj.setId(itemProvider.getItem(jsonObject.getString("#item.id")));
            }
            taskObjects.add(obj);
        }
    }

    @Override
    protected void doDestroy() {}

    @Override
    protected void doReload() {}

    protected boolean getRewards(int reference, int index,String type) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        boolean bool = false;
        if (index >= taskObjects.size()) {
            return bool;
        }
        TaskObject taskObject = taskObjects.get(index);
        if (reference >= taskObject.getTerms()) {
            getPrize(taskObject, normal,type,index);
            bool = true;

        }
        return bool;

    }
    protected List<TaskObject> taskObjects()
    {
        return taskObjects;
    }

    protected  void getPrize(TaskObject taskObject, Normal normal,String type,int index) {
        if (taskObject.getCopper() != 0) {
            SessionUtils.incrementCopper(taskObject.getCopper());
        }
        if (taskObject.getDiamond() != 0) {
            JSONObject object = new JSONObject();
            object.put("place", "ContinuousTask");
            object.put("continuousTaskType", type);
            object.put("continuousTaskCounts", index);
            SessionUtils.incrementDiamond(taskObject.getDiamond(), object.toString());
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

    public void taskEntrance(SingleValue packet) {

        Player player = SessionUtils.getPlayer();

        if (!taskEntrance(player, packet)) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("the Task is not complete");
            player.getSession().write(error);
            return;
        }

        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 
     * @return
     */
    protected abstract String loadTaskTable();

    protected abstract boolean taskEntrance(Player player, SingleValue packet);
}
