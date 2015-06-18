/*
 * Copyright 2013 The Skfiy Open Association.
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
package org.skfiy.typhon.packet;

/**
 * Packet 命名空间定义.
 * 
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
public interface Namespaces {

    /**
     * 获取服务器Timestamp.
     */
    String TIMESTAMP = "timestamp";

    /**
     * 错误信息的命名空间.
     */
    String ERROR = "error";

    /**
     * 普通用户认证命名空间.
     */
    String AUTH = "auth";

    /**
     * OAuth2 认证命名空间.
     */
    String OAUTH2 = "oauth2";

    /**
     * 登出.
     */
    String LOGOUT = "logout";

    /**
     * 登录返回User信息命名空间.
     */
    String USER_INFO = "user-info";

    /**
     * 登录返回的Role信息.
     */
    String ROLE = "role";

    /**
     * 创建角色.
     */
    String ROLE_CREATE = "role-create";

    /**
     * 更新角色名称.
     */
    String ROLE_UPDATE_NAME = "role-updateName";

    /**
     * 玩家信息.
     */
    String PLAYER_INFO = "player-info";

    /**
     * 玩家数据修改.
     */
    String SET_PLAYER = "set-player";

    /**
     * 保存玩家新手引导的进度.
     */
    String SV_LEAD = "sv-lead";

    /**
     * 保存拉霸元素的新手引导信息.
     */
    String SV_LEAD_SHOT = "sv-leadShot";

    /**
     * 修改玩家头像.
     */
    String CH_AVATAR = "ch-avatar";

    /**
     * 修改玩家头像边框.
     */
    String CH_AVATAR_BORDER = "ch-avatarBr";

    /**
     * 一个属性值.
     */
    String SINGLE_VAL = "a-val";

    /**
     * 心跳消息命名空间.
     */
    String PING = "ping";

    /**
     * 购买体力.
     */
    String BUY_VIGOR = "buy-vigor";

    /**
     * 装载武将.
     */
    String HERO_LOAD = "hero-load";

    /**
     * 攻击组武将交换.
     */
    String HERO_SWAP = "hero-swap";

    /**
     * 修改攻击组的队长技能.
     */
    String HERO_CHANGE_CAPTAIN = "hero-changeCaptain";

    /**
     * 武将槽口激活.
     */
    String HERO_RABBET = "hero-rabbet";

    /**
     * 武将升阶.
     */
    String HERO_UPLADDER = "hero-upladder";

    /**
     * 武将升星.
     */
    String HERO_UPSTAR = "hero-upstar";

    /**
     * 武将召唤.
     */
    String HERO_BECKON = "hero-beckon";

    /**
     * 战前查询好友.
     */
    String PVE_SEARCH_SUCCOR = "pve-searchSuccor";

    /**
     * 加载好友数据.
     */
    String PVE_LOAD_SUCCOR = "pve-loadSuccor";

    /**
     * 进入PVE战斗.
     */
    String PVE_ENTER = "pve-enter";

    /**
     * 退出PVE战斗.
     */
    String PVE_EXIT = "pve-exit";

    /**
     * 领取成就奖励.
     */
    String PVE_REC_AWARD = "pve-rec-award";

    /**
     * PVP查询援手.
     */
    String SEARCH_SUCCOR = "searchSuccor";

    /**
     * PVP查询对手.
     */
    String PVP_SEARCH_RIVALS = "pvp-searchRivals";

    /**
     * PVP选择对手.
     */
    String PVP_CHOOSE_RIVAL = "pvp-chooseRival";

    /**
     * PVP刷新次数.
     */
    String PVP_BUY_COUNT = "pvp-buyCount";

    /**
     * 刷新PVP冷却时间.
     */
    String PVP_REFRESH_CD = "pvp-refreshCd";

    /**
     * 加载PVP排行榜.
     */
    String PVP_LOAD_RANKING_LIST = "pvp-loadRankingList";

    /**
     * PVP回放.
     */
    String PVP_PLAYBACK = "pvp-playback";

    /**
     * PVP商店刷新.
     */
    String PVP_STORE_REFRESH = "pvp-storeRefresh";

    /**
     * 购买PVP商店道具.
     */
    String PVP_BUY_COMMODITY = "pvp-buyCommodity";

    /**
     * 邀请连线PVP.
     */
    String OPVP_INVITE = "op-invite";

    /**
     * 被邀请连线PVP.
     */
    String OPVP_INVITED = "opvp-invited";

    /**
     * 接受连线PVP.
     */
    String OPVP_ACCEPT = "opvp-accept";

    /**
     * 被接收的连线PVP.
     */
    String OPVP_ACCEPTED = "opvp-accepted";

    /**
     * 拒绝连线PVP.
     */
    String OPVP_REJECT = "opvp-reject";

    /**
     * 被拒绝的连线PVP请求.
     */
    String OPVP_REJECTED = "opvp-rejected";

    /**
     * 准备连线PVP.
     */
    String OPVP_PREPARE = "opvp-prepare";

    /**
     * 连线对战(PVP)对手信息.
     */
    String OPVP_RIVAL = "opvp-rival";

    /**
     * 准备好的一方可开始攻击.
     */
    String OPVP_READY = "opvp-ready";

    /**
     * 连线PVP攻击.
     */
    String OPVP_ATTACK = "opvp-attack";

    /**
     * 战斗结果.
     */
    String OPVP_EFFECT = "opvp-effect";

    /**
     * 金币抽卡.
     */
    String HERO_LUCK_DRAW_C = "hero-ld-c";

    /**
     * 金币10连抽.
     */
    String HERO_LUCK_DRAW_C10 = "hero-ld-c10";

    /**
     * 钻石抽卡.
     */
    String HERO_LUCK_DRAW_D = "hero-ld-d";

    /**
     * 钻石10连抽.
     */
    String HERO_LUCK_DRAW_D10 = "hero-ld-d10";

    /**
     * 土豪抽卡.
     */
    String HERO_LUCK_DRAW_TUHAO = "hero-ld-tuhao";

    /**
     * 添加好友.
     */
    String FRIEND_ADD = "friend-add";

    /**
     * 接受.
     */
    String FRIEND_ACCEPT = "friend-accept";

    /**
     * 查询玩家列表.
     */
    String FRIEND_FIND = "friend-find";

    /**
     * 装备合成.
     */
    String EQUIP_MINGLE = "equip-mingle";
    /**
     * 拒绝加好友.
     */
    String FRIEND_REJECT = "friend-reject";
    /**
     * 删除好友.
     */
    String FRIEND_DELETE = "friend-delete";
    /**
     * 查看对方武将属性.
     */
    String SHOW_HEROLIST = "show-herolist";

    /**
     * 商店刷新.
     */
    String STORE_REFRESH = "store-refresh";
    /**
     * 购买物品.
     */
    String BUY_COMMODITY = "buy-commodity";
    /**
     * 在“地下集市”购买商品.
     */
    String BUY_COMMODITY4M = "buy-commodity4m";
    /**
     * 在"西域商人"购买商品.
     */
    String BUY_COMMODITY4W = "buy-commodity4w";
    /**
     * 刷新"地下集市"商品.
     */
    String REFRESH_MARKET = "refresh-market";
    /**
     * 刷新"西域商人"商品.
     */
    String REFRESH_WESTERN = "refresh-western";
    /**
     * 道具使用.
     */
    String ITEM_APPLY = "item-apply";
    /**
     * 批量使用道具.
     */
    String ITEM_APPLIES = "item-applies";
    /**
     * 道具出售.
     */
    String ITEM_SELL = "item-sell";
    /**
     * 批量出售道具.
     */
    String ITEM_SELLS = "item-sells";
    /**
     * 战位强化.
     */
    String TROOP_STRENG = "troop-streng";

    /**
     * 锦囊强化.
     */
    String TROOP_HARDEN = "troop-harden";

    /**
     * 锦囊分解.
     */
    String TROOP_RESOLVE = "troop-resolve";

    /**
     * 锦囊装载.
     */
    String TROOP_EQUIP = "troop-equip";

    /**
     * 锦囊卸载.
     */
    String TROOP_UNEQUIP = "troop-unequip";

    /**
     * 道具附魔.
     */
    String HERO_ENCHEM = "hero-enchem";
    /**
     * 道具附魔.
     */
    String ENCHEM_ONEC = "enchem-onec";

    /**
     * 签到.
     */
    String ROLE_SIGN = "role-sign";
    /**
     * 补签.
     */
    String SIGN_AGAIN = "sign-again";
    /**
     * 随机取名字.
     */
    String RANDOM_NAME = "random-name";
    /**
     * 扫荡.
     */
    String PVE_CLEAN = "pve-clean";
    /**
     * 获取对手/好友武将信息.
     */
    String HERO_PROPERTIES = "hero-properties";

    // mail
    /**
     * 提取邮件道具.
     */
    String E_MAIL_APPENDIX = "e-mail-appendix";

    /**
     * 更新邮件状态.
     */
    String U_MAIL_STATE = "u-mail-state";

    /**
     * 聊天消息.
     */
    String CHAT_MSG = "chat-msg";

    /**
     * pve普通排行榜.
     */
    String PVE_RANKING_LIST = "pve-rankingList";

    /**
     * 等级排行榜.
     */
    String LEVEL_RANKING_LIST = "level-rankingList";

    /**
     * 攻击力排行榜.
     */
    String POWERGUESS_RANKING_LIST = "powerGuess-rankingList";

    /**
     * 武将星星排行榜.
     */
    String HEROSTAR_RANKING_LIST = "heroStar-rankingList";

    /**
     * PVE精英进度排行榜.
     */
    String PVE_DIFFICULT_RANKING_LIST = "pve-difficult-rankingList";

    /**
     * 公告信息.
     */
    String NOTICE = "notice";

    /**
     * 龙脉开始.
     */
    String DARGON_LIST = "dargon-list";

    /**
     * 开始闯龙脉.
     */
    String DARGON_START = "dargon-start";

    /**
     * 重置.
     */
    String DARGON_RESET = "dargon-reset";

    /**
     * 回到原来位置.
     */
    String DARGON_AGAIN = "dargon-again";

    /**
     * 龙脉商店刷新.
     */
    String DARGON_REFRESH = "dargon-refresh";

    /**
     * 龙脉商店购买.
     */
    String DARGON_BUY = "dargon-buy";

    /**
     * 战斗成功.
     */
    String DARGON_WAR_RESULT = "dargon-war-result";

    /**
     * 购买龙脉次数.
     */
    String DARGON_BUY_COUNTS = "dargon-buy-counts";

    /**
     * 每日任务.
     */
    String TASK_DAILY = "task-daily";

    /**
     * 定点领取体力.
     */
    String TASK_ACCESSP_VIGOR = "task-access-vigor";

    /**
     * 创建公会.
     */
    String SOCIETY_CREATE = "society-create";

    /**
     * 公会解散.
     */
    String SOCIETY_DISSOLVE = "society-dissolve";

    /**
     * 加载公会列表.
     */
    String SOCIETY_LOAD_LIST = "society-loadList";

    /**
     * 接受他人的入会请求.
     */
    String SOCIETY_ACCEPT = "society-accept";

    /**
     * 拒绝他人的入会请求.
     */
    String SOCIETY_REJECT = "society-reject";

    /**
     * 申请加入公会.
     */
    String SOCIETY_APPLY = "society-apply";

    /**
     * 把公会成员踢出公会.
     */
    String SOCIETY_KICKOUT = "society-kickout";

    /**
     * 离开公会.
     */
    String SOCIETY_LEAVE = "society-leave";

    /**
     * 加载公会信息.
     */
    String SOCIETY_LOAD = "society-load";

    /**
     * 更新公会信息.
     */
    String SOCIETY_UPDATE_INFO = "society-updateInfo";

    /**
     * 更新公会成员权限.
     */
    String SOCIETY_UPDATE_PERM = "society-updatePerm";

    /**
     * 等级任务.
     */
    String TASK_ROLELEVEL = "task-roleLevel";

    /**
     * 收集英雄任务.
     */
    String TASK_HEROCOUNTS = "task-heroCounts";

    /**
     * 竞技场胜利任务.
     */
    String TASK_PVPWINCOUNTS = "task-pvpWinsCounts";

    /**
     * pveCombo任务.
     */
    String TASK_WARCOMBO = "task-warCombo";

    /**
     * 龙脉单次获得金币.
     */
    String TASK_DARGONMONEY = "task-dargonMoney";

    /**
     * 客户端传Combo.
     */
    String TASK_COMBOVALUER = "task-comboValue";

    /**
     * 重置副本.
     */
    String PVE_RESETCOUNT = "pve-resetCount";

    /**
     * PVE任务.
     */
    String TASK_PVE = "task-pve";

    /**
     * 摇钱树.
     */
    String DIAMOND_EXCHANGE_GOLD = "diamond-exchange-gold";

    /**
     * 一次性领取成就奖励.
     */
    String PVE_CLEAN_REC_AWARD = "pve-clean-rec-award";

    /**
     * 人物的基础属性.
     */
    String ROLE_BASE = "role-base";

    /**
     * 魂匣.
     */
    String SOUL_CARTRIDGE = "soul-cartridge";

    /**
     * 援军领取体力.
     */
    String AID_RECEIVE_VIGOR = "aid-receive-vigor";

    /**
     * 一键穿装备.
     */
    String HERO_RABBET_ALL = "hero-rabbet-all";

    /**
     * 领取新手七天礼包.
     */
    String ACCESS_LOGIN_GIFT = "access-login-gift";

    /**
     * CDK礼包.
     */
    String CDKEY_CASH_GIFT = "cdkey-cash-gift";

    /**
     * 对方在不在线.
     */
    String CHECK_ONLINE = "check-online";

    /**
     * 删除好友请求.
     */
    String FRIEND_DELETE_REQUEST = "friend-delete-request";

    /**
     * 图鉴领取奖励.
     */
    String RECEIVE_ATLAS_REWARD = "receive-atlas-reward";

    /**
     * 星级任务奖励.
     */
    String TASK_PVESTAR = "task-pvestar";

    /**
     * 月卡.
     */
    String RECEIVE_MONTH_CARD = "receive-month-card";

    /**
     * 冲级奖励.
     */
    String RECEIVE_UPGRADE_GIFT = "receive-upgrade-gift";

    /**
     * 首充礼包.
     */
    String FIRST_VIPCHARFGE_GIFT = "first-vipcharge-gift";

    /**
     * Boss许愿.
     */
    String SOCIETY_BOSS_WISH = "society-boss-wish";

    /**
     * 购买成长基金.
     */
    String BUY_GROWTH_FUND = "buy-growth-fund";

    /**
     * 成长基金.
     */
    String GROWTH_FUND = "growth-fund";

    /**
     * 工会商店刷新.
     */
    String SOCIETY_STORE_REFRESH = "society-store-refresh";

    /**
     * 工会商店购买.
     */
    String SOCIETY_STORE_BUY = "society-store-buy";

    /**
     * 邀请码兑换.
     */
    String EXCHANGE_INVITATION_CODE = "exchange-invitation-code";

    /**
     * 领取邀请码奖品.
     */
    String RECEIVE_INVITATION_REWARDE = "receive-invitation-reward";

    /**
     * 领取邀请好友的奖励.
     */
    String RECEIVE_INVITATION_USERS = "receive-invitation-users";

    /**
     * 获取公会boss信息.
     */
    String GETTING_WISH_INFORMATION = "getting-wish-information";

    /**
     * 获取工会Boss排行榜.
     */
    String GETTING_SOCIETY_BOSSRANKING = "getting-society-bossranking";

    /**
     * 公会boss战斗返回伤害.
     */
    String SOCIETY_BOSS_ATK = "society-boss-atk";

    /**
     * 领取公会排名奖励.
     */
    String RECEIVE_SOCIETY_BOSSREWARD = "receive-society-bossreward";

    /**
     * 充值抽奖.
     */
    String TOPUP_LUCKEY_DRAW = "topup-luckey-draw";

    /**
     * 商人信息刷新.
     */
    String CARAVAN_REFRESH = "caravan-refresh";

    /**
     * 派遣商队.
     */
    String CARAVAN_WAYGOING = "caravan-waygoing";

    /**
     * 商队召回.
     */
    String CARAVAN_RECALL = "caravan-recall";

    /**
     * 专属武器装载.
     */
    String EXCLUSIVE_RABBET = "exclusive-rabbet";

    /**
     * 专属武器附魔.
     */
    String EXCLUSIVE_ENCHEM = "exclusive_enchem";

    /**
     * 专属武器一次洗练.
     */
    String EXCLUSIVE_ENCHEM_ONCE = "exclusive-enchem-once";

    /**
     * 专属武器打造.
     */
    String EXCLUSIVE_BUILD = "exclusive-build";

    /**
     * 专属武器洗练.
     */
    String EXCLUSIVE_ENHANCEMENT = "exclusive-enhancement";


    /**
     * 专属武器强化.
     */
    String EXCLUSIVE_STRENG = "exclusive-streng";

    /**
     * 专属武器洗练保存.
     */
    String EXCLUSIVE_ENHANCEMENT_SAVE = "exclusive-enhancement-save";

    /**
     * Vip购买礼包.
     */
    String VIP_RECEIVE = "vip-receive";

    /**
     * 一天内充值领取礼包.
     */
    String VIP_RECEIVE_DAY = "vip-receive-day";

    /**
     * 活动期间内充值领取礼包.
     */
    String VIP_RECEIVE_ACTIVITY = "vip-receive-activity";

    /**
     * 打开随机宝箱.
     */
    String OPEN_BOX = "open-box";

    /**
     * Vip免费领取礼包.
     */
    String VIP_FREE_GIFT = "vip-free-gift";

    /**
     * 印花兑换.
     */
    String STAMP_EXCHANGE = "stamp-exchange";

    /**
     * 印花购买.
     */
    String STAMP_BUY = "stamp-buy";

    /**
     * 重置挑战BossCD时间.
     */
    String RESET_PVEBOSSCD = "reset-pveBossCD";

    /**
     * 积分墙.
     */
    String INTEGRAL_REFRESH = "integral-refresh";
}
