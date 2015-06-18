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
package org.skfiy.typhon.rnsd.service.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.skfiy.typhon.rnsd.domain.OS;
import org.skfiy.typhon.rnsd.domain.Region;
import org.skfiy.typhon.rnsd.repository.RegionRepository;
import org.skfiy.typhon.rnsd.service.RegionDTO;
import org.skfiy.typhon.rnsd.service.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Service
public class RegionServiceImpl implements RegionService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionService.class);

    private final List<Region> REGIONS = new ArrayList<>();

    @Resource
    private RegionRepository regionRepository;

    @Override
    public Region load(int rid) {
        for (Region region : loadAll()) {
            if (region.getRid() == rid) {
                return region;
            }
        }

        // FIXME 待处理
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Region> loadAll() {
        if (REGIONS.isEmpty()) {
            REGIONS.addAll(regionRepository.loadAll());
        }

        return REGIONS;
    }

    @Override
    public List<RegionDTO> loadByOS(OS os) {
        long currentTime = System.currentTimeMillis();

        List<RegionDTO> results = new ArrayList<>();
        for (Region region : loadAll()) {
            if (region.getOpenningTime() <= currentTime
                    && (region.getOs() == null || region.getOs() == os)) {
                results.add(new RegionDTO(region));
            }
        }
        return results;
    }

}
