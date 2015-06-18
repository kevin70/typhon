/*
 * Copyright 2013 The Skfiy Open Association.
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
package org.skfiy.typhon.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class RoleData {

    private int rid;
    private String normalData;
    private String bagData;
    private String heroEntitiesData;
    private String invisibleData;
    private String vacantData;

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getNormalData() {
        return normalData;
    }

    public void setNormalData(String normalData) {
        this.normalData = normalData;
    }

    public String getBagData() {
        return bagData;
    }

    public void setBagData(String bagData) {
        this.bagData = bagData;
    }

    public String getHeroBagData() {
        return heroEntitiesData;
    }

    public void setHeroBagData(String heroEntitiesData) {
        this.heroEntitiesData = heroEntitiesData;
    }

    public String getInvisibleData() {
        return invisibleData;
    }

    public void setInvisibleData(String invisibleData) {
        this.invisibleData = invisibleData;
    }

    public String getVacantData() {
        return vacantData;
    }

    public void setVacantData(String vacantData) {
        this.vacantData = vacantData;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this);
        tsb.append("rid", rid).append("\n");
        tsb.append("normalData", normalData).append("\n");
        tsb.append("bagData", bagData).append("\n");
        tsb.append("heroEntitiesData", heroEntitiesData).append("\n");
        tsb.append("invisibleData", invisibleData).append("\n");
        tsb.append("vacantData", vacantData).append("\n");
        return tsb.toString();
    }
}
