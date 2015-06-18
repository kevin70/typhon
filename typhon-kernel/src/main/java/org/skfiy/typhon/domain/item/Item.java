/*
 * Copyright 2013 The Skfiy Open Association.
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
package org.skfiy.typhon.domain.item;

import org.skfiy.typhon.script.Script;

/** 
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class Item {

    private String id;
    private Star star;

    /**
     * 
     * @return 
     */
    public final String getId() {
        return id;
    }

    /**
     * 
     * @param id 
     */
    public final void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return 
     */
    public Star getStar() {
        return star;
    }

    /**
     * 
     * @param star 
     */
    public void setStar(Star star) {
        this.star = star;
    }
    
    /**
     * 
     * @return 
     */
    public abstract boolean isAutoOpen();

    /**
     * 
     * @return 
     */
    public abstract int getOverlapping();

    /**
     * 
     * @return 
     */
    public abstract int getPrice();

    /**
     * 
     * @return 
     */
    public abstract Script getScript();
    
    /**
     * 
     * @return 
     */
    public abstract Object getAnnex();
    
    /**
     * 道具品质.
     */
    public enum Star {

        X1("typhon.domain.hero.X1.factor"),
        X2("typhon.domain.hero.X2.factor"),
        X3("typhon.domain.hero.X3.factor"),
        X4("typhon.domain.hero.X4.factor"),
        X5("typhon.domain.hero.X5.factor");

        private final String heroFactorKey;

        Star(String k1) {
            heroFactorKey = k1;
        }

        public String getHeroFactorKey() {
            return heroFactorKey;
        }

    }
}
