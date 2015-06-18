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
package war.buf;

import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.item.IFightItem;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.WarInfo;
import org.skfiy.typhon.spi.war.AttackEntry;
import org.skfiy.typhon.spi.war.AttackResult;
import org.skfiy.typhon.spi.war.Direction;
import org.skfiy.typhon.spi.war.FightObjectBufferSkill;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class FYuBufferSkill extends FightObjectBufferSkill {

    public FYuBufferSkill(WarInfo warInfo, Direction dire, FightObject fobj) {
        super(warInfo, dire, fobj);
    }

    @Override
    public String getName() {
        return "FYu";
    }

    @Override
    public Type getType() {
        return Type.BUFF;
    }

    @Override
    protected int getTotalRound() {
        return 1;
    }

    @Override
    protected Object begin() {
        // 增加血量
        int pHp = (int) (fobj.getMaxHp() * Typhons.getFloat("typhon.spi.war.fyu.factor"));
        fobj.incrementHp(pHp); // FIXME

        // 增加防御/恢复
        int def = (int) (fobj.getMaxDef() * 0.2);
        int mdef = (int) (fobj.getMaxMdef() * 0.2);
        fobj.setDef(fobj.getDef() + def);
        fobj.setMdef(fobj.getMdef() + mdef);

        fobj.addBufferSkill(this);

        // 返回结果===================================================================================
        AttackResult ar = new AttackResult();
        ar.setShot(IFightItem.Shot.FYu);

        AttackEntry ae = new AttackEntry();
        ae.setLab(fobj.getLab());
        ae.setVal(pHp);
        ar.setSource(ae);
        return ar;
    }

    @Override
    protected Object end() {
        // 增加防御/恢复
        int def = (int) (fobj.getMaxDef() * 0.2);
        int mdef = (int) (fobj.getMaxMdef() * 0.2);
        fobj.setDef(fobj.getDef() - def);
        fobj.setMdef(fobj.getMdef() - mdef);
        return null;
    }

}
