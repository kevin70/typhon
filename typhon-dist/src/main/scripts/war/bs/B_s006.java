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
import org.skfiy.typhon.spi.war.BufferSkill;
import org.skfiy.typhon.spi.war.Direction;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.FightObjectBufferSkill;
import org.skfiy.typhon.spi.war.MultiAttackResult;
import org.skfiy.typhon.spi.war.WarInfo;
import org.skfiy.typhon.spi.war.WarProvider;

/**
 * 曹操, ha06.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s006 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = getAtk(bsaWapper, bsaWapper.getAobj());
        WarProvider warProvider = getWarProvider();

        FightObject aobj = bsaWapper.getAobj();
        MultiAttackResult mar = new MultiAttackResult();
        AttackEntry ae;
        double p1;
        double f;
        double f1;
        double r;
        double g1;
        double j = bsaSkill.getFactor() * bsaWapper.getFactor();

        for (FightObject goal
                : bsaWapper.getDefenderEntity().findFightGoals(bsaSkill.getNumOfTargets())) {
            p1 = warProvider.getTerrainFactor(aobj.getRace(), bsaWapper.getWarInfo().getTerrain());
            f = warProvider.getFuryFactor(aobj.getFury());
            f1 = warProvider.getFuryFactor(goal.getFury());
            r = warProvider.getCritMagn(aobj.getCritRate(), goal.getDecritRate(), aobj.getCritMagn());
            g1 = 0;

            ae = warProvider.attack0(atk, getDef(bsaWapper, goal), p1, f, f1, r, j, g1);
            ae.setLab(goal.getLab());

            int hp = (int) ae.getVal();

            goal.decrementHp(hp);
            mar.addTarget(ae);
        }
        
        for (FightObject fobj : bsaWapper.getAttackerEntity().getFightObjects()) {
            if (!fobj.isDead()) {
                MyBufferSkill bufferSkill = new MyBufferSkill(bsaWapper.getWarInfo(),
                        bsaWapper.getAttackerEntity().getDire(), fobj);
                bufferSkill.onBefore();
            }
        }
        return mar;
    }

    private class MyBufferSkill extends FightObjectBufferSkill {

        public MyBufferSkill(WarInfo warInfo, Direction dire, FightObject fobj) {
            super(warInfo, dire, fobj);
        }

        @Override
        protected int getTotalRound() {
            return 2;
        }

        @Override
        protected Object begin() {
            fobj.setCritRate(fobj.getCritRate() + 0.25);
            return null;
        }

        @Override
        protected Object end() {
            fobj.setCritRate(fobj.getCritRate() - 0.25);
            return null;
        }

        @Override
        public BufferSkill.Type getType() {
            return BufferSkill.Type.BUFF;
        }

    }
    
}
