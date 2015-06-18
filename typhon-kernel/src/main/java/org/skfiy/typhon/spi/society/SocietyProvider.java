/*
 * Copyright 2014 The Skfiy Open Association.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.skfiy.typhon.spi.society;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.GlobalData;
import org.skfiy.typhon.domain.Incident;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.packet.DoubleValue;
import org.skfiy.typhon.packet.MultipleValue;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.packet.SocietyPacket;
import org.skfiy.typhon.repository.GlobalDataRepository;
import org.skfiy.typhon.repository.IncidentRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ConfigurationLoader;
import org.skfiy.typhon.spi.IncidentConstants;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class SocietyProvider extends AbstractComponent {

    public static final String CONFIG_SID_SEQ_KEY = "society.lastSid";
    private static final AtomicInteger SID_SEQ = new AtomicInteger(1000);

    private final Timer TIMER = new Timer("Society-Timer", true);
    private final Timer TIMER1 = new Timer("UpdateSociety-Timer", true);
    private final List<Society> societies = new ArrayList<>();
    private int updateChairmanTime;

    @Inject
    private GlobalDataRepository globalDataReposy;
    @Inject
    private IncidentRepository incidentReposy;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private ConfigurationLoader configurationLoader;

    @Override
    protected void doInit() {
        GlobalData globalData = globalDataReposy.getGlobalData(GlobalData.Type.society_data);
        societies.addAll(JSON.parseArray(globalData.getData(), Society.class));

        SID_SEQ.set(configurationLoader.getServerInt(CONFIG_SID_SEQ_KEY, 1000));

        TIMER.schedule(new TimerTask() {

            @Override
            public void run() {
                // 扫描公会


                saveData();
            }
        }, Typhons.getLong("typhon.spi.pvp.saveDataFixedRateMs"),
                Typhons.getLong("typhon.spi.pvp.saveDataFixedRateMs"));

        TIMER1.schedule(new TimerTask() {

            @Override
            public void run() {
                updateChairman();

            }
        },120000, Typhons.getLong("typhon.spi.society.updateChairman"));
        updateChairmanTime = Typhons.getInteger("typhon.spi.society.updateChairman.time");
    }

    @Override
    protected void doReload() {

    }

    @Override
    protected void doDestroy() {
        updateChairman();
        saveData();
    }

    /**
     * 创建公会.
     * 
     * @param packet 协议包
     */
    public synchronized void create(SocietyPacket packet) {
        Player player = SessionUtils.getPlayer();

        if (StringUtils.isEmpty(packet.getName()) || findByName(packet.getName()) != null) {
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.society_name_illegal);
            player.getSession().write(error);
            return;
        }

        if (player.getNormal().getSocietyId() > 0) {
            // 不能接受
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.not_acceptable);
            player.getSession().write(error);
            return;
        }

        if (player.getNormal().getSocietyLeaveTime() > System.currentTimeMillis()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.time_over);
            error.setText("Leave Society time limit");
            player.getSession().write(error);
            return;
        }

        int diamond = Typhons.getInteger("typhon.spi.society.create.depletion", 500);
        SessionUtils.checkDiamond(diamond);

        // 获取公会的ID
        int sid;
        for (;;) {
            sid = SID_SEQ.getAndIncrement();
            if (findBySid(sid) == null) {
                break;
            }
        }

        //
        Society society = new Society();
        society.setSid(sid);
        society.setName(packet.getName());
        society.setIcon(packet.getIcon());
        society.setSummary(packet.getSummary());
        society.setChairmanRid(player.getRole().getRid());

        // 设置会长权限
        Member member = newMember(player);
        member.setPerm(Member.CHAIRMAN_PERM);
        society.addMember(member);

        player.getNormal().setSocietyId(society.getSid());
        player.getNormal().setSocietyName(packet.getName());
        JSONObject object = new JSONObject();
        object.put("place", "CreateSociety");
        object.put("societyName", packet.getName());
        SessionUtils.decrementDiamond(diamond, object.toString());

        societies.add(society);

        // 设置新的SID号
        configurationLoader.setServerProperty(CONFIG_SID_SEQ_KEY, sid);
        player.getSession().write(Packet.createResult(packet));
        roleProvider.updateInformation();
        pushRecord(society, member.getName(), null, "create");
    }

    /**
     * 解散公会.
     * 
     * @param packet 协议包
     */
    public void dissolve(Packet packet) {
        Player player = SessionUtils.getPlayer();

        Society society = findBySid(player.getNormal().getSocietyId());
        if (society == null
                || society.findMember(player.getRole().getRid()).getPerm() != Member.CHAIRMAN_PERM) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            player.getSession().write(error);
            return;
        }

        // 删除公会
        societies.remove(society);

        for (Member member : society.getMembers()) {
            setPlayerSocietyId(member.getRid(), 0);
        }
    }

    /**
     * 
     * @param packet
     */
    public void loadSocieties(SingleValue packet) {
        int p = (int) packet.getVal();
        MultipleValue result = MultipleValue.createResult(packet);

        for (int i = (p - 1) * 5; i < (p * 5 + 1); i++) {
            if (i >= societies.size()) {
                break;
            }

            result.addVal(new SocietyResult(societies.get(i)));
        }

        SessionContext.getSession().write(result);
    }

    /**
     * 请受别人入会请求.
     * 
     * @param packet 协议包
     */
    public synchronized void accept(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Society society = findBySid(player.getNormal().getSocietyId());
        if (society.getMembers().size() >= society.getMaxMember()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.size_limit);
            player.getSession().write(error);
            return;
        }
        int rid = (int) packet.getVal();
        Member member = society.findRequestMember(rid);
        if (getPlayerSocietyId(rid) > 0) {
            society.removeRequestMember(member);

            // 不能接受
            PacketError error =
                    PacketError.createResult(packet, PacketError.Condition.not_acceptable);
            player.getSession().write(error);
            return;
        }

        Session beSession = sessionManager.getSession(member.getRid());
        if (beSession == null) {
            VacantData vacantData = roleProvider.loadVacantData(member.getRid());
            member.setAvatar(vacantData.getAvatar());
            member.setAvatarBorder(vacantData.getAvatarBorder());
            member.setLevel(vacantData.getLevel());
            member.setName(vacantData.getName());
        } else {
            Player bePlayer = SessionUtils.getPlayer(beSession);
            Normal beNormal = bePlayer.getNormal();
            member.setAvatar(beNormal.getAvatar());
            member.setAvatarBorder(beNormal.getAvatarBorder());
            member.setLevel(beNormal.getLevel());
            member.setName(bePlayer.getRole().getName());
        }
        society.addMember(member);
        society.removeRequestMember(member);

        // 设置玩家的公会ID
        setPlayerSocietyId(rid, society.getSid());

        // 排序公会..
        sortSocieties();
        pushRecord(society, member.getName(), null, "join");
    }

    /**
     * 拒绝别人的申请.
     * 
     * @param packet 协议包
     */
    public void reject(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Society society = findBySid(player.getNormal().getSocietyId());

        int rid = (int) packet.getVal();

        Member member = society.findRequestMember(rid);
        society.removeRequestMember(member);
    }

    /**
     * 申请加入公会.
     * 
     * @param packet 协议包
     */
    public void apply(SingleValue packet) {
        Player player = SessionUtils.getPlayer();

        int sid = (int) packet.getVal();
        Society society = findBySid(sid);
        if (society == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            player.getSession().write(error);
            return;
        }

        if (society.getMembers().size() >= society.getMaxMember()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.size_limit);
            player.getSession().write(error);
            return;
        }

        if (society.findMember(player.getRole().getRid()) != null
                || society.findRequestMember(player.getRole().getRid()) != null) {
            return;
        }
        if (player.getNormal().getSocietyLeaveTime() > System.currentTimeMillis()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.time_over);
            error.setText("Leave Society time limit");
            player.getSession().write(error);
            return;
        }

        Member member = newMember(player);
        society.addRequestMember(member);

        // FIXME 给公会发一条消息
        // 排序公会
        sortSocieties();
    }

    /**
     * 踢出某个成员出公会.
     * 
     * @param packet
     */
    public void kickout(SingleValue packet) {
        Player player = SessionUtils.getPlayer();

        int rid = (int) packet.getVal();
        if (rid == player.getRole().getRid()) {
            // 无法把自己踢出公会
            return;
        }

        Society society = findBySid(player.getNormal().getSocietyId());
        Member member = society.findMember(rid);

        if (member != null) {
            setPlayerSocietyId(member.getRid(), 0);
            society.removeMember(member);

            // 排序公会
            sortSocieties();
        }

        pushRecord(society, member.getName(), player.getRole().getName(), "kickout");
    }

    /**
     * 离开公会.
     * 
     * @param packet 协议包
     */
    public void leave(Packet packet) {
        Player player = SessionUtils.getPlayer();

        Society society = findBySid(player.getNormal().getSocietyId());
        Member member = society.findMember(player.getRole().getRid());
        if (member.getPerm() == Member.CHAIRMAN_PERM) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("is chairman");
            player.getSession().write(error);
            return;
        }

        setPlayerSocietyId(member.getRid(), 0);
        society.removeMember(member);

        player.getSession().write(Packet.createResult(packet));
        // 排序公会
        sortSocieties();
        // 离开工会
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        player.getNormal().setSocietyLeaveTime(calendar.getTimeInMillis());

        pushRecord(society, member.getName(), null, "leave");
    }

    /**
     * 加载当前公会信息.
     * 
     * @param packet 协议包
     */
    public void load(Packet packet) {
        Player player = SessionUtils.getPlayer();
        SingleValue result =
                SingleValue.createResult(packet, findBySid(player.getNormal().getSocietyId()));
        player.getSession().write(result);
    }

    /**
     * 修改信息
     * 
     * @param packet 协议包
     */
    public void updateInfo(DoubleValue packet) {
        Player player = SessionUtils.getPlayer();
        Society society = findBySid(player.getNormal().getSocietyId());

        String flag = (String) packet.getFirst();
        String v = (String) packet.getSecond();

        Member member = society.findMember(player.getRole().getRid());
        if (member.getPerm() == Member.NORMAL_PERM) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("No prem");
            player.getSession().write(error);
            return;
        }

        switch (flag) {
            case "summary":
                society.setSummary(v);
                break;
            case "icon":
                society.setIcon(v);
                break;
            case "levelLimit":
                society.setLevelLimit(Integer.valueOf(v));
                break;
        }

        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 设置公会权限.
     * 
     * @param packet 协议包
     */
    public void updatePerm(DoubleValue packet) {
        Player player = SessionUtils.getPlayer();
        Society society = findBySid(player.getNormal().getSocietyId());

        int rid = (int) packet.getFirst();
        int p = (int) packet.getSecond();

        Member member = society.findMember(rid);
        if (member.getRid() == player.getRole().getRid()) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("myself");
            player.getSession().write(error);
            return;
        }

        Member myMember = society.findMember(player.getRole().getRid());
        if (myMember.getPerm() != Member.CHAIRMAN_PERM) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("Need chairman perm.");
            player.getSession().write(error);
            return;
        }

        updatePerm(society, myMember, member, p);
        player.getSession().write(Packet.createResult(packet));
    }

    /**
     * 根据公会ID查询公会对象.
     * 
     * @param sid 公会ID
     * @return
     */
    public Society findBySid(int sid) {
        for (Society soc : societies) {
            if (soc.getSid() == sid) {
                return soc;
            }
        }
        return null;
    }

    /**
     * 根据公会名称查询公会对象.
     * 
     * @param name 公会名称
     * @return
     */
    public Society findByName(String name) {
        for (Society soc : societies) {
            if (soc.getName().equals(name)) {
                return soc;
            }
        }
        return null;
    }

    /**
     * 会长三天未上线更换会长
     * 
     * @param member
     * @param p
     */

    public void updateChairman() {
        for (Society soc : societies) {
            for (Member member : soc.getMembers()) {
                if (member.getPerm() == Member.CHAIRMAN_PERM) {
                    Session session = sessionManager.getSession(member.getRid());
                    if (session != null) {
                        break;
                    } else {
                        VacantData vacantData = roleProvider.loadVacantData(member.getRid());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(vacantData.getLastLogoutTime());
                        roleProvider.clearCalendar(calendar);
                        calendar.add(Calendar.DATE, updateChairmanTime);
                        if (System.currentTimeMillis() < calendar.getTimeInMillis()) {
                            break;
                        } else {
                            member.setPerm(Member.NORMAL_PERM);
                            Member member1 = getMember(soc);
                            member1.setPerm(Member.CHAIRMAN_PERM);
                            soc.setChairmanRid(member1.getRid());
                            break;
                        }
                    }
                }
            }
        }
    }

    private Member getMember(Society society) {
        List<Member> members = new ArrayList<>();
        for (Member member : society.getMembers()) {
            if (member.getPerm() == Member.ELDER_PERM) {
                Session session = sessionManager.getSession(member.getRid());
                if (session != null) {
                    return member;
                } else {
                    members.add(member);
                }
            }
        }
        if (members.isEmpty()) {
            for (Member member : society.getMembers()) {
                if (member.getPerm() == Member.NORMAL_PERM) {
                    Session session = sessionManager.getSession(member.getRid());
                    if (session != null) {
                        return member;
                    } else {
                        members.add(member);
                    }
                }
            }
        }
        Member meb = members.get(0);
        VacantData vacantData = roleProvider.loadVacantData(meb.getRid());
        for (int i = 0; i < members.size(); i++) {
            VacantData nextVacantData = roleProvider.loadVacantData(members.get(i).getRid());
            if (nextVacantData.getLastLogoutTime() > vacantData.getLastLogoutTime()) {
                meb = members.get(i);
                vacantData = nextVacantData;
            }
        }
        return meb;
    }

    private void updatePerm(Society society, Member myMember, Member member, int p) {
        if (p == Member.CHAIRMAN_PERM) {
            // 设置会长<转让公会>
            member.setPerm(Member.CHAIRMAN_PERM);
            society.setChairmanRid(member.getRid());

            myMember.setPerm(Member.NORMAL_PERM);

            pushRecord(society, member.getName(), myMember.getName(), "perm_1");
        } else if (p == Member.ELDER_PERM) {
            // 设置长老
            member.setPerm(Member.ELDER_PERM);
            pushRecord(society, member.getName(), myMember.getName(), "perm_2");
        } else {
            member.setPerm(Member.NORMAL_PERM);
            pushRecord(society, member.getName(), myMember.getName(), "perm_0");
        }
    }

    private void pushRecord(Society society, String name, String annex, String event) {
        Record record = new Record();
        record.setTime(System.currentTimeMillis() / 1000);
        record.setName(name);
        record.setAnnex(annex);
        record.setEvent(event);
        society.getRecords().add(record);
    }

    private synchronized void sortSocieties() {
        Collections.sort(societies);
    }

    private int getPlayerSocietyId(int rid) {
        Session session = sessionManager.getSession(rid);
        if (session == null) {
            return roleProvider.loadVacantData(rid).getSocietyId();
        }

        Player player = SessionUtils.getPlayer(session);
        return player.getNormal().getSocietyId();
    }

    private void setPlayerSocietyId(int rid, int societyId) {
        Session session = sessionManager.getSession(rid);
        if (session != null) {
            Player player = SessionUtils.getPlayer(session);
            player.getNormal().setSocietyId(societyId);

            if (societyId == 0) {
                player.getNormal().setSocietyName(null);
            } else {
                player.getNormal().setSocietyName(findBySid(societyId).getName());
            }
            roleProvider.updateInformation();
        } else {
            // 设置玩家的公会ID
            VacantData vacantData = roleProvider.loadVacantData(rid);
            vacantData.setSocietyId(societyId);

            Incident incident = new Incident();
            incident.setUid(rid);
            incident.setData(String.valueOf(societyId));
            incident.setEventName(IncidentConstants.EVENT_SOCIETY_SET_PLAYER_SOCIETY_ID);
            incidentReposy.save(incident);
        }
    }

    private Member newMember(Player player) {
        Member member = new Member();
        member.setRid(player.getRole().getRid());
        member.setName(player.getRole().getName());
        member.setAvatar(player.getNormal().getAvatar());
        member.setAvatarBorder(player.getNormal().getAvatarBorder());
        member.setLevel(player.getNormal().getLevel());
        return member;
    }

    private void saveData() {
        GlobalData globalData = new GlobalData();
        globalData.setType(GlobalData.Type.society_data);
        globalData.setData(JSON.toJSONString(societies));
        globalDataReposy.updateGlobalData(globalData);
    }
}
