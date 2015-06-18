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

import javax.inject.Inject;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class GJiComboScript implements Script {

    private static final Logger LOG  = LoggerFactory.getLogger("org.typhon.spi.war.ComboScript");
    
    private static final double[] FACTORS = {
        1, // 1C
        1.2, // 2C
        1.3, // 3C
        1.5, // 4C
        1.8, // 5C
        2, // 6C
        2.3, // 7C
        2.6, // 8C
        3, // 9C
        3.2, // 10C
        3.4, // 11C
        3.6, // 12C
        3.8, // 13C
        4, // 14C
        5 // 15C
    };

    @Inject
    private Container container;

    @Override
    public Object invoke(Session session, Object obj) {
//        AttackBean bean = (AttackBean) obj;
//        WarCombo warCombo = bean.getWarCombo();
//
//        double hurt = 0;
//        double factor = FACTORS[warCombo.getComboCount() - 1];
//
//        for (Point point : warCombo.getPoints()) {
//            hurt += container.getInstance(WarProvider.class)
//                    .regularAttack(bean.getWarInfo(), point.getFightObject(), bean.getGoalObject());
//        }
//        hurt /= warCombo.getPoints().size();
//
//        // FIXME
//        bean.getGoalObject().decrementHp(hurt * factor);
//        
//        if(LOG.isDebugEnabled()) {
//            LOG.debug("[GJi] b:{} hurt={}, factor={}",
//                    bean.getGoalObject().getHero().getId(),
//                    hurt,
//                    factor);
//        }
        
        return null;
    }

}
