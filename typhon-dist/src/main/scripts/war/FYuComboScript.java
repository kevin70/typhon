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

import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class FYuComboScript implements Script {

    private static final Logger LOG = LoggerFactory.getLogger("org.typhon.spi.war.ComboScript");

    private static final double[] FACTORS = {
        0.3,
        0.35,
        0.4,
        0.45,
        0.5,
        0.55,
        0.6,
        0.65,
        0.7,
        0.75,
        0.8,
        0.85,
        0.9,
        0.95,
        1
    };

    @Override
    public Object invoke(Session session, Object obj) {
//        AttackBean bean = (AttackBean) obj;
//        double factor = FACTORS[bean.getWarCombo().getComboCount()];
//
//        for (FightObject fo : getFightObjects(bean)) {
//            fo.incrementHp((int) (fo.getOrigHp() * factor));
//        }
        
        return null;
    }

//    private FightObject[] getFightObjects(AttackBean bean) {
//        if (bean.getWarInfo().getAround() % 2 == 0) {
//            return bean.getWarInfo().getAfightObjects();
//        }
//        return bean.getWarInfo().getBfightObjects();
//    }

}
