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
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.skfiy.typhon.rnsd.domain.Platform;
import org.skfiy.typhon.rnsd.domain.Recharging;

/**
 * 棱镜SDK冲值处理器.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class LJRechargingHandler implements RechargingHandler {

    private final String PRODUCT_SECRET = "40914098d0bc47b28fc2cb493bd764eb";

    @Resource
    private Map<String, Integer> goodsPrices;
    
    @Override
    public RechargingBO handle(JSONObject json) throws TradeValidatedException {
        validate(json);

        int price = json.getIntValue("price") / 100;
        
        Recharging recharging = new Recharging();
        recharging.setTradeId(json.getString("orderId"));
        recharging.setPlatform(Platform.lj.getLabel());

        String callbackInfo = StringUtils.newStringUtf8(Base64.decodeBase64(json.getString("callbackInfo")));
        JSONObject extra = JSON.parseObject(callbackInfo);
        
        String goods = extra.getString("goods");
        if (!goodsPrices.containsKey(goods) || price != goodsPrices.get(goods).intValue()) {
            throw new TradeValidatedException("fail", "goods price error");
        }
        
        recharging.setUid(extra.getString("uid"));
        recharging.setRegion(extra.getString("region"));
        recharging.setGoods(goods);

        recharging.setAmount(price);
        recharging.setCreationTime(System.currentTimeMillis() / 1000);
        recharging.setChannel(json.getString("channelLabel"));

        return (new RechargingBO(recharging, "success"));
    }

    private void validate(JSONObject json) throws TradeValidatedException {
        StringBuilder sb = new StringBuilder();
        sb.append(json.getString("orderId"));
        sb.append(json.getString("price"));
        sb.append(json.getString("callbackInfo"));
        sb.append(PRODUCT_SECRET);

        String sign = DigestUtils.md5Hex(sb.toString());
        if (!sign.equals(json.getString("sign"))) {
            throw new TradeValidatedException("fail", "not validated");
        }
    }

}
