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
package war;

import java.util.HashMap;
import java.util.Map;
import org.skfiy.typhon.domain.item.IFightItem.Shot;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.spi.war.BufferSkill;
import org.skfiy.typhon.spi.war.Direction;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.Q7ComboWapper;
import org.skfiy.typhon.spi.war.WarInfo;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Q7ComboScript implements Script {

    @Override
    public Object invoke(Session session, Object obj) {
        Q7ComboWapper qcw = (Q7ComboWapper) obj;
        WarInfo.Entity attackerEntity = qcw.getAttackerEntity();

        BufferSkill bs;
        for (FightObject fobj : attackerEntity.getFightObjects()) {
            if (qcw.getWarCombo().getComboCount() == 2) {
                bs = new TwoCombo(qcw.getWarInfo(), attackerEntity, fobj);
            } else {
                bs = new ThreeCombo(qcw.getWarInfo(), attackerEntity, fobj);
            }
            bs.onBefore();
        }

        return null;
    }

    private static class TwoCombo implements BufferSkill {

        private static final Shot[] QXI_SHOTS = {Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi,
            Shot.QXi};

        private final Map<FightObject, Shot[]> shotMap = new HashMap<>();

        private final WarInfo warInfo;
        private final WarInfo.Entity attackerEntity;
        private final FightObject fobj;
        private int atkCount;

        public TwoCombo(WarInfo warInfo, WarInfo.Entity attackerEntity, FightObject fobj) {
            this.warInfo = warInfo;
            this.attackerEntity = attackerEntity;
            this.fobj = fobj;
        }

        @Override
        public String getName() {
            return "Q7";
        }

        @Override
        public Type getType() {
            return Type.BUFF;
        }

        @Override
        public Object onBefore() {
            atkCount = attackerEntity.getAtkCount();

            // 缓存原先的拉霸项
            shotMap.put(fobj, fobj.getShots());

            fobj.setShots(getShots());
            fobj.addBufferSkill(this);

            // 继续攻击
            warInfo.setNextDire(attackerEntity.getDire());
            return null;
        }

        @Override
        public Object onAfter() {
            if ((attackerEntity.getAtkCount() - atkCount) >= 3) {
                fobj.setShots(shotMap.get(fobj));
                fobj.removeBufferSkill(this);
            }

            return null;
        }

        Shot[] getShots() {
            return QXI_SHOTS;
        }

        @Override
        public Object onFinish() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private static class ThreeCombo extends TwoCombo {

        private static final Shot[] BSA_SHOTS = {Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa,
            Shot.BSa};

        public ThreeCombo(WarInfo warInfo, WarInfo.Entity attackerEntity, FightObject fobj) {
            super(warInfo, attackerEntity, fobj);
        }

        @Override
        Shot[] getShots() {
            return BSA_SHOTS;
        }

    }

}
