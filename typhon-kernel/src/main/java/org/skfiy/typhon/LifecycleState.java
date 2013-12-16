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
package org.skfiy.typhon;

/**
 * 生命周期状态枚举定义.
 *
 * @author Kevin Zou <kevinz@skfiy.com>
 */
public enum LifecycleState {

    /**
     * 最开始状态.
     */
    NEW(false, null),
    /**
     * {@code Lifecycle }对象正在初始化中.
     */
    INITIALIZING(false, Lifecycle.BEFORE_INIT_EVENT),
    /**
     * {@code Lifecycle }对象已经初始化完毕.
     */
    INITIALIZED(false, Lifecycle.AFTER_INIT_EVENT),
    /**
     * {@code Lifecycle }对象启动之前.
     */
    STARTING_PREP(false, Lifecycle.BEFORE_START_EVENT),
    /**
     * {@code Lifecycle }对象启动中.
     */
    STARTING(true, Lifecycle.START_EVENT),
    /**
     * {@code Lifecycle }对象启动完毕.
     */
    STARTED(true, Lifecycle.AFTER_START_EVENT),
    /**
     * {@code Lifecycle }对象停止前.
     */
    STOPPING_PREP(true, Lifecycle.BEFORE_STOP_EVENT),
    /**
     * {@code Lifecycle }对象停止中.
     */
    STOPPING(false, Lifecycle.STOP_EVENT),
    /**
     * {@code Lifecycle }对象停止完毕
     */
    STOPPED(false, Lifecycle.AFTER_STOP_EVENT),
    /**
     * {@code Lifecycle }对象销毁资源中.
     */
    DESTROYING(false, Lifecycle.BEFORE_DESTROY_EVENT),
    /**
     * {@code Lifecycle }对象销毁完毕.
     */
    DESTROYED(false, Lifecycle.AFTER_DESTROY_EVENT),
    /**
     * {@code Lifecycle }失败状态.
     */
    FAILED(false, null);
    
    private final boolean available;
    private final String event;

    private LifecycleState(boolean available, String event) {
        this.available = available;
        this.event = event;
    }

    /**
     * {@code Lifecycle }对象是否为可用状态.
     *
     * @return 一个{@code boolean }对象
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * 状态对应的事件值.
     *
     * @return 事件字符串值
     */
    public String getEvent() {
        return event;
    }
}
