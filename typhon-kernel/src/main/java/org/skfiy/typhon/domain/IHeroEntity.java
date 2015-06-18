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
import org.skfiy.typhon.domain.item.Item;
import org.skfiy.typhon.domain.item.Race;

/**
 * 武将实体对象.
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface IHeroEntity {

    /**
     * 武将ID.
     * 
     * @return ID
     */
    String getId();

    /**
     * 武将等级.
     * 
     * @return 等级
     */
    int getLevel();

    /**
     * 武将等阶.
     * 
     * @return 等阶
     */
    int getLadder();

    /**
     * 武将经验值.
     * 
     * @return 经验值
     */
    int getExp();

    /**
     * 武将战斗力.
     * 
     * @return 战斗力
     */
    int getPowerGuess();

    /**
     * 武将已经穿戴的装备.
     * 
     * @return 已经穿戴装备列表
     */
    List<Rabbet> getRabbets();

    /**
     * 额外的"统"属性.
     * 
     * @return 统
     */
    int getExtraTong();

    /**
     * 额外的"武"属性.
     * 
     * @return 武
     */
    int getExtraWu();

    /**
     * 额外的"智"属性.
     * 
     * @return 智
     */
    int getExtraZhi();

    /**
     * 额外的"攻击力"属性.
     * 
     * @return 攻击力
     */
    int getExtraAtk();

    /**
     * 额外的"防御力"属性.
     * 
     * @return 防御力
     */
    int getExtraDef();

    /**
     * 额外的"魔法攻击力"属性.
     * 
     * @return 魔法攻击力
     */
    int getExtraMatk();

    /**
     * 额外的"魔法防御力"属性.
     * 
     * @return 魔法防御力
     */
    int getExtraMdef();

    /**
     * 额外的"血量"属性.
     * 
     * @return 血量
     */
    int getExtraHp();

    /**
     * 额外的"暴击"属性.
     * 
     * @return 暴击
     */
    int getExtraCritRate();

    /**
     * 额外的"韧性"属性.
     * 
     * @return 韧性
     */
    int getExtraDecritRate();

    /**
     * 额外的"暴伤"属性.
     * 
     * @return 暴伤
     */
    int getExtraCritMagn();

    /**
     * 额外的"格挡"属性.
     * 
     * @return 格挡
     */
    int getExtraParryRate();

    /**
     * 额外的"穿透"属性.
     * 
     * @return 穿透
     */
    int getExtraDeparryRate();

    /**
     * 额外的"免伤值"属性.
     * 
     * @return 免伤值
     */
    int getExtraParryValue();

    /**
     * 
     * @return
     */
    Race getRace();

    /**
     * 
     * @return
     */
    Item.Star getStar();

    /**
     * 专属武器初始+洗练
     * 
     * @return
     */
    Rabbet getWeaponsRabbets();

    /**
     * 专属武器打造
     * 
     * @return
     */
    Rabbet getWeaponsBuild();

    /**
     * 专属武器附魔
     * 
     * @return
     */
    Rabbet getWeaponsEnchant();

    /**
     * 专属武器强化
     * 
     * @return
     */
    Rabbet getWeaponsStreng();

    /**
     *
     */
    class Rabbet extends AbstractIndexable {

        private int point;

        private int level;
        private int exp;
        private int expSum;
        private int tong;
        private int wu;
        private int zhi;

        private int atk;
        private int def;
        private int matk;
        private int mdef;
        private int hp;

        private int critRate;
        private int critMagn;
        private int decritRate;
        private int parryRate;
        private int parryValue;
        private int deparryRate;

        public int getPoint() {
            return point;
        }

        public void setPoint(int point) {
            this.point = point;
        }

        /**
         * 装备附魔等级.
         * 
         * @return 等级
         */
        public int getLevel() {
            return level;
        }

        /**
         * 装备附魔等级.
         * 
         * @param level 等级
         */
        public void setLevel(int level) {
            this.level = level;
        }

        /**
         * 装备附魔当前经验.
         * 
         * @return 当前经验.
         */
        public int getExp() {
            return exp;
        }

        /**
         * 装备附魔当前经验.
         * 
         * @param exp 当前经验.
         */
        public void setExp(int exp) {
            this.exp = exp;
        }

        /**
         * 装备附魔总经验.
         * 
         * @return 总经验
         */
        public int getExpSum() {
            return expSum;
        }

        /**
         * 装备附魔总经验.
         * 
         * @param expSum 总经验
         */
        public void setExpSum(int expSum) {
            this.expSum = expSum;
        }

        /**
         * 装备附魔的"统"属性.
         * 
         * @return 统
         */
        public int getTong() {
            return tong;
        }

        /**
         * 装备附魔的"统"属性.
         * 
         * @param tong 统
         */
        public void setTong(int tong) {
            this.tong = tong;
        }

        /**
         * 装备附魔的"武"属性.
         * 
         * @return 武
         */
        public int getWu() {
            return wu;
        }

        /**
         * 装备附魔的"武"属性.
         * 
         * @param wu 武
         */
        public void setWu(int wu) {
            this.wu = wu;
        }

        /**
         * 装备附魔的"智"属性.
         * 
         * @return 智
         */
        public int getZhi() {
            return zhi;
        }

        /**
         * 装备附魔的"智"属性.
         * 
         * @param zhi 智
         */
        public void setZhi(int zhi) {
            this.zhi = zhi;
        }

        /**
         * 装备附魔的"攻击力"属性.
         * 
         * @return 攻击力
         */
        public int getAtk() {
            return atk;
        }

        /**
         * 装备附魔的"攻击力"属性.
         * 
         * @param atk 攻击力
         */
        public void setAtk(int atk) {
            this.atk = atk;
        }

        /**
         * 装备附魔的"防御力"属性.
         * 
         * @return 防御力
         */
        public int getDef() {
            return def;
        }

        /**
         * 装备附魔的"防御力"属性.
         * 
         * @param def 防御力
         */
        public void setDef(int def) {
            this.def = def;
        }

        /**
         * 装备附魔的"魔法攻击力"属性.
         * 
         * @return 魔法攻击力
         */
        public int getMatk() {
            return matk;
        }

        /**
         * 装备附魔的"魔法攻击力"属性.
         * 
         * @param matk 魔法攻击力
         */
        public void setMatk(int matk) {
            this.matk = matk;
        }

        /**
         * 装备附魔的"魔法防御力"属性.
         * 
         * @return 魔法防御力
         */
        public int getMdef() {
            return mdef;
        }

        /**
         * 装备附魔的"魔法防御力"属性.
         * 
         * @param mdef 魔法防御力
         */
        public void setMdef(int mdef) {
            this.mdef = mdef;
        }

        /**
         * 装备附魔的"血量"属性.
         * 
         * @return 血量
         */
        public int getHp() {
            return hp;
        }

        /**
         * 装备附魔的"血量"属性.
         * 
         * @param hp 血量
         */
        public void setHp(int hp) {
            this.hp = hp;
        }

        /**
         * 装备附魔的"暴击"属性.
         * 
         * @return 暴击
         */
        public int getCritRate() {
            return critRate;
        }

        /**
         * 装备附魔的"暴击"属性.
         * 
         * @param critRate 暴击
         */
        public void setCritRate(int critRate) {
            this.critRate = critRate;
        }

        /**
         * 装备附魔的"暴伤"属性.
         * 
         * @return 暴伤
         */
        public int getCritMagn() {
            return critMagn;
        }

        /**
         * 装备附魔的"暴伤"属性.
         * 
         * @param critMagn 暴伤
         */
        public void setCritMagn(int critMagn) {
            this.critMagn = critMagn;
        }

        /**
         * 装备附魔的"韧性"属性.
         * 
         * @return 韧性
         */
        public int getDecritRate() {
            return decritRate;
        }

        /**
         * 装备附魔的"韧性"属性.
         * 
         * @param decritRate 韧性
         */
        public void setDecritRate(int decritRate) {
            this.decritRate = decritRate;
        }

        /**
         * 装备附魔的"格挡"属性.
         * 
         * @return 格挡
         */
        public int getParryRate() {
            return parryRate;
        }

        /**
         * 装备附魔的"格挡"属性.
         * 
         * @param parryRate 格挡
         */
        public void setParryRate(int parryRate) {
            this.parryRate = parryRate;
        }

        /**
         * 装备附魔的"免伤值"属性.
         * 
         * @return 免伤值
         */
        public int getParryValue() {
            return parryValue;
        }

        /**
         * 装备附魔的"免伤值"属性.
         * 
         * @param parryValue 免伤值
         */
        public void setParryValue(int parryValue) {
            this.parryValue = parryValue;
        }

        /**
         * 装备附魔的"穿透"属性.
         * 
         * @return 穿透
         */
        public int getDeparryRate() {
            return deparryRate;
        }

        /**
         * 装备附魔的"穿透"属性.
         * 
         * @param deparryRate 穿透
         */
        public void setDeparryRate(int deparryRate) {
            this.deparryRate = deparryRate;
        }

    }

}
