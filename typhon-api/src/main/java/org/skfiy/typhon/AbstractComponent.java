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
package org.skfiy.typhon;

/**
 * 抽象的{@code Component }实现. 该对象实现{@code Component }的基本状态定义.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class AbstractComponent implements Component {

    private Status status;

    @Override
    public final void init() {
        checkStatus(null);
        doInit();
        status = Status.INITIALIZED;
    }

    @Override
    public final void reload() {
        checkStatus(Status.INITIALIZED);
        doReload();
    }

    @Override
    public final void destroy() {
        checkStatus(Status.INITIALIZED);
        doDestroy();
        status = Status.DESTROYED;
    }

    /**
     * 获取当前{@code Component }的状态.
     *
     * @return 状态枚举
     */
    protected Status getStatus() {
        return status;
    }

    /**
     * 具体的初始化实现.
     */
    protected abstract void doInit();

    /**
     * 具体的重加载实现. 执行该操作要求当前的{@code Component }状态值必须是{@link Status#INITIALIZED }.
     */
    protected abstract void doReload();

    /**
     * 具体的销毁实现.
     */
    protected abstract void doDestroy();

    private void checkStatus(Status expected) {
        if (status != expected) {
            throw new ComponentStatusException(
                    "current status [" + status + "] exected status [" + expected + "]");
        }
    }
}
