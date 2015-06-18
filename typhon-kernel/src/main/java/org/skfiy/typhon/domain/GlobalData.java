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
package org.skfiy.typhon.domain;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class GlobalData {

    private Type type;
    private String data;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("type=").append(type);
        sb.append(", data=").append(data);
        return sb.toString();
    }

    /**
     *
     */
    public enum Type {

        server_settings,
        pvp_data,
        pvp_ranking_data,
        pve_data,
        level_data,
        powerGuess_data,
        society_data,
        pve_difficult_data,
        heroStar_data
    }
}
