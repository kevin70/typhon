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
import org.skfiy.typhon.spi.war.Direction;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.FightObjectBufferSkill;
import org.skfiy.typhon.spi.war.MultiAttackResult;
import org.skfiy.typhon.spi.war.RecoveryEntry;
import org.skfiy.typhon.spi.war.WarInfo;

/**
 * 刘禅.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s053 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = getAtk(bsaWapper, bsaWapper.getAobj());

        FightObject aobj = bsaWapper.getAobj();

        MultiAttackResult mar = new MultiAttackResult();
        FightObject goal = bsaWapper.getDefenderEntity().findFightGoal();
        AttackEntry ae = getWarProvider().attack0(bsaWapper.getWarInfo().getTerrain(), aobj,
                goal, atk, getDef(bsaWapper, goal), bsaSkill.getFactor() * bsaWapper.getFactor());

        int hp = (int) ae.getVal();

        ae.setVal(hp);

        goal.decrementHp(hp);
        mar.addTarget(ae);

        for (FightObject fobj : bsaWapper.getAttackerEntity().getFightObjects()) {
            if (!fobj.isDead()) {
                int dehp = (int) (fobj.getHp() * 0.1);

                RecoveryEntry re = new RecoveryEntry();
                re.setLab(fobj.getLab());
                re.setType("HP");
                re.setVal(-dehp);
                mar.addTarget(re);

                fobj.decrementHp(dehp);
                
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
            return 1;
        }

        @Override
        protected Object begin() {
            fobj.setDef(fobj.getDef() + fobj.getMaxDef());
            fobj.setMdef(fobj.getMdef() + fobj.getMaxMdef());
            return null;
        }

        @Override
        protected Object end() {
            fobj.setDef(fobj.getDef() - fobj.getMaxDef());
            fobj.setMdef(fobj.getMdef() - fobj.getMaxMdef());
            return null;
        }

        @Override
        public Type getType() {
            return Type.BUFF;
        }

    }
}
