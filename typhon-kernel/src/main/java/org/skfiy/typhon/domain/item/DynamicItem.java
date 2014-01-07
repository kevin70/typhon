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

import com.alibaba.fastjson.annotation.JSONType;
import org.skfiy.typhon.script.Script;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 * @param <S>
 */
@JSONType(ignores = {"autoOpen", "overlapping", "price", "script", "staticItem"})
public abstract class DynamicItem<S extends Item> extends Item {

    private S staticItem;

    public S getStaticItem() {
        return staticItem;
    }

    public void setStaticItem(S staticItem) {
        this.staticItem = staticItem;

        if (getId() == null) {
            setId(staticItem.getId());
        }
    }

    @Override
    public boolean isAutoOpen() {
        return staticItem.isAutoOpen();
    }

    @Override
    public int getOverlapping() {
        return staticItem.getOverlapping();
    }

    @Override
    public int getPrice() {
        return staticItem.getPrice();
    }

    @Override
    public Script getScript() {
        return staticItem.getScript();
    }

}
