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
package org.skfiy.typhon.domain.item;

import org.skfiy.typhon.script.Script;

/**
 * 可战斗对象.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface IFightItem {

    /**
     *
     * @return
     */
    String getId();

    /**
     * 战斗对象的种族.
     *
     * @return
     */
    Race getRace();

    /**
     * 拉霸选项.
     *
     * @return
     */
    public Shot[] getShots();

    int getAtk();

    int getDef();
    
    int getMatk();
    
    int getMdef();

    int getHp();

    short getDefaultFury();
    
    short getMaxFury();

    int getCritRate();

    int getDecritRate();

    int getCritMagn();

    int getParryRate();

    int getDeparryRate();

    int getParryValue();

    Script getAskill();

    Script getPskill();

    /**
     * 英雄可用的拉霸选项.
     */
    public enum Shot {

        /**
         * 计策.
         */
        JCe("war.JCeComboScript"),
        /**
         * 必杀技.
         */
        BSa("war.BSaComboScript"),
        /**
         * 防御.
         */
        FYu("war.FYuComboScript"),
        /**
         * 援护.
         */
        YHu("war.YHuComboScript"),
        /**
         * 奇袭.
         */
        QXi("war.QXiComboScript"),
        /**
         * 攻击.
         */
        GJi("war.GJiComboScript"),
        /**
         * 7.
         */
        Q7("war.Q7ComboScript"),
        /**
         * Miss.
         */
        Miss("Miss"),
        /**
         * 没有任何Combo.
         */
        None("None");

        Shot(String scriptName) {
            this.scriptName = scriptName;
        }

        private final String scriptName;

        /**
         *
         * @return
         */
        public String getScriptName() {
            return scriptName;
        }

        /**
         * 判断Shot
         *
         * @param s
         * @return
         */
        public Shot equals(Shot s) {
            if (this == None || s == None) {
                return null;
            }
            if (this == Q7 && s != Miss) {
                return s;
            }
            if (s == Q7 && this != Miss) {
                return this;
            }
            return (this == s) ? s : null;
        }
    }
}
