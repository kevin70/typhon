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

import java.util.List;
import org.skfiy.typhon.dobj.BSaSkill;
import org.skfiy.typhon.spi.war.AttackEntry;
import org.skfiy.typhon.spi.war.BSaScript;
import org.skfiy.typhon.spi.war.BSaWapper;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.MultiAttackResult;

/**
 * 姜维, hb11.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s039 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = (int) (getAtk(bsaWapper, bsaWapper.getAobj()) * 1.4);

        FightObject aobj = bsaWapper.getAobj();

        MultiAttackResult mar = new MultiAttackResult();

        List<FightObject> goals = bsaWapper.getDefenderEntity().findFightGoals(bsaSkill.getNumOfTargets());
        AttackEntry ae;
        int hp;

        for (FightObject goal : goals) {
            ae = getWarProvider().attack0(bsaWapper.getWarInfo().getTerrain(), aobj,
                    goal, atk, (goal.getDef() + goal.getMdef()) / 2, bsaSkill.getFactor() * bsaWapper.getFactor());
            hp = (int) ae.getVal();
            goal.decrementHp(hp);
            mar.addTarget(ae);
        }
        return mar;
    }

}
