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
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
public class PacketError extends Packet {

    private int code;
    private String text;

    @Override
    public String getNs() {
        return "error";
    }

    @Override
    public void setNs(String ns) {
        // nothing
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @param cond
     * @return
     */
    public static PacketError createError(Condition cond) {
        PacketError result = new PacketError();
        result.setNs(Namespaces.ERROR);
        result.setCode(cond.getCode());
        return result;
    }

    /**
     *
     * @param packet
     * @param cond
     * @return
     */
    public static PacketError createResult(Packet packet, Condition cond) {
        PacketError result = createError(cond);
        result.assignIdAndType(packet, result);
        return result;
    }

    public enum Condition {

        bat_request(400),
        not_authorized(401),
        payment_required(402),
        forbidden(403),
        item_not_found(404),
        not_allowed(405),
        not_acceptable(406),
        registration_required(407),
        not_enabled_role(408),
        conflict(409),
        level_limit(410),
        vigor_not_enough(411),
        pvp_ranking_changed(412),
        size_limit(413),
        not_online(414),
        time_platform_over(418),
        batch_error(419),
        cdkey_error(420),
        time_over(421),
        receive_over(423),
        no_exist(424),
        
        /**
         * 无法连接到对方对战.
         */
        opvp_unable_connect(415),
        /**
         * 非法的公会名称.
         */
        society_name_illegal(416),
        /**
         * 用户在其它终端上线.
         */
        other_online(417),
        internal_server_error(500),
        feature_not_implemented(501),
        service_unavailable(503);
        
        private final int code;

        Condition(int c) {
            code = c;
        }

        public int getCode() {
            return code;
        }
    }
}
