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
package org.skfiy.typhon.rnsd.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.skfiy.typhon.rnsd.service.RechargingService;
import org.skfiy.typhon.rnsd.service.handler.TradeValidatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Controller
public class RechargingController {

    private static final Logger LOG = LoggerFactory.getLogger(RechargingController.class);

    @Resource
    private RechargingService rechargingService;

    @RequestMapping("/recharging/uc")
    public @ResponseBody
    String recharge(NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String content;
        try {
            content = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            LOG.error("read content error.", ex);
            return "error";
        }

        JSONObject json = JSON.parseObject(content);
        json.put("platform", "uc");

        return recharge0(json);
    }

    @RequestMapping(value = "/recharging/{platform}")
    public @ResponseBody
    String recharge(@PathVariable String platform, WebRequest webRequest) {
        JSONObject json = encapsuleParams(webRequest);
        json.put("platform", platform);

        return recharge0(json);
    }

    private String recharge0(JSONObject json) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("recharge by [{}]: {}", json.getString("platform"), json);
        }

        try {
            return rechargingService.recharge(json);
        } catch (TradeValidatedException ex) {
            LOG.error("trade validated. {}", json, ex);
            return ex.getSignal();
        } catch (DuplicateKeyException ex) {
            LOG.warn("{}", json, ex);
            return "success";
        }
    }

    private JSONObject encapsuleParams(WebRequest request) {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (entry.getValue().length > 1) {
                List list = Lists.newArrayList(entry.getValue());
                json.put(entry.getKey(), new JSONArray(list));
            } else {
                json.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        return json;
    }

}
