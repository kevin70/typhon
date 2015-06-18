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
package org.skfiy.typhon.spi.war;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface BufferSkill {

    /**
     * 攻击BUF.
     */
    String ATK_BUF = "atk";

    /**
     * 防御BUF.
     */
    String DEF_BUF = "def";

    /**
     * 魔攻BUF.
     */
    String MATK_BUF = "matk";

    /**
     * 魔防BUF.
     */
    String MDEF_BUF = "mdef";

    /**
     * 双攻BUF.
     */
    String ATK_AND_MATK = "atk+matk";

    /**
     * 双防BUF.
     */
    String DEF_AND_MDEF = "def+mdef";

    /**
     * 暴击BUF.
     */
    String CRIT_RATE_BUF = "critRate";

    /**
     * 暴伤BUF.
     */
    String CRIT_MAGN_BUF = "critMagn";

    /**
     * 韧性BUF.
     */
    String DECRIT_RATE_BUF = "decritRate";

    /**
     * 格挡率BUF.
     */
    String PARRY_RATE_BUF = "parryRate";

    /**
     * 免伤值BUF.
     */
    String PARRY_VALUE_BUF = "parryValue";

    /**
     * 穿透率BUF.
     */
    String DEPARRY_RATE_BUF = "deparryRate";

    /**
     * 护盾BUF.
     */
    String SHIELD_BUF = "shield";

    /**
     * 中毒BUF.
     */
    String POISON_BUF = "poison";

    /**
     * BUFF名称.
     *
     * @return BUFF名称
     */
    String getName();

    /**
     *
     * @return
     */
    Type getType();

    /**
     *
     * @return
     */
    Object onBefore();

    /**
     *
     * @return
     */
    Object onAfter();

    /**
     *
     * @return
     */
    Object onFinish();

    // begin
    // end
    /**
     *
     */
    public enum Type {

        BUFF, DEBUFF
    }

}
