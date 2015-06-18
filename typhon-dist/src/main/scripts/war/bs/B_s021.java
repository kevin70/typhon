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
import org.skfiy.typhon.spi.war.BufferResult;
import org.skfiy.typhon.spi.war.BufferSkill;
import org.skfiy.typhon.spi.war.Direction;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.MultiAttackResult;
import org.skfiy.typhon.spi.war.WarInfo;

/**
 * 张角, hd03.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s021 extends BSaScript {

    @Override
    protected Object doExecute(BSaWapper bsaWapper) {
        BSaSkill bsaSkill = bsaWapper.getBsaSkill();
        int atk = getAtk(bsaWapper, bsaWapper.getAobj());

        FightObject aobj = bsaWapper.getAobj();
        MultiAttackResult mar = new MultiAttackResult();
        AttackEntry ae;
        BufferSkill bufferSkill;

        for (FightObject goal
                : bsaWapper.getDefenderEntity().findFightGoals(bsaSkill.getNumOfTargets())) {
            ae = getWarProvider().attack0(bsaWapper.getWarInfo().getTerrain(), aobj,
                    goal, atk, getDef(bsaWapper, goal), bsaSkill.getFactor() * bsaWapper.getFactor());

            int hp = (int) ae.getVal();
            bufferSkill = new MyBufferSkill(bsaWapper.getWarInfo(),
                    bsaWapper.getAttackerEntity().getDire(), goal, hp);
            bufferSkill.onBefore();

            goal.decrementHp(hp);
            mar.addTarget(ae);
        }

        return mar;
    }

    private class MyBufferSkill implements BufferSkill {

        private final int damage;
        private final Direction dire;
        private final WarInfo warInfo;
        private final FightObject fobj;
        private int beginRound;

        @Override
        public String getName() {
            return getClass().getName();
        }

        @Override
        public BufferSkill.Type getType() {
            return BufferSkill.Type.DEBUFF;
        }

        MyBufferSkill(WarInfo warInfo, Direction dire, FightObject fobj, int damage) {
            this.damage = damage;

            this.beginRound = warInfo.getRound();
            this.dire = dire;
            this.warInfo = warInfo;
            this.fobj = fobj;

            if (this.dire == Direction.N) {
                beginRound += 1;
            }
        }

        @Override
        public Object onBefore() {
            fobj.addBufferSkill(this);
            return null;
        }

        @Override
        public Object onAfter() {
            if (warInfo.getRound() - beginRound >= 2) {
                onFinish();
            }

            // DEBUF造成的伤害
            int d = (int) (damage * 0.3);
            fobj.decrementHp(d);

            // 返回
            BufferResult br = new BufferResult();
            br.setBufid(POISON_BUF);
            br.setRound(2 - (warInfo.getRound() - beginRound));

            AttackEntry ae = new AttackEntry();
            ae.setLab(fobj.getLab());
            ae.setVal(-d);
            br.setSource(ae);
            return br;
        }

        @Override
        public Object onFinish() {
            fobj.removeBufferSkill(this);
            return null;
        }

    }
}
