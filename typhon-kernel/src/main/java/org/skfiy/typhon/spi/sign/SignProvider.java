package org.skfiy.typhon.spi.sign;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ConfigurationLoader;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.ServerSettingKeys;
import org.skfiy.typhon.util.ComponentUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Singleton
public class SignProvider extends AbstractComponent {
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private ConfigurationLoader configurationLoader;

    private JSONArray arrayCount;

    //Map<Integer（年）,Map<Integer（月）,Map<Integer(日),Sign>>>
    public static Map<Integer, Map<Integer, Map<Integer, Sign>>> signies = new HashMap<>();



    @Override
    protected void doDestroy() {

    }

    @Override
    protected void doInit() {
        JSONObject obj = JSON.parseObject(ComponentUtils.readDataFile("month_sign.json"));

        for (Entry<String, Object> entry : obj.entrySet()) {

            JSONObject array = obj.getJSONObject(entry.getKey());
            Map<Integer, Map<Integer, Sign>> asign = new HashMap<>();

            for (Entry<String, Object> entries : array.entrySet()) {

                JSONArray arraies = array.getJSONArray(entries.getKey());
                Map<Integer, Sign> bsign = new HashMap<>();

                for (int i = 0; i < arraies.size(); i++) {

                    Sign sign = new Sign();
                    sign = JSON.toJavaObject(arraies.getJSONObject(i), Sign.class);
                    bsign.put(i, sign);
                }
                asign.put(Integer.valueOf(entries.getKey()), bsign);
            }
            signies.put(Integer.valueOf(entry.getKey()), asign);
        }

        arrayCount = JSON.parseArray(ComponentUtils.readDataFile("sign_cost.json"));
    }

    @Override
    protected void doReload() {

    }

    public void sign(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        //签到次数packet.getval();
        int size = normal.getSigns().size();
        //领取物品的倍数 &&也代表领取次数
        Calendar calendar = Calendar.getInstance();
        int count = 1;
        Map<Integer, Sign> signa = inster(size, calendar, player);
        if (signa == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Time since I can't sign in");
            player.getSession().write(error);
            return;
        }
        
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //获取当前签到的Sign
        Sign sign = signa.get(size);
        if (sign.getVipLevel() <= normal.getVipLevel()) {
            count = 2;
        }

        ItemDobj item = itemProvider.getItem(sign.getPrize());
        BagUtils.intoItem(item, sign.getCount() * count);

        SignDraw signdraw = new SignDraw(count, day, normal.getVipLevel());
        normal.addSigns(signdraw);
        player.getSession().write(Packet.createResult(packet));
    }


    public void signAgain(SingleValue packet) {
        Player player = SessionUtils.getPlayer();

        Normal normal = player.getNormal();
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //领取某天补签第二份奖励
        int oneDay = (int) packet.getVal() - 1;

        Sign sign = new Sign();
        int count = 1;

        Map<Integer, Sign> signa = new HashMap<Integer, Sign>();
        //size+1代表签到的数据size
        signa = inster(oneDay, calendar, player);
        if (signa == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("No data cannot be retroactive and be rewarded Sign");
            player.getSession().write(error);
            return;
        }
        if (oneDay + 1 > normal.getSigns().size()) {
            //获取当前签到的Sign
            sign = signa.get(oneDay);
            if (sign.getVipLevel() <= normal.getVipLevel()) {
                count = 2;
            }
            SignDraw signdraw = new SignDraw(count, day, normal.getVipLevel());
            normal.addSigns(signdraw);
            //增加补签次数
            int signed = player.getNormal().getSigned();
            //扣去补签的钱
            int cost = arrayCount.getInteger(signed);
            JSONObject object = new JSONObject();
            object.put("place", "SignAgain");
            object.put("signAgainCounts", signed);
            object.put("signAgainItems", sign.getPrize());
            SessionUtils.decrementDiamond(cost, object.toString());
            player.getNormal().setSigned(signed + 1);

        } else {
            SignDraw signDraw = normal.getSigns().get(oneDay);
            //size代表list中的数据索引 从0开始
            if (signDraw.getDrawTime() != day || signDraw.getCount() == 2
                    || signDraw.getLastVip() >= normal.getVipLevel()) {
                PacketError error =
                        PacketError.createResult(packet, PacketError.Condition.conflict);
                error.setText("No again signed number");
                player.getSession().write(error);
                return;
            } else {
                sign = signa.get(oneDay);
                signDraw.setCount(count + 1);
                signDraw.setDrawTime(day);
                signDraw.setLastVip(normal.getVipLevel());
            }
        }
        ItemDobj item = itemProvider.getItem(sign.getPrize());
        BagUtils.intoItem(item, sign.getCount() * count);
        player.getSession().write(Packet.createResult(packet));
    }


    private Map<Integer, Sign> inster(int size, Calendar calendar,Player player) {
        
        Calendar serverCalendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        serverCalendar.setTimeInMillis(player.getRole().getCreationTime());
        //对开服时间进行特殊判断
        if (year == serverCalendar.get(Calendar.YEAR)
                && month == serverCalendar.get(Calendar.MONTH)
                && size + serverCalendar.get(Calendar.DAY_OF_MONTH) > day) {

            return null;
        } else {
            if (size > day) {
                return null;
            }
        }
        Map<Integer, Map<Integer, Sign>> signss = signies.get(year);
        Map<Integer, Sign> map = signss.get(month + 1);
        return map;
    }
}
