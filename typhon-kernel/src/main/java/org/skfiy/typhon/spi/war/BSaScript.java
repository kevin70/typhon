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
import org.skfiy.typhon.Container;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.spi.war.BSaWapper;
import org.skfiy.typhon.spi.war.FightObject;
import org.skfiy.typhon.spi.war.WarProvider;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class BSaScript implements Script {

    @Inject
    protected Container container;

    @Override
    public Object invoke(Session session, Object obj) {
        return doExecute((BSaWapper) obj);
    }

    /**
     *
     * @param bsaWapper
     * @return
     */
    protected abstract Object doExecute(BSaWapper bsaWapper);

    /**
     *
     * @return
     */
    protected WarProvider getWarProvider() {
        return container.getInstance(WarProvider.class);
    }

    /**
     *
     * @param bsaWapper
     * @param obj
     * @return
     */
    protected int getAtk(BSaWapper bsaWapper, FightObject obj) {
        switch (bsaWapper.getBsaSkill().getDamageType()) {
            case AD:
                return obj.getAtk();
            case AP:
                return obj.getMatk();
            default:
                return (obj.getAtk() + obj.getMatk()) / 2;
        }
    }

    /**
     *
     * @param bsaWapper
     * @param obj
     * @return
     */
    protected int getDef(BSaWapper bsaWapper, FightObject obj) {
        switch (bsaWapper.getBsaSkill().getDamageType()) {
            case AD:
                return obj.getDef();
            case AP:
                return obj.getMdef();
            default:
                return (obj.getDef() + obj.getMdef()) / 2;
        }
    }

}
