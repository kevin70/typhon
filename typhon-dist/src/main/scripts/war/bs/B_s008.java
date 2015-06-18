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

import org.skfiy.typhon.spi.war.AttackEntry;
import org.skfiy.typhon.spi.war.BSaScript;
import org.skfiy.typhon.spi.war.BSaWapper;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.MultiAttackResult;
import org.skfiy.typhon.spi.war.WarProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s008 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        // BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = getAtk(bsaWapper, bsaWapper.getAobj());

        FightObject aobj = bsaWapper.getAobj();
        WarProvider warProvider = getWarProvider();

        MultiAttackResult mar = new MultiAttackResult();
        AttackEntry ae;
        double p1;
        double f;
        double f1;
        double r;
        double g1;
        double j = bsaWapper.getBsaSkill().getFactor() * bsaWapper.getFactor();

        FightObject goal = bsaWapper.getDefenderEntity().findFightGoal();

        p1 = warProvider.getTerrainFactor(aobj.getRace(), bsaWapper.getWarInfo().getTerrain());
        f = warProvider.getFuryFactor(aobj.getFury());
        f1 = warProvider.getFuryFactor(goal.getFury());
        r = warProvider.getCritMagn(aobj.getCritRate() + 0.2, goal.getDecritRate(), aobj.getCritMagn() + 0.6);
        g1 = warProvider.getParryValue(goal.getParryRate(), aobj.getDeparryRate(), goal.getParryValue());

        ae = warProvider.attack0(atk, getDef(bsaWapper, goal), p1, f, f1, r, j, g1);
        ae.setLab(goal.getLab());

        // 根据自身血量重新计算伤害
        int hp = (int) ae.getVal();

        goal.decrementHp(hp);
        mar.addTarget(ae);

        return mar;
    }

}
