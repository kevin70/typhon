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
package org.skfiy.typhon.rnsd.web.controller;

import com.alibaba.fastjson.JSON;
import javax.annotation.Resource;
import org.skfiy.typhon.rnsd.domain.OS;
import org.skfiy.typhon.rnsd.service.RegionService;
import org.skfiy.typhon.rnsd.web.MediaTypeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Controller
public class RegionController {

    @Resource
    private RegionService regionService;

    @RequestMapping(value = "/findVersion/{os}", produces = MediaTypeUtils.TEXT_PLAIN)
    public @ResponseBody
    String findVersion(@PathVariable OS os) {
        return System.getProperty(os.getVersionKey());
    }

    @RequestMapping(value = "/findRegions/{os}", produces = MediaTypeUtils.APPLICATION_JSON)
    public @ResponseBody
    String findRegions(@PathVariable OS os) {
        return JSON.toJSONString(regionService.loadByOS(os));
    }

}
