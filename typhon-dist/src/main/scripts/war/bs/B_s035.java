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
package war.bs;

import org.skfiy.typhon.dobj.BSaSkill;
import org.skfiy.typhon.spi.war.AttackEntry;
import org.skfiy.typhon.spi.war.BSaScript;
import org.skfiy.typhon.spi.war.BSaWapper;
import org.skfiy.typhon.spi.war.BufferResult;
import org.skfiy.typhon.spi.war.BufferSkill;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.MultiAttackResult;
import org.skfiy.typhon.spi.war.RecoveryEntry;

/**
 * 黄月英, hb09.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s035 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = getAtk(bsaWapper, bsaWapper.getAobj());

        FightObject aobj = bsaWapper.getAobj();
        MultiAttackResult mar = new MultiAttackResult();
        int sumHp = 0;
        
        for (FightObject goal
                : bsaWapper.getDefenderEntity().findFightGoals(bsaSkill.getNumOfTargets())) {
            AttackEntry ae = getWarProvider().attack0(bsaWapper.getWarInfo().getTerrain(), aobj,
                    goal, atk, getDef(bsaWapper, goal), bsaSkill.getFactor() * bsaWapper.getFactor());
            int hp = (int) ae.getVal();

            sumHp += hp;
            
            goal.decrementHp(hp);
            mar.addTarget(ae);
        }

        FightObject lowest = null;
        for (FightObject fo : bsaWapper.getAttackerEntity().getFightObjects()) {
            if (!fo.isDead()) {
                if (lowest == null) {
                    lowest = fo;
                    continue;
                }
                if (lowest.getHp() / lowest.getMaxHp() > fo.getHp() / fo.getMaxHp()) {
                    lowest = fo;
                }
            }
        }

        

        // 增加一个护盾
        if (lowest != null) {
            int shieldHp = (int) (sumHp * 0.5);
            lowest.setShieldHp(shieldHp);
            
            RecoveryEntry re = new RecoveryEntry();
            re.setLab(lowest.getLab());
            re.setVal(shieldHp);
            re.setType("SHIELD");
            mar.addTarget(re);
        }

        return mar;
    }

}
