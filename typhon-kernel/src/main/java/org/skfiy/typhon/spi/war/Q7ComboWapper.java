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
package org.skfiy.typhon.spi.war;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Q7ComboWapper {

    private final WarInfo warInfo;
    private final WarInfo.Entity attackerEntity;
    private final WarCombo warCombo;

    public Q7ComboWapper(WarInfo warInfo, WarInfo.Entity attackerEntity, WarCombo warCombo) {
        this.warInfo = warInfo;
        this.attackerEntity = attackerEntity;
        this.warCombo = warCombo;
    }

    public WarInfo getWarInfo() {
        return warInfo;
    }

    public WarInfo.Entity getAttackerEntity() {
        return attackerEntity;
    }

    public WarCombo getWarCombo() {
        return warCombo;
    }

}
