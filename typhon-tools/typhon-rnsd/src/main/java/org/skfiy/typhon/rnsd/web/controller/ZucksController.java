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

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import org.skfiy.typhon.rnsd.service.RechargingService;
import org.skfiy.typhon.rnsd.service.handler.TradeValidatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Controller
public class ZucksController {

    private static final Logger LOG = LoggerFactory.getLogger(ZucksController.class);

    @Resource
    private RechargingService rechargingService;

    @RequestMapping("/zucks/{os}")
    public ResponseEntity execute(@PathVariable String os,
            @RequestParam("id") String id,
            @RequestParam("user") String userId,
            @RequestParam("point") String point,
            @RequestParam("verify") String verify) {

        JSONObject json = new JSONObject();
        json.put("os", os);
        json.put("id", id);
        json.put("userId", userId);
        json.put("point", point);
        json.put("verify", verify);

        LOG.debug("{}", json);

        try {
            rechargingService.giveZucks(json);
        } catch (TradeValidatedException ex) {
            LOG.warn("TradeValidatedException: ", ex);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException ex) {
            // ignore
            LOG.warn("DuplicateKeyException: ", ex);
        } catch (Exception ex) {
            LOG.warn("Exception: ", ex);
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

}
