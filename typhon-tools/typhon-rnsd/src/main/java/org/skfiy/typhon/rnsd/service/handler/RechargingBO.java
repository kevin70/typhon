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

import org.skfiy.typhon.rnsd.domain.Recharging;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class RechargingBO {

    private final Recharging recharging;
    private final String result;

    public RechargingBO(Recharging recharging, String result) {
        this.recharging = recharging;
        this.result = result;
    }

    public Recharging getRecharging() {
        return recharging;
    }

    public String getResult() {
        return result;
    }

}
