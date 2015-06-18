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
import org.skfiy.typhon.domain.Changeable;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.script.Script;
import org.skfiy.typhon.util.DomainUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 * @param <S>
 */
@JSONType(ignores = {"autoOpen", "overlapping", "price", "script", "annex", "itemDobj", "parent"})
public abstract class AbstractItem<S extends Item> extends Item implements Changeable {

    private Changeable _parent;
    private String _parentPropertyName;

    private S itemDobj;

    @Override
    public Player player() {
        return _parent.player();
    }

    @Override
    public Changeable parent() {
        return _parent;
    }

    @Override
    public String parentPropertyName() {
        return _parentPropertyName;
    }

    @Override
    public void set(Changeable parent, String parentPropertyName) {
        this._parent = parent;
        this._parentPropertyName = parentPropertyName;
    }

    /**
     *
     * @return
     */
    public S getItemDobj() {
        return itemDobj;
    }

    /**
     *
     * @param itemDobj
     */
    public void setItemDobj(S itemDobj) {
        this.itemDobj = itemDobj;

        if (getId() == null) {
            setId(itemDobj.getId());
        }
    }

    @Override
    public final boolean isAutoOpen() {
        return itemDobj.isAutoOpen();
    }

    @Override
    public final int getOverlapping() {
        return itemDobj.getOverlapping();
    }

    @Override
    public final int getPrice() {
        return itemDobj.getPrice();
    }

    @Override
    public final Script getScript() {
        return itemDobj.getScript();
    }

    @Override
    public Object getAnnex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStar(Star star) {
        super.setStar(star);
        if (star != null) {
            DomainUtils.firePropertyChange(this, "star", star);
        }
    }

}
