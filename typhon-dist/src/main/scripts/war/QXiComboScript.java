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
public class QXiComboScript implements Script {

    private static final Logger LOG  = LoggerFactory.getLogger("org.typhon.spi.war.ComboScript");
    
    private static final double[] FACTORS = {
        0.6,
        1.2,
        1.3,
        1.5,
        1.8,
        2,
        2.3,
        2.6,
        3,
        3.2,
        3.4,
        3.6,
        3.8,
        4,
        5
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
//                    .qxiAttack(bean.getWarInfo(), point.getFightObject(), bean.getGoalObject(), factor);
//        }
//        hurt /= warCombo.getPoints().size();
//
//        // FIXME
//        bean.getGoalObject().decrementHp(hurt * factor);
//        
//        if (LOG.isDebugEnabled()) {
//            LOG.debug("[QXi] b:{} hurt={}, factor={}",
//                    bean.getGoalObject().getHero().getId(),
//                    hurt,
//                    factor);
//        }
        
        return null;
    }
    
}
