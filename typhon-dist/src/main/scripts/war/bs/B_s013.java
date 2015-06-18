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
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.MultiAttackResult;

/**
 * 甘宁, hc01.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s013 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        FightObject aobj = bsaWapper.getAobj();

        int atk = getAtk(bsaWapper, aobj);

        MultiAttackResult mar = new MultiAttackResult();
        AttackEntry ae;

        for (FightObject goal
                : bsaWapper.getDefenderEntity().findFightGoals(bsaSkill.getNumOfTargets())) {
            ae = getWarProvider().attack0(bsaWapper.getWarInfo().getTerrain(), aobj,
                    goal, atk, getDef(bsaWapper, goal), bsaSkill.getFactor() * bsaWapper.getFactor());

            int hp = (int) ae.getVal();
            if (aobj.getFury() > goal.getFury()) {
                hp *= 1.8;
            }
            ae.setVal(hp);

            goal.decrementHp(hp);
            mar.addTarget(ae);
        }
        return mar;
    }

}
