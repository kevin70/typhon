package org.skfiy.typhon.spi;

public interface IncidentConstants {

    /**
     * 请求添加好友.
     */
    String EVENT_FRIEND_REQUEST = "friend.request";

    /**
     * 接受好友请求.
     */
    String EVENT_FRIEND_ACCEPTED = "friend.accepted";

    /**
     * 拒绝好友请求.
     */
    String EVENT_FRIEND_REJECTED = "friend.rejected";

    /**
     * 删除好友.
     */
    String EVENT_FRIEND_DELETEED = "friend.delete";

    /**
     * 玩家新邮件.
     */
    String EVENT_MAIL_NEW = "mail.new";

    /**
     * PVP战报.
     */
    String EVENT_PVP_REPORT = "mail.pvpReport";

    /**
     * 竞技场排名改变.
     */
    String EVENT_PVP_RANKING_CHANGED = "mail.pvpRankingChanged";
    
    /**
     * 冲值.
     */
    String EVENT_VIP_RECHARGING = "vip.recharging";
    
    /**
     * 设置玩家的公会ID.
     */
    String EVENT_SOCIETY_SET_PLAYER_SOCIETY_ID = "society.setPlayerSocietyId";
    /**
     * 更新好友信息.
     */
    String EVENT_UPDATE_FRIENDS="friend.update";
    /**
     * 援军领取体力.
     */
    String ADI_RECEIVE_VIGOR="adi_receive_vigor";
    
    /**
     *邀请码. 
     */
    String INVITE_MANAGER="invite.manager";
    
    /**
     * Boss攻打次数.
     */
    String RESET_BOSSCOUNT="reset.bossCount";
}
