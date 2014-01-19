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
        conflict(409),
        feature_not_implemented(501),
        forbidden(403),
        internal_server_error(500),
        item_not_found(404),
        not_acceptable(406),
        not_allowed(405),
        not_authorized(401),
        payment_required(402),
        registration_required(407),
        not_enabled_role(408),
        service_unavailable(503);
        
        private int code;

        Condition(int c) {
            code = c;
        }

        public int getCode() {
            return code;
        }
    }
}
