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
import org.skfiy.typhon.spi.war.WarInfo;

/**
 * 祝融, hd11.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s042 extends BSaScript {

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

        goal.decrementHp(hp);
        mar.addTarget(ae);

        MyBufferSkill bufferSkill = new MyBufferSkill(bsaWapper.getWarInfo(),
                bsaWapper.getAttackerEntity().getDire(), aobj);
        bufferSkill.onBefore();
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
            fobj.setParryRate(fobj.getParryRate() + 0.3);
            fobj.setDecritRate(fobj.getDecritRate() + 0.3);
            return null;
        }

        @Override
        protected Object end() {
            fobj.setParryRate(fobj.getParryRate() - 0.3);
            fobj.setDecritRate(fobj.getDecritRate() - 0.3);
            return null;
        }

        @Override
        public Type getType() {
            return Type.BUFF;
        }

    }

}
