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
package war.bs;

import org.skfiy.typhon.dobj.BSaSkill;
import org.skfiy.typhon.spi.war.AttackEntry;
import org.skfiy.typhon.spi.war.BSaScript;
import org.skfiy.typhon.spi.war.BSaWapper;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.MultiAttackResult;
import org.skfiy.typhon.spi.war.RecoveryEntry;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s065 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = getAtk(bsaWapper, bsaWapper.getAobj());

        FightObject aobj = bsaWapper.getAobj();
        MultiAttackResult mar = new MultiAttackResult();

        for (FightObject goal : bsaWapper.getDefenderEntity().findFightGoals(bsaSkill.getNumOfTargets())) {
            AttackEntry ae = getWarProvider().attack0(bsaWapper.getWarInfo().getTerrain(), aobj,
                    goal, atk, getDef(bsaWapper, goal), bsaSkill.getFactor() * bsaWapper.getFactor());
            int hp = (int) ae.getVal();

            goal.decrementHp(hp);
            mar.addTarget(ae);

            //======================================================================================
            FightObject lowest = null;
            for (FightObject fo : bsaWapper.getAttackerEntity().getFightObjects()) {
                if (fo.isDead()) {
                    continue;
                }

                if (lowest == null) {
                    lowest = fo;
                    continue;
                }

                if (fo.getHp() * 1D / fo.getMaxHp() < lowest.getHp() * 1D / lowest.getMaxHp()) {
                    lowest = fo;
                }
            }

            RecoveryEntry re = new RecoveryEntry();
            int rehp = (int) (aobj.getMaxHp() * 0.1);
            lowest.incrementHp(rehp);

            re.setLab(lowest.getLab());
            re.setType("HP");
            re.setVal(rehp);

            mar.addTarget(re);
        }

        return mar;
    }
}
