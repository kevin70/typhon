/*
 * Copyright 2015 The Skfiy Open Association.
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
package org.skfiy.typhon.rnsd.service.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.skfiy.typhon.rnsd.domain.Platform;
import org.skfiy.typhon.rnsd.domain.Recharging;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class UCRechargingHandler implements RechargingHandler {

    private final String APP_KEY = "2e0662d2220d70a1692fa2d724c8a46d";

    @Override
    public RechargingBO handle(JSONObject json) throws TradeValidatedException {
        validate(json);

        JSONObject data = json.getJSONObject("data");
        Recharging recharging = new Recharging();
        recharging.setTradeId(data.getString("orderId"));
        recharging.setPlatform(Platform.uc.getLabel());

        String callbackInfo = StringUtils.newStringUtf8(Base64.decodeBase64(data.getString("callbackInfo")));
        JSONObject extra = JSON.parseObject(callbackInfo);
        recharging.setUid(extra.getString("uid"));
        recharging.setRegion(extra.getString("region"));
        recharging.setGoods(extra.getString("goods"));

        recharging.setAmount(data.getDouble("amount").intValue());
        recharging.setCreationTime(System.currentTimeMillis() / 1000);

        return (new RechargingBO(recharging, "SUCCESS"));
    }

    private void validate(JSONObject json) throws TradeValidatedException {
        JSONObject data = json.getJSONObject("data");

        if ("F".equals(data.getString("orderStatus"))) {
            throw new TradeValidatedException("SUCCESS", "orderStatus is failed");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("accountId=").append(data.getString("accountId"));
        sb.append("amount=").append(data.getString("amount"));
        sb.append("callbackInfo=").append(data.getString("callbackInfo"));
        // sb.append("cpOrderId=").append(toStr(data.getString("cpOrderId")));
        sb.append("creator=").append(data.getString("creator"));
        sb.append("failedDesc=").append(toStr(data.getString("failedDesc")));
        sb.append("gameId=").append(data.getString("gameId"));
        sb.append("orderId=").append(data.getString("orderId"));
        sb.append("orderStatus=").append(data.getString("orderStatus"));
        sb.append("payWay=").append(data.getString("payWay"));
        sb.append(APP_KEY);

        String sign = DigestUtils.md5Hex(sb.toString());
        if ("F".equals(data.getString("orderStatus")) || !sign.equals(json.getString("sign"))) {
            throw new TradeValidatedException("FAILURE", "not validated");
        }
    }

    private String toStr(String str) {
        return str == null ? "" : str;
    }

}
