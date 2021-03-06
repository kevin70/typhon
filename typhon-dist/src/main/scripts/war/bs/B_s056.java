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
import org.skfiy.typhon.domain.item.Race;
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
 * 曹丕.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s056 extends BSaScript {

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
        }

        for (FightObject fobj : bsaWapper.getAttackerEntity().getFightObjects()) {
            if (!fobj.isDead() && fobj.getRace() == Race.Gong) {
                MyBufferSkill bufferSkill = new MyBufferSkill(bsaWapper.getWarInfo(),
                        bsaWapper.getAttackerEntity().getDire(), fobj);
                bufferSkill.onBefore();
            }
        }
        return mar;
    }

    private class MyBufferSkill extends FightObjectBufferSkill {

        private int patk;
        private int pmatk;

        public MyBufferSkill(WarInfo warInfo, Direction dire, FightObject fobj) {
            super(warInfo, dire, fobj);
        }

        @Override
        protected int getTotalRound() {
            return 2;
        }

        @Override
        protected Object begin() {
            patk = (int) (fobj.getMaxAtk() * 0.4);
            pmatk = (int) (fobj.getMaxMatk() * 0.4);
            fobj.setAtk(fobj.getAtk() + patk);
            fobj.setMatk(fobj.getMatk() + pmatk);
            return null;
        }

        @Override
        protected Object end() {
            fobj.setAtk(fobj.getAtk() - patk);
            fobj.setMatk(fobj.getMatk() - pmatk);
            return null;
        }

        @Override
        public BufferSkill.Type getType() {
            return BufferSkill.Type.BUFF;
        }

    }
}
