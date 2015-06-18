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
import org.skfiy.typhon.spi.war.WarProvider;

/**
 * 夏侯惇, ha04.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s004 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = getAtk(bsaWapper, bsaWapper.getAobj());

        FightObject aobj = bsaWapper.getAobj();
        WarProvider warProvider = getWarProvider();

        MultiAttackResult mar = new MultiAttackResult();
        double p1;
        double f;
        double f1;
        double r;
        double g1;
        double j = bsaSkill.getFactor() * bsaWapper.getFactor();

        FightObject goal = bsaWapper.getDefenderEntity().findFightGoal();
        p1 = warProvider.getTerrainFactor(aobj.getRace(), bsaWapper.getWarInfo().getTerrain());
        f = warProvider.getFuryFactor(aobj.getFury());
        f1 = warProvider.getFuryFactor(goal.getFury());

        // 根据自身掉血计算暴击率
        r = warProvider.getCritMagn(Math.max((aobj.getMaxHp() - aobj.getHp()) / aobj.getMaxHp(), 0.3),
                goal.getDecritRate(),
                aobj.getCritMagn());
        g1 = warProvider.getParryValue(goal.getParryRate(), aobj.getDeparryRate(), goal.getParryValue());

        AttackEntry ae = warProvider.attack0((int) (atk * 1.1), getDef(bsaWapper, goal), p1, f, f1, r, j, g1);
        ae.setLab(goal.getLab());
        
        int hp = (int) ae.getVal();

        goal.decrementHp(hp);
        mar.addTarget(ae);

        return mar;
    }

}
