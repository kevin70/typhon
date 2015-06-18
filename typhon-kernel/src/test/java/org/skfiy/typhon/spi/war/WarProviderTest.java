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
package org.skfiy.typhon.spi.war;

import javax.inject.Inject;
import org.skfiy.typhon.TestComponentBase;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class WarProviderTest extends TestComponentBase {

    @Inject
    private WarProvider warProvider;

    /**
     * Test of attack method, of class WarProvider.
     */
    @Test
    public void testAttack() {
        // FIXME
    }

    @Test
    public void testCalculateCombo() {
//        WarProvider warProvider = new WarProvider();
//        
//        //
//        HeroItemDobj heroItemDobj = new HeroItemDobj();
//        Shot[] shots = {Shot.YHu, Shot.Miss, Shot.JCe,
//            Shot.Q7, Shot.FYu, Shot.GJi,
//            Shot.GJi, Shot.Q7, Shot.QXi,
//            Shot.BSa, Shot.GJi, Shot.QXi, Shot.Q7};
//        heroItemDobj.setShots(shots);
//        
//        FightObject fightObject = new FightObject((IFightItem) heroItemDobj.toDomainItem());
//        FightObject[] fightObjects = {fightObject, fightObject, fightObject, fightObject, fightObject};
//        int[] holdPoints = {1, 1, 1, 3, 3};
//        
//        Collection<WarCombo> combos = warProvider.calculateCombo(fightObjects, holdPoints);
//        //
//        combos.size();
    }

}
