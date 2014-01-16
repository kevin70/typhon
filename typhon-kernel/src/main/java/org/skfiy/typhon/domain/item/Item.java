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
}
