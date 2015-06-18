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
package org.skfiy.typhon.spi;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Vip {

    public final int storedMoney;
    public final Privileged privileged;

    Vip() {
        throw new AssertionError("No access");
    }

    Vip(JSONObject json) {
        storedMoney = json.getIntValue("storedMoney");
        privileged = new Privileged(json.getJSONObject("privileged"));
    }

    public static class Privileged {

        /**
         * 每日购买体力的次数.
         */
        public final int buy_vigor_count;
        /**
         * 好友个数上限.
         */
        public final int max_friend_limit;
        /**
         * 每日添加活动的次数.
         */
        public final int activity_count;
        /**
         * 活动奖励倍率.
         */
        public final double activity_award_magn;
        /**
         * 每日PVE购买的刷新次数.
         */
        public final int max_pve_buy_count;
        /**
         * 每日龙脉的参与次数.
         */
        public final int max_dargon_count;
        /**
         * 每日PVP购买的次数.
         */
        public final int max_pvp_buy_count;
        /**
         * 是否开启一键附魔.
         */
        public final boolean one_key_enchem_enabled;
        /**
         * 常驻"地下集市"商店.
         */
        public final boolean market_store_enabled;
        /**
         * 常驻"西域商人"商店.
         */
        public final boolean western_store_enabled;
        /**
         * pve扫荡次数.
         */
        public final int pve_sweep_counts;
        /**
         * 摇钱树.
         */
        public final int diamond_exchange_gold_counts;
        /**
         * 一键传装备.
         */
        public final boolean one_key_equipment;
        /**
         * 成长基金购买.
         */
        public final boolean buy_growth_fun;
        /**
         * 公会Boss许愿次数
         */
        public final int society_wish_counts;
        /**
         * 公会Boss许愿次数
         */
        public final int society_atkBoss_counts;
        /**
         * 商队派遣队伍数量
         */
        public final int explore_count;
        Privileged() {
            throw new AssertionError("No access");
        }

        Privileged(JSONObject json) {
            buy_vigor_count = json.getIntValue("buy_vigor_count");
            max_friend_limit = json.getIntValue("max_friend_limit");
            activity_count = json.getIntValue("activity_count");
            activity_award_magn = json.getDoubleValue("activity_award_magn");
            max_pve_buy_count = json.getIntValue("max_pve_buy_count");
            max_dargon_count = json.getIntValue("max_dargon_count");
            max_pvp_buy_count = json.getIntValue("max_pvp_buy_count");
            one_key_enchem_enabled = json.getBooleanValue("one_key_enchem_enabled");
            market_store_enabled = json.getBooleanValue("market_store_enabled");
            western_store_enabled = json.getBooleanValue("western_store_enabled");
            pve_sweep_counts = json.getIntValue("pve_sweep_counts");
            diamond_exchange_gold_counts = json.getIntValue("diamond_exchange_gold_counts");
            one_key_equipment = json.getBooleanValue("one_key_equipment");
            buy_growth_fun = json.getBooleanValue("buy_growth_fun");
            society_wish_counts = json.getIntValue("society_wish_counts");
            society_atkBoss_counts = json.getIntValue("society_atkBoss_counts");
            explore_count = json.getIntValue("explore_count");
        }
    }
}
