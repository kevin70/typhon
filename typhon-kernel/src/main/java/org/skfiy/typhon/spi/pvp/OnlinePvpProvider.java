/*
 * Copyright 2014 The Skfiy Open Association.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skfiy.typhon.spi.pvp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.domain.FightGroup;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Troop;
import org.skfiy.typhon.domain.item.HeroItem;
import org.skfiy.typhon.packet.AttackPacket;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.OnlinePvpPacket;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.war.Direction;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.WarInfo;
import org.skfiy.typhon.spi.war.WarProvider;
import org.skfiy.typhon.spi.war.WarReport;
import org.skfiy.util.Assert;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class OnlinePvpProvider extends AbstractComponent {

    private static final String ONLINE_PVP_SESSION_KEY = "__onlinePvpSessionKey";

    private final AtomicInteger OP_ID_SEQ = new AtomicInteger();
    private final Map<String, OnlinePvpWarInfo> onlinePvpWarInfos = new HashMap<>();

    @Inject
    private SessionManager sessionManager;
    @Inject
    private WarProvider warProvider;

    @Override
    protected void doInit() {
    }

    @Override
    protected void doReload() {
    }

    @Override
    protected void doDestroy() {
    }

    // invite 邀请对战
    public void invite(SingleValue packet) {
        Player player = SessionUtils.getPlayer();

        Session otherSession = sessionManager.getSession((int) packet.getVal());
        if (otherSession == null) {
            // No online.
            player.getSession().write(PacketError.createResult(packet,
                    PacketError.Condition.not_online));
            return;
        }

        // 对方已经存在战斗
        if (otherSession.getAttribute(ONLINE_PVP_SESSION_KEY) != null) {
            player.getSession().write(PacketError.createResult(packet,
                    PacketError.Condition.opvp_unable_connect));
            return;
        }

        String opid = generateId();
        OnlinePvpWarInfo opInfo = new OnlinePvpWarInfo(opid);
        onlinePvpWarInfos.put(opid, opInfo);

        // 设置攻击方基本信息
        WarInfo.Entity attackerEntity = new WarInfo.Entity(Direction.S);
        attackerEntity.setRid(player.getRole().getRid());
        attackerEntity.setRoleName(player.getRole().getName());
        attackerEntity.setLevel(player.getRole().getLevel());
        opInfo.setAttackerEntity(attackerEntity);

        // 在Session中保存战斗信息
        player.getSession().setAttribute(ONLINE_PVP_SESSION_KEY, opInfo);

        Player otherPlayer = SessionUtils.getPlayer(otherSession);

        // 通知对方邀请信息
        JSONObject result = new JSONObject();
        result.put("id", packet.getId());
        result.put("opid", opid);
        result.put("name", otherPlayer.getRole().getName());
        otherSession.write(Namespaces.OPVP_INVITED, result);
    }

    // accept 接受对战
    public void accept(SingleValue packet) {
        // 发送好友列表信息
        String opid = (String) packet.getVal();
        OnlinePvpWarInfo opInfo = onlinePvpWarInfos.get(opid);
        if (opInfo == null) {

            return;
        }

        Session otherSession = sessionManager.getSession(opInfo.getAttackerEntity().getRid());
        if (SessionUtils.isSessionAvailable(otherSession)) {
            Packet result = Packet.createResult(packet);
            result.setNs(Namespaces.OPVP_ACCEPTED);
            otherSession.write(result);
        }

        Player player = SessionUtils.getPlayer();

        // 设置防御方基本信息
        WarInfo.Entity defenderEntity = new WarInfo.Entity(Direction.N);
        defenderEntity.setRid(player.getRole().getRid());
        defenderEntity.setRoleName(player.getRole().getName());
        defenderEntity.setLevel(player.getRole().getLevel());
        opInfo.setDefenderEntity(defenderEntity);

        // 在Session中保存战斗信息
        player.getSession().setAttribute(ONLINE_PVP_SESSION_KEY, opInfo);
    }

    /**
     *
     * @param packet
     */
    public void reject(SingleValue packet) {
        String opid = (String) packet.getVal();
        OnlinePvpWarInfo opInfo = onlinePvpWarInfos.remove(opid);
        if (opInfo != null) {
            Session otherSession = sessionManager.getSession(opInfo.getAttackerEntity().getRid());
            if (SessionUtils.isSessionAvailable(otherSession)) {
                otherSession.removeAttribute(ONLINE_PVP_SESSION_KEY);

                Packet result = Packet.createResult(packet);
                result.setNs(Namespaces.OPVP_REJECTED);
                otherSession.write(result);
            }
        }
    }

    /**
     *
     * @param packet
     */
    public void prepare(OnlinePvpPacket packet) {
        Player player = SessionUtils.getPlayer();
        OnlinePvpWarInfo onlinePvpWarInfo = onlinePvpWarInfos.get(packet.getOpid());
        initWarInfoEntity(getWarInfoEntity(player, onlinePvpWarInfo), player, packet);

        // 开战
        if (onlinePvpWarInfo.getAttackerEntity() != null
                && onlinePvpWarInfo.getDefenderEntity() != null) {
            warProvider.prepare(onlinePvpWarInfo);

            sendRival(onlinePvpWarInfo.getAttackerEntity().getRid(),
                    onlinePvpWarInfo.getDefenderEntity());
            sendRival(onlinePvpWarInfo.getDefenderEntity().getRid(),
                    onlinePvpWarInfo.getAttackerEntity());
            sendReady(packet, onlinePvpWarInfo);
        }
    }

    // attack 攻击
    public void attack(AttackPacket packet) {
//        Player player = SessionUtils.getPlayer();
//
//        // 判断是哪个方向的人
//        OnlinePvpWarInfo opWarInfo = (OnlinePvpWarInfo) player.getSession().getAttribute(
//                ONLINE_PVP_SESSION_KEY);
//
//        // 计算战斗返回结果集
//        List<Object> details = new ArrayList<>();
////        WarReport.Effect effect = warProvider.attack(packet.getHoldPoints(), opWarInfo,
////                getWarInfoEntity(player, opWarInfo).getDire(), details);
//
//        // 战斗结果
//        SingleValue result = SingleValue.createResult(packet, details);
//        String str = JSON.toJSONString(result);
//
//        sendAttack(opWarInfo.getAttackerEntity(), str);
//        sendAttack(opWarInfo.getDefenderEntity(), str);
//
//        switch (effect) {
//            case W:
//                break;
//            case D:
//                break;
//            default:
//            // 通知下一次攻击方
//        }
    }

    private WarInfo.Entity getWarInfoEntity(Player player, OnlinePvpWarInfo onlinePvpWarInfo) {
        int rid = player.getRole().getRid();
        if (rid == onlinePvpWarInfo.getAttackerEntity().getRid()) {
            return onlinePvpWarInfo.getAttackerEntity();
        } else if (rid == onlinePvpWarInfo.getDefenderEntity().getRid()) {
            return onlinePvpWarInfo.getDefenderEntity();
        } else {
            // FIXME 修改异常处理
            throw new RuntimeException();
        }
    }

    private boolean sendAttack(WarInfo.Entity entity, String str) {
        Session session = sessionManager.getSession(entity.getRid());
        if (!SessionUtils.isSessionAvailable(session)) {
            return false;
        }

        session.write(Namespaces.OPVP_ATTACK, str);
        return true;
    }

    private boolean sendRival(int sid, WarInfo.Entity entity) {
        Session session = sessionManager.getSession(sid);

        if (!SessionUtils.isSessionAvailable(session)) {
            return false;
        }

        String str = JSON.toJSONString(warProvider.newWarReportEntity(entity));
        session.write(Namespaces.OPVP_RIVAL, str);

        return true;
    }

    private boolean sendReady(Packet packet, OnlinePvpWarInfo onlinePvpWarInfo) {
        Session otherSession;
        switch (onlinePvpWarInfo.getNextDire()) {
            case S:
                otherSession = sessionManager.getSession(onlinePvpWarInfo.getAttackerEntity().getRid());
                break;
            default:
                otherSession = sessionManager.getSession(onlinePvpWarInfo.getDefenderEntity().getRid());
        }

        if (!SessionUtils.isSessionAvailable(otherSession)) {
            return false;
        }

        Packet result = Packet.createResult(packet);
        result.setNs(Namespaces.OPVP_READY);
        otherSession.write(result);

        return true;
    }

    private void initWarInfoEntity(WarInfo.Entity entity, Player player, OnlinePvpPacket packet) {
//        Normal normal = player.getNormal();
//        FightGroup fightGroup = normal.getFightGroup(packet.getGidx());
//        List<FightObject> fightObjects = new ArrayList<>();
//
//        Troop troop;
//        HeroItem hero;
//        for (int i = 0; i < fightGroup.getHeroItems().length; i++) {
//            hero = fightGroup.getHeroItem(i);
//            troop = warProvider.getTroop(normal, i);
//            fightObjects.add(warProvider.newFightObject(i, troop, hero));
//        }
//
//        entity.setFightObjects(fightObjects);
//        entity.setSuccor(warProvider.loadSuccorFightObject(packet.getSuccorRid(), packet.getSuccorIid(),
//                fightObjects.get(FightGroup.PRIMARY_POS)));
    }

    private String generateId() {
        int seq = OP_ID_SEQ.incrementAndGet();
        OP_ID_SEQ.compareAndSet(Integer.MAX_VALUE, 0);
        return Integer.toHexString(seq);
    }

    /**
     *
     */
    private static class OnlinePvpWarInfo extends WarInfo {

        String opid;
        long lastAccessedTime = System.currentTimeMillis();

        OnlinePvpWarInfo(String opid) {
            this.opid = opid;
        }

        long updateLastAccessTime() {
            lastAccessedTime = System.currentTimeMillis();
            return lastAccessedTime;
        }
    }

}
