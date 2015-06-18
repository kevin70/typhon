/*
 * Copyright 2013 The Skfiy Open Association.
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
package org.skfiy.typhon.action;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.annotation.Action;
import org.skfiy.typhon.domain.Friend;
import org.skfiy.typhon.domain.Mail;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.VacantData;
import org.skfiy.typhon.packet.MultipleValue;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketError;
import org.skfiy.typhon.packet.PacketFriend;
import org.skfiy.typhon.packet.PacketRole;
import org.skfiy.typhon.packet.SingleValue;
import org.skfiy.typhon.repository.RoleRepository;
import org.skfiy.typhon.session.BagUtils;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.IPlayerNameValidated;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.spi.RoleProvider;
import org.skfiy.typhon.spi.ranking.UpdateRankingList;
import org.skfiy.typhon.spi.sign.SignProvider;
import org.skfiy.typhon.spi.society.SocietyProvider;
import org.skfiy.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import org.skfiy.typhon.packet.DoubleValue;
import org.skfiy.typhon.packet.Platform;
import org.skfiy.typhon.repository.UserRepository;

/**
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class RoleAction {

    private static final Logger LOG = LoggerFactory.getLogger(RoleAction.class);
    private static final Pattern CHINESE_CHAR_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    @Inject
    private RoleRepository roleReposy;
    @Inject
    private UserRepository userReposy;
    @Inject
    private RoleProvider roleProvider;
    @Inject
    private SignProvider signProvider;
    @Inject
    private ItemProvider itemProvider;
    @Inject
    private SessionManager sessionManager;
    @Inject
    private UpdateRankingList updateRankingLists;
    @Inject
    private SocietyProvider societyProvider;
    @Resource(name = "iPlayerNameValidated")
    private Set<IPlayerNameValidated> playerNameValidates;

    /**
     * 
     * @param packet
     */
    @Action(Namespaces.ROLE_CREATE)
    public void create(PacketRole packet) {
        if (StringUtils.isEmpty(packet.getName())
                || StringUtils.containsWhitespace(packet.getName())
                || roleNameLength(packet.getName()) > Typhons.getInteger(
                        "typhon.spi.role.nameMaxLength", 10)) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("name is empty/contains whitespace/max length");
            SessionContext.getSession().write(error);
        }

        packet.setName(roleProvider.randomUniqueName());

        String lock = (packet.getName() + "@role-name").intern();
        synchronized (lock) {
            Session session = SessionContext.getSession();
            if (roleReposy.existsName(packet.getName()) > 0) {
                LOG.debug("exists role name [{}]", packet.getName());
                PacketError error =
                        PacketError.createResult(packet, PacketError.Condition.conflict);
                session.write(error);
                return;
            }

            roleProvider.create(packet.getName());
        }
    }
    
    @Action("get-temp-id")
    public void getTempId(Packet packet) {
        int tempId = userReposy.getNextTempId();
        userReposy.save(String.valueOf(tempId), String.valueOf(tempId), Platform.none);

        DoubleValue result = new DoubleValue();
        Packet.assignResult(packet, result);

        result.setFirst(tempId);
        result.setSecond(tempId);
        SessionContext.getSession().write(result);
    }

    @Action("user-changePassword")
    public void changePassword(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        userReposy.updatePassowrd(player.getRole().getRid(), (String) packet.getVal());
        player.getSession().write(Packet.createResult(packet));
    }
    
    /**
     * 
     * @param packet
     */
    @Action(Namespaces.ROLE_UPDATE_NAME)
    public void updateRoleName(SingleValue packet) {
        String name = (String) packet.getVal();
        if (StringUtils.isEmpty(name) || StringUtils.containsWhitespace(name)
                || roleNameLength(name) > Typhons.getInteger("typhon.spi.role.nameMaxBytes", 14)) {
            throw new IllegalArgumentException("name is null, non string or contains whitespace");
        }

        String lock = (name + "@role-name").intern();
        synchronized (lock) {
            Player player = SessionUtils.getPlayer();

            for (IPlayerNameValidated pnv : playerNameValidates) {
                if (!pnv.validate(name)) {
                    LOG.debug("exists role name [{}]", name);
                    PacketError error =
                            PacketError.createResult(packet, PacketError.Condition.conflict);
                    player.getSession().write(error);
                    return;
                }
            }

            int count = player.getNormal().getUpdateNameCounts();
            if (count != 0) {
                JSONObject object = new JSONObject();
                object.put("place", "UpdateRoleName");
                object.put("counts", count);
                object.put("oldName", player.getRole().getName());
                object.put("newName", name);
                SessionUtils.decrementDiamond(
                        Typhons.getInteger("typhon.spi.role.update.name.depletionDiamond"),
                        object.toString());
            }

            player.getNormal().setUpdateNameCounts(count + 1);
            roleReposy.updateRoleName(player.getRole().getRid(), name);

            player.getRole().setName(name);

            roleProvider.updateInformation();
            player.getSession().write(Packet.createResult(packet));
        }
        // update friends
    }

    /**
     * 保存玩家的新手进度.
     * 
     * @param packet 协议包
     */
    @Action(Namespaces.SV_LEAD)
    public void lead(SingleValue packet) {
        if (packet.getVal() != null) {
            SessionUtils.getPlayer().getNormal().setLead((String) packet.getVal());
        }
    }

    /**
     * 保存拉霸元素的新手引导信息.
     * 
     * @param packet 协议包
     */
    @Action(Namespaces.SV_LEAD_SHOT)
    public void leadShot(SingleValue packet) {
        if (packet.getVal() != null) {
            Normal normal = SessionUtils.getPlayer().getNormal();
            String leadShot = (String) packet.getVal();
            normal.addLeadShot(leadShot);
        }
    }

    /**
     * 修改玩家头像.
     * 
     * @param packet 协议包
     */
    @Action(Namespaces.CH_AVATAR)
    public void changeAvatar(SingleValue packet) {
        if (packet.getVal() != null) {
            Normal normal = SessionUtils.getPlayer().getNormal();
            normal.setAvatar((String) packet.getVal());
            updateRankingLists.updateAllRanking();
            roleProvider.updateInformation();
        }
    }

    /**
     * 修改玩家头像边框.
     * 
     * @param packet 协议包
     */
    @Action(Namespaces.CH_AVATAR_BORDER)
    public void changeAvatarBorder(SingleValue packet) {
        if (packet.getVal() != null) {
            Normal normal = SessionUtils.getPlayer().getNormal();
            normal.setAvatarBorder((String) packet.getVal());
            updateRankingLists.updateAllRanking();
            roleProvider.updateInformation();
        }
    }

    @Action(Namespaces.BUY_VIGOR)
    public void buyVigor(Packet packet) {
        roleProvider.buyVigor(packet);
    }

    //
    @Action(Namespaces.E_MAIL_APPENDIX)
    public void extractMailAppendix(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Mail mail = normal.getMail((int) packet.getVal());

        if (StringUtils.hasLength(mail.getAppendix())) {
            BagUtils.intoItem(itemProvider.getItem(mail.getAppendix()), mail.getCount());
        }
        normal.removeMail(mail);

        player.getSession().write(Packet.createResult(packet));
    }

    @Action(Namespaces.U_MAIL_STATE)
    public void updateMailState(SingleValue packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = player.getNormal();
        Mail mail = normal.getMail((int) packet.getVal());

        mail.setState(Mail.READ_STATE);
    }

    @Action(Namespaces.FRIEND_FIND)
    public void findFriends(PacketFriend packet) {
        Player player = SessionUtils.getPlayer();
        List<Role> roles =
                roleProvider.findFriend(packet.getName(), roleProvider.returnFriendsNumber());
        MultipleValue result = MultipleValue.createResult(packet);
        VacantData vacantData = null;
        String avatar = null;
        String avatarBorder = null;
        String societyName = null;
        for (Role role : roles) {
            int rId = role.getRid();
            if (sessionManager.getSession(rId) == null) {
                vacantData = roleProvider.loadVacantData(rId);
                avatar = vacantData.getAvatar();
                avatarBorder = vacantData.getAvatarBorder();
                societyName = vacantData.getSocietyName();

            } else {
                Player bePlayer = SessionUtils.getPlayer(sessionManager.getSession(rId));
                Normal normal = bePlayer.getNormal();
                avatar = normal.getAvatar();
                avatarBorder = normal.getAvatarBorder();
                societyName = normal.getSocietyName();
            }
            if (rId != player.getRole().getRid()) {
                result.addVal(new Friend(rId, role.getName(), role.getLevel(), roleProvider
                        .findHeroFighting(rId), roleProvider.primaryHeroId(rId), avatar,
                        avatarBorder, societyName));
            }
        }
        Packet.assignResult(packet, result);
        player.getSession().write(result);
    }

    @Action(Namespaces.FRIEND_ADD)
    public void addFriend(PacketFriend packet) {
        Player player = SessionUtils.getPlayer();
        Normal normal = SessionUtils.getPlayer(player.getSession()).getNormal();
        if (!roleProvider.existsRole(packet.getUid())) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("the id is nonentity");
            player.getSession().write(error);
            return;
        }

        // 如果已经是好友则不处理
        if (packet.getUid() == player.getRole().getRid()
                || normal.findFriend(packet.getUid()) != null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("the id is your friend");
            player.getSession().write(error);
            return;
        }
        // 不存在的用户
        roleProvider.addFriend(packet.getUid());
    }

    @Action(Namespaces.FRIEND_ACCEPT)
    public void acceptFriend(PacketFriend packet) {
        roleProvider.acceptFriend(packet);
    }

    @Action(Namespaces.FRIEND_REJECT)
    public void rejectFriend(PacketFriend packet) {
        roleProvider.rejectFriend(packet.getPid());
    }

    @Action(Namespaces.FRIEND_DELETE)
    public void deleteFriend(PacketFriend packet) {
        Session session = SessionContext.getSession();
        Normal normal = SessionUtils.getPlayer().getNormal();

        // 如果不是好友不处理
        Friend friend = normal.findFriend(packet.getUid());
        if (friend == null) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("the uid is not your friend Or the id is yourself");
            session.write(error);
            return;
        }

        roleProvider.deleteFriend(friend.getRid());

        session.write(Packet.createResult(packet));
    }

    @Action(Namespaces.SHOW_HEROLIST)
    public void showHeroInformation(SingleValue packet) {
        Session session = SessionContext.getSession();
        if (!roleProvider.existsRole((int) packet.getVal())) {
            PacketError error = PacketError.createResult(packet, PacketError.Condition.conflict);
            error.setText("the id is not Exist  SHOW_HEROLIST");
            session.write(error);
            return;
        }
        roleProvider.showFightGroups(packet);
    }

    @Action(Namespaces.ROLE_SIGN)
    public void roleSign(SingleValue packet) {
        signProvider.sign(packet);
    }

    @Action(Namespaces.SIGN_AGAIN)
    public void signAgain(SingleValue packet) {
        signProvider.signAgain(packet);
    }

    @Action(Namespaces.RANDOM_NAME)
    public void randomName(SingleValue packet) {
        roleProvider.createName(packet);
    }

    private int roleNameLength(String name) {
        int len = 0;
        String s;
        for (char c : name.toCharArray()) {
            s = String.valueOf(c);
            if (CHINESE_CHAR_PATTERN.matcher(s).matches()) {
                len += 2;
            } else {
                len += s.getBytes().length;
            }
        }
        return len;
    }

    @Action(Namespaces.ROLE_BASE)
    public void roleBase(SingleValue packet) {
        roleProvider.roleBase(packet);
    }

}
