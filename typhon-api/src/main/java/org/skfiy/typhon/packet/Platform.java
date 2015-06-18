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
public enum Platform {

    /**
     * 无平台.
     */
    none(""),
    /**
     * 360平台.
     */
    qihoo("360"),
    /**
     * 应用宝.
     */
    yyb("yyb"),
    /**
     * 4399平台.
     */
    four399("4399"),
    /**
     * UC平台.
     */
    uc("uc"),
    /**
     * LJ平台.
     */
    lj("lj"),
    /**
     * 当乐平台.
     */
    dangle("dangle"),
    /**
     * 小米平台.
     */
    xiaomi("xiaomi"),
    /**
     * 机锋平台.
     */
    gfan("gfan"),
    /**
     * 安智平台.
     */
    anzhi("anzhi"),
    /**
     * 豌豆荚平台.
     */
    wandoujia("wandoujia"),
    /**
     * 百度平台.
     */
    baidu("baidu"),
    /**
     * 联想平台.
     */
    lenovo("lenovo"),
    /**
     * 魅族游戏中心.
     */
    meizu("meizu"),
    /**
     * VIVO平台.
     */
    vivo("vivo"),
    /**
     * OPPO平台.
     */
    oppo("oppo"),
    /**
     * 卓易平台.
     */
    zhuoyi("zhuoyi"),
    /**
     * 华为平台.
     */
    huawei("huawei"),
    /**
     * 拇指玩平台.
     */
    muzhiwan("muzhiwan");

    private final String label;

    Platform(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
