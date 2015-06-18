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

/**
 * 刘备, hb04.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s010 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = getAtk(bsaWapper, bsaWapper.getAobj());

        FightObject aobj = bsaWapper.getAobj();
        MultiAttackResult mar = new MultiAttackResult();
        AttackEntry ae;

        for (FightObject goal
                : bsaWapper.getDefenderEntity().findFightGoals(bsaSkill.getNumOfTargets())) {
            ae = getWarProvider().attack0(bsaWapper.getWarInfo().getTerrain(), aobj,
                    goal, atk, getDef(bsaWapper, goal), bsaSkill.getFactor() * bsaWapper.getFactor());

            // 根据自身血量重新计算伤害
            int hp = (int) ae.getVal();

            goal.decrementHp(hp);
            mar.addTarget(ae);
        }

        // 增加双防御Buffer
        BufferSkill bufferSkill;
        for (FightObject fo : bsaWapper.getAttackerEntity().getFightObjects()) {
            bufferSkill = new MyBufferSkill(bsaWapper.getWarInfo(),
                    bsaWapper.getAttackerEntity().getDire(), fo);
            bufferSkill.onBefore();
        }
        return mar;
    }

    private class MyBufferSkill extends FightObjectBufferSkill {

        private int pdef;
        private int pmdef;

        public MyBufferSkill(WarInfo warInfo, Direction dire, FightObject fobj) {
            super(warInfo, dire, fobj);
        }

        @Override
        public Type getType() {
            return Type.BUFF;
        }

        @Override
        protected int getTotalRound() {
            return 2;
        }

        @Override
        protected Object begin() {
            pdef = (int) (fobj.getMaxDef() * 0.4);
            pmdef = (int) (fobj.getMaxMdef() * 0.4);

            fobj.setDef(fobj.getDef() + pdef);
            fobj.setMdef(fobj.getMdef() + pmdef);
            return null;
        }

        @Override
        protected Object end() {
            fobj.setDef(fobj.getDef() - pdef);
            fobj.setMdef(fobj.getMdef() - pmdef);
            return null;
        }

    }
}
