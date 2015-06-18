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
package org.skfiy.typhon.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.skfiy.typhon.util.DomainUtils;

import com.alibaba.fastjson.annotation.JSONType;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@JSONType(ignores = {"parent", "player", "parentPropertyName"})
public class Role implements Changeable {

    private int rid;
    private String name;
    private int level = 1;
    private boolean enabled;
    private long creationTime = -1L;
    private long lastAccessedTime;
    private long lastLoginedTime;
    private int diamond;

    private Changeable _parent;
    private String _parentPropertyName;

    @Override
    public Player player() {
        return _parent.player();
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        DomainUtils.firePropertyChange(this, "name", this.name);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;

        if (_parent != null) {
            DomainUtils.firePropertyChange(this, "level", this.level);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
        DomainUtils.firePropertyChange(this, "creationTime", this.creationTime);
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public long getLastLoginedTime() {
        return lastLoginedTime;
    }

    public void setLastLoginedTime(long lastLoginedTime) {
        this.lastLoginedTime = lastLoginedTime;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;

        if (_parent != null) {
            DomainUtils.firePropertyChange(this, "diamond", this.diamond);
        }
    }

    @Override
    public Changeable parent() {
        return _parent;
    }

    @Override
    public void set(Changeable parent, String parentPropertyName) {
        this._parent = parent;
        this._parentPropertyName = parentPropertyName;
    }

    @Override
    public String parentPropertyName() {
        return _parentPropertyName;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("rid", rid).append("\n");
        builder.append("level", level).append("\n");
        builder.append("creationTime", creationTime).append("\n");
        builder.append("diamond", diamond).append("\n");
        builder.append("currentTimeMillis", System.currentTimeMillis()).append("\n");
        return builder.toString();
    }
}
