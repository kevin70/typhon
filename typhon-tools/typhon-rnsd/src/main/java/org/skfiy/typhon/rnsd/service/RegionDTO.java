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
package org.skfiy.typhon.rnsd.service;

import org.skfiy.typhon.rnsd.domain.Region;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class RegionDTO {

    private int rid;
    private String name;
    private String ip;
    private int port;
    private Region.State state;

    public RegionDTO() {

    }

    public RegionDTO(Region region) {
        this.rid = region.getRid();
        this.name = region.getName();
        this.ip = region.getIp();
        this.port = region.getPort();
        this.state = region.getState();
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Region.State getState() {
        return state;
    }

    public void setState(Region.State state) {
        this.state = state;
    }

}
