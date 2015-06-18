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

import org.skfiy.typhon.dobj.BSaSkill;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class BSaWapper {

    private final WarInfo.Entity attackerEntity;
    private final WarInfo.Entity defenderEntity;
    private final FightObject aobj;
    private final BSaSkill bsaSkill;
    private final WarInfo warInfo;
    private double factor = 1.0;

    public BSaWapper(WarInfo.Entity attackerEntity, WarInfo.Entity defenderEntity,
            FightObject aobj, BSaSkill bsaSkill, WarInfo warInfo) {
        this.attackerEntity = attackerEntity;
        this.defenderEntity = defenderEntity;
        this.aobj = aobj;
        this.bsaSkill = bsaSkill;
        this.warInfo = warInfo;
    }
    
    public BSaWapper(WarInfo.Entity attackerEntity, WarInfo.Entity defenderEntity,
            FightObject aobj, BSaSkill bsaSkill, WarInfo warInfo, double factor) {
        this.attackerEntity = attackerEntity;
        this.defenderEntity = defenderEntity;
        this.aobj = aobj;
        this.bsaSkill = bsaSkill;
        this.warInfo = warInfo;
        this.factor = factor;
    }

    public WarInfo.Entity getAttackerEntity() {
        return attackerEntity;
    }

    public WarInfo.Entity getDefenderEntity() {
        return defenderEntity;
    }

    public FightObject getAobj() {
        return aobj;
    }

    public BSaSkill getBsaSkill() {
        return bsaSkill;
    }

    public WarInfo getWarInfo() {
        return warInfo;
    }

    public double getFactor() {
        return factor;
    }

}
