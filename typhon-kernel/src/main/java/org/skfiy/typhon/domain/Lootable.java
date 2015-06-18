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
package org.skfiy.typhon.domain;

import java.util.List;

/**
 * 关卡掉落管理对象.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Lootable {

    /**
     * 副本总共攻击次数.
     *
     * @return 总共攻击次数
     */
    int getTotal();

    /**
     * 副本已经掉落的道具集合.
     *
     * @return 掉落的道具集合
     */
    List<Record> getRecords();

    /**
     * 增加副本掉落道具记录.
     *
     * @param record 掉落记录
     * @return true/false
     */
    boolean addRecord(Record record);

    /**
     * 查询已经掉落的记录.
     *
     * @param lid 掉落ID
     * @return 掉落记录
     */
    Record findRecord(String lid);

    /**
     * 掉落的记录对象.
     */
    class Record {

        private String lid;
        private int tally;
        private int lastTime;

        public Record() {
        }

        /**
         *
         * @param lid
         */
        public Record(String lid) {
            this.lid = lid;
        }

        /**
         * 掉落ID.
         *
         * @return 掉落ID
         */
        public String getLid() {
            return lid;
        }

        /**
         * 掉落ID.
         *
         * @param lid 掉落ID
         */
        public void setLid(String lid) {
            this.lid = lid;
        }

        /**
         * 已经掉落的次数.
         *
         * @return 掉落次数
         */
        public int getTally() {
            return tally;
        }

        /**
         * 已经掉落的次数.
         *
         * @param tally 掉落次数
         */
        public void setTally(int tally) {
            this.tally = tally;
        }

        /**
         * 上一次掉落节点.
         *
         * @return 上一次掉落
         */
        public int getLastTime() {
            return lastTime;
        }

        /**
         * 上一次掉落节点.
         *
         * @param lastTime 上一次掉落
         */
        public void setLastTime(int lastTime) {
            this.lastTime = lastTime;
            incrementTally();
        }

        /**
         *
         * @return
         */
        public int incrementTally() {
            return (tally++);
        }
    }

}
