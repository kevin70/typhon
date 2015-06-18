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
package org.skfiy.typhon.rnsd.service.impl;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.skfiy.typhon.rnsd.domain.OS;
import org.skfiy.typhon.rnsd.domain.Recharging;
import org.skfiy.typhon.rnsd.domain.Region;
import org.skfiy.typhon.rnsd.domain.Zucks;
import org.skfiy.typhon.rnsd.repository.RechargingRepository;
import org.skfiy.typhon.rnsd.service.RechargingException;
import org.skfiy.typhon.rnsd.service.RechargingService;
import org.skfiy.typhon.rnsd.service.RegionService;
import org.skfiy.typhon.rnsd.service.handler.RechargingBO;
import org.skfiy.typhon.rnsd.service.handler.RechargingHandler;
import org.skfiy.typhon.rnsd.service.handler.TradeValidatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class RechargingServiceImpl implements RechargingService {

    private static final Logger LOG = LoggerFactory.getLogger(RechargingService.class);

    @Resource
    private RegionService regionService;
    @Resource
    private RechargingRepository rechargingRepository;
    @Resource
    private Map<String, RechargingHandler> rechargingHandlers;

    private final String basicAuth;
    // 
    private final HttpClientBuilder HC_BUILDER = HttpClientBuilder.create();
    private final Map<OS, String> zucksKeys = new HashMap<>();

    private RechargingServiceImpl(String basicAuth) {
        this.basicAuth = basicAuth;

        zucksKeys.put(OS.ANDROID, "fa5396f7705881831269de56b921d6eb");
        zucksKeys.put(OS.IOS, "530e936ead86ffc313f2f3cbd8382d76");
    }

    @Transactional
    @Override
    public String recharge(JSONObject json) throws TradeValidatedException {
        RechargingBO rechargingBo = rechargingHandlers.get(json.getString("platform")).handle(json);
        LOG.debug("recharging info: {}", rechargingBo.getRecharging());

        rechargingRepository.save(rechargingBo.getRecharging());

        // 通知游戏服务器
        sendToGameServer(rechargingBo.getRecharging());

        // 保存订单信息
        rechargingBo.getRecharging().setStatus("S");
        return rechargingBo.getResult();
    }

    @Transactional
    @Override
    public void giveZucks(JSONObject json) throws TradeValidatedException {
        Zucks zucks = new Zucks();
        zucks.setOs(OS.valueOf(json.getString("os")));
        zucks.setZid(json.getString("id"));
        zucks.setPoint(json.getIntValue("point"));
        zucks.setUid(json.getString("userId"));

        // validate
        String sha1 = DigestUtils.sha1Hex(zucksKeys.get(zucks.getOs()) + zucks.getUid() + zucks.getPoint() + zucks.getZid());
        if (!json.getString("verify").equals(sha1)) {
            throw new TradeValidatedException(sha1, "verify failed");
        }

        rechargingRepository.save(zucks);
        
        Region region = regionService.loadAll().get(0);

        CloseableHttpClient hc = HC_BUILDER.build();
        HttpHost httpHost = new HttpHost(region.getIp(), region.getJmxPort());

        StringBuilder query = new StringBuilder();
        query.append("/InvokeAction//org.skfiy.typhon.spi%3Aname%3DGMConsoleProvider%2Ctype%3Dorg.skfiy.typhon.spi.GMConsoleProvider/action=pushItem?action=pushItem");
        query.append("&uid%2Bjava.lang.String=").append(zucks.getUid());
        query.append("&iid%2Bjava.lang.String=").append("w036");
        query.append("&count%2Bjava.lang.String=").append(zucks.getPoint());

        HttpGet httpGet = new HttpGet(query.toString());
        httpGet.addHeader("Authorization", basicAuth);

        CloseableHttpResponse response = null;
        try {
            response = hc.execute(httpHost, httpGet);
        } catch (IOException ex) {
            LOG.error("host:port -> {}:{}", region.getIp(), region.getJmxPort(), ex);
            throw new RechargingException("send to game server error error", ex);
        } finally {
            try {
                hc.close();
            } catch (IOException ex) {
            }
        }

        if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
            LOG.error("recharging failed. {}", response);
            throw new RechargingException("recharging failed");
        }
    }

    private void sendToGameServer(Recharging recharging) {
        Region region = regionService.load(Integer.parseInt(recharging.getRegion()));

        CloseableHttpClient hc = HC_BUILDER.build();
        HttpHost httpHost = new HttpHost(region.getIp(), region.getJmxPort());

        StringBuilder query = new StringBuilder();
        query.append("/InvokeAction//org.skfiy.typhon.spi%3Aname%3DGMConsoleProvider%2Ctype%3Dorg.skfiy.typhon.spi.GMConsoleProvider/action=recharge?action=recharge");
        query.append("&rid%2Bjava.lang.String=").append(recharging.getUid());
        query.append("&cash%2Bjava.lang.String=").append(recharging.getGoods());

        HttpGet httpGet = new HttpGet(query.toString());
        httpGet.addHeader("Authorization", basicAuth);

        CloseableHttpResponse response = null;
        try {
            response = hc.execute(httpHost, httpGet);
        } catch (IOException ex) {
            LOG.error("host:port -> {}:{}", region.getIp(), region.getJmxPort(), ex);
            throw new RechargingException("send to game server error error", ex);
        } finally {
            try {
                hc.close();
            } catch (IOException ex) {
            }
        }

        if (response == null || response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
            LOG.error("recharging failed. {}", response);
            throw new RechargingException("recharging failed");
        }
    }

}
