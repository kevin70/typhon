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
package org.skfiy.typhon.packet;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class PacketChatMessage {

    public static final int PRIVATE_CHANNEL = 0;
    public static final int WORLD_CHANNEL = -1;
    public static final int GUILD_CHANNEL = -2;

    /**
     *
     */
    public static final class Sending extends Packet {

        private int to;
        private String toName;
        private String msg;

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public String getToName() {
            return toName;
        }

        public void setToName(String toName) {
            this.toName = toName;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    /**
     *
     */
    public static final class Receiving extends Packet {

        private int channel;
        private int from;
        private String name;
        private String msg;
        private String avatar;
        private String avatarBorder;
        private int level;

        public Receiving() {
            setNs(Namespaces.CHAT_MSG);
        }

        public Receiving(int channel, int from, String name, String msg, String avatar, String avatarBorder, int level) {
            this();
            this.channel = channel;
            this.from = from;
            this.name = name;
            this.msg = msg;
            this.avatar = avatar;
            this.avatarBorder = avatarBorder;
            this.level = level;
        }

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getAvatarBorder() {
            return avatarBorder;
        }

        public void setAvatarBorder(String avatarBorder) {
            this.avatarBorder = avatarBorder;
        }
    }

}
