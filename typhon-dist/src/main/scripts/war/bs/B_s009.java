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

import java.util.Random;
import org.skfiy.typhon.dobj.BSaSkill;
import org.skfiy.typhon.spi.war.AttackEntry;
import org.skfiy.typhon.spi.war.BSaScript;
import org.skfiy.typhon.spi.war.BSaWapper;
import org.skfiy.typhon.spi.war.BufferResult;
import org.skfiy.typhon.spi.war.BufferSkill;
import org.skfiy.typhon.spi.war.Direction;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.FightObjectBufferSkill;
import org.skfiy.typhon.spi.war.MultiAttackResult;
import org.skfiy.typhon.spi.war.WarInfo;

/**
 * 诸葛亮.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class B_s009 extends BSaScript {

    private static final String[] BUF_TYPES = {BufferSkill.ATK_AND_MATK, BufferSkill.DEPARRY_RATE_BUF,
        BufferSkill.CRIT_RATE_BUF, BufferSkill.CRIT_MAGN_BUF};
    private static final Random RANDOM = new Random();

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

        // 增加BUF
        for (FightObject obj : bsaWapper.getAttackerEntity().getFightObjects()) {
            if (!obj.isDead()) {
                // 增加一个BUF
                String bufType = BUF_TYPES[RANDOM.nextInt(BUF_TYPES.length)];
                MyBufferSkill bufferSkill = new MyBufferSkill(bsaWapper.getWarInfo(),
                        bsaWapper.getAttackerEntity().getDire(), obj, bufType);
                bufferSkill.onBefore();
                
                BufferResult br = new BufferResult();
                br.setBufid(bufType);
                br.setRound(bufferSkill.getTotalRound());
                br.setSource(new AttackEntry(obj.getLab()));
                
                mar.addTarget(br);
            }
        }

        return mar;
    }

    // FIXME 诸葛亮技能需要修改
    private class MyBufferSkill extends FightObjectBufferSkill {

        private int patk;
        private int pmatk;
        
        private double pval;
        
        private final String bufType;

        public MyBufferSkill(WarInfo warInfo, Direction dire, FightObject fobj, String bufType) {
            super(warInfo, dire, fobj);
            this.bufType = bufType;
        }

        @Override
        protected int getTotalRound() {
            return 2;
        }

        @Override
        protected Object begin() {
            switch (bufType) {
                case BufferSkill.ATK_AND_MATK:
                    patk = (int) (fobj.getAtk() * 0.2);
                    pmatk = (int) (fobj.getMatk() * 0.2);

                    fobj.setAtk(fobj.getAtk() + patk);
                    fobj.setMatk(fobj.getMatk() + pmatk);
                    break;
                case BufferSkill.DEPARRY_RATE_BUF:
                    pval = 0.2;
                    fobj.setDeparryRate(fobj.getDeparryRate() + pval);
                    break;
                case BufferSkill.CRIT_RATE_BUF:
                    pval = 0.2;
                    fobj.setCritRate(fobj.getCritRate() + pval);
                    break;
                case BufferSkill.CRIT_MAGN_BUF:
                    fobj.setCritMagn(fobj.getCritMagn() + 0.4);
                    break;
            }
            return null;
        }

        @Override
        protected Object end() {
            switch (bufType) {
                case BufferSkill.ATK_AND_MATK:
                    fobj.setAtk(fobj.getAtk() - patk);
                    fobj.setMatk(fobj.getMatk() - pmatk);
                    break;
                case BufferSkill.DEPARRY_RATE_BUF:
                    fobj.setDeparryRate(fobj.getDeparryRate() - pval);
                    break;
                case BufferSkill.CRIT_RATE_BUF:
                    fobj.setCritRate(fobj.getCritRate() - pval);
                    break;
                case BufferSkill.CRIT_MAGN_BUF:
                    fobj.setCritMagn(fobj.getCritMagn() - 0.4);
                    break;
            }
            return null;
        }

        @Override
        public Type getType() {
            return Type.BUFF;
        }

    }

}
