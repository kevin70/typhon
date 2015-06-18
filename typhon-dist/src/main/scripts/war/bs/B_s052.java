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

import java.util.Comparator;
import java.util.List;
import org.skfiy.typhon.dobj.BSaSkill;
import org.skfiy.typhon.spi.war.AttackEntry;
import org.skfiy.typhon.spi.war.BSaScript;
import org.skfiy.typhon.spi.war.BSaWapper;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.MultiAttackResult;
import org.skfiy.typhon.spi.war.RecoveryEntry;

/**
 * 王元姬.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s052 extends BSaScript {

    private static final FruyComparator FRUY_COMPARATOR = new FruyComparator();

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = getAtk(bsaWapper, bsaWapper.getAobj());

        FightObject aobj = bsaWapper.getAobj();

        MultiAttackResult mar = new MultiAttackResult();
        List<FightObject> goals = bsaWapper.getDefenderEntity().findFightGoals(bsaSkill.getNumOfTargets());

        for (FightObject goal : goals) {
            AttackEntry ae = getWarProvider().attack0(bsaWapper.getWarInfo().getTerrain(), aobj,
                    goal, atk, getDef(bsaWapper, goal), bsaSkill.getFactor() * bsaWapper.getFactor());
            int hp = (int) ae.getVal();
            goal.decrementHp(hp);
            mar.addTarget(ae);
            
            // 给已方最少怒气的英雄加怒
            FightObject lowest = null;
            for (FightObject fo : bsaWapper.getAttackerEntity().getFightObjects()) {
                if (fo.isDead()) {
                    continue;
                }

                if (lowest == null) {
                    lowest = fo;
                    continue;
                }

                if (fo.getFury() < lowest.getFury()) {
                    lowest = fo;
                }
            }

            RecoveryEntry re = new RecoveryEntry();
            re.setLab(lowest.getLab());
            re.setType("FURY");
            re.setVal(1);
            lowest.incrementFury(1);
            mar.addTarget(re);
        }

        return mar;
    }

    private static class FruyComparator implements Comparator<FightObject> {

        @Override
        public int compare(FightObject o1, FightObject o2) {
            return Integer.compare(o1.getFury(), o2.getFury());
        }

    }
}
