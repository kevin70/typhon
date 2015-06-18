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
package org.skfiy.typhon.domain;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class AbstractChangeable implements Changeable {

    private Changeable _parent;
    private String _parentPropertyName;

    @Override
    public Player player() {
        if (_parent == null) {
            return null;
        }

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

}
