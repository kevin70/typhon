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
package org.skfiy.typhon.dobj;

import com.alibaba.fastjson.annotation.JSONType;
import org.skfiy.typhon.domain.item.AbstractItem;
import org.skfiy.typhon.domain.item.Item;
import org.skfiy.typhon.script.Script;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = "script")
public abstract class ItemDobj extends Item {

    private boolean autoOpen;
    private int overlapping;
    private int price;
    private String displayGroup;
    private Script script;
    private Object annex;

    public void setAutoOpen(boolean autoOpen) {
        this.autoOpen = autoOpen;
    }

    public void setOverlapping(int overlapping) {
        this.overlapping = overlapping;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public void setAnnex(Object annex) {
        this.annex = annex;
    }
    public String getDisplayGroup() {
        return displayGroup;
    }
    public void setDisplayGroup(String displayGroup) {
        this.displayGroup = displayGroup;
    }

    @Override
    public boolean isAutoOpen() {
        return autoOpen;
    }

    @Override
    public int getOverlapping() {
        return overlapping;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public Script getScript() {
        return script;
    }

    @Override
    public Object getAnnex() {
        return annex;
    }

    public abstract AbstractItem toDomainItem();
    
}
