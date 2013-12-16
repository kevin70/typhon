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
 * 生命周期接口.
 *
 * @author Kevin Zou <kevinz@skfiy.com>
 */
public interface Lifecycle {

    /**
     * The LifecycleEvent type for the "component after init" event.
     */
    String BEFORE_INIT_EVENT = "before_init";
    /**
     * The LifecycleEvent type for the "component after init" event.
     */
    String AFTER_INIT_EVENT = "after_init";
    /**
     * The LifecycleEvent type for the "component start" event.
     */
    String START_EVENT = "start";
    /**
     * The LifecycleEvent type for the "component before start" event.
     */
    String BEFORE_START_EVENT = "before_start";
    /**
     * The LifecycleEvent type for the "component after start" event.
     */
    String AFTER_START_EVENT = "after_start";
    /**
     * The LifecycleEvent type for the "component stop" event.
     */
    String STOP_EVENT = "stop";
    /**
     * The LifecycleEvent type for the "component before stop" event.
     */
    String BEFORE_STOP_EVENT = "before_stop";
    /**
     * The LifecycleEvent type for the "component after stop" event.
     */
    String AFTER_STOP_EVENT = "after_stop";
    /**
     * The LifecycleEvent type for the "component after destroy" event.
     */
    String AFTER_DESTROY_EVENT = "after_destroy";
    /**
     * The LifecycleEvent type for the "component before destroy" event.
     */
    String BEFORE_DESTROY_EVENT = "before_destroy";

    /**
     * 添加LifecycleListener.
     *
     * @param listener LifecycleListener实例
     */
    void addListener(LifecycleListener listener);

    /**
     * 移除LifecycleListener.
     *
     * @param listener LifecycleListener实例
     */
    void removeListener(LifecycleListener listener);

    /**
     * 获取当前所有的LifecycleListener实例.
     *
     * @return 所有的LifecycleListener实例
     */
    LifecycleListener[] findListeners();

    /**
     * 初始化资源. 执行之前当前对象{@code getState() }必须等于{@link LifecycleState#NEW },
     * 反之将得到{@link IllegalStateException }.
     * <p>状态改变及事件通知顺序：
     * <ul>
     *  <li>{@link LifecycleState#INITIALIZING } -> {@link #BEFORE_INIT_EVENT }
     *  <li>{@link LifecycleState#INITIALIZED } -> {@link #AFTER_INIT_EVENT }
     * </ul>
     *
     * @throws LifecycleException 初始化失败
     */
    void init() throws LifecycleException;

    /**
     * 启动当前实例. 执行之前当前对象{@code getState() }必须小于{@link LifecycleState#STARTING_PREP },
     * 如果当前实例并未进行初始化操作,则需要先执行{@link #init() }. 其它则将会得到{@link IllegalStateException }.
     * <p>状态改变及事件通知顺序：
     * <ul>
     *  <li>{@link LifecycleState#STARTING_PREP } -> {@link #BEFORE_START_EVENT }
     *  <li>{@link LifecycleState#STARTING } -> {@link #START_EVENT }
     *  <li>{@link LifecycleState#STARTED } -> {@link #AFTER_START_EVENT }
     * </ul>
     * 
     * @throws LifecycleException 启动失败
     */
    void start() throws LifecycleException;

    /**
     * 停止当前实例. 如果该实例并未启动则不做任何操作, 如果当前对象{@code getState() }大于等于
     * {@link LifecycleState#STOPPED}则将会得到{@link IllegalStateException }.
     * <p>状态改变及事件通知顺序：
     * <ul>
     *  <li>{@link LifecycleState#STOPPING_PREP } -> {@link #BEFORE_STOP_EVENT }
     *  <li>{@link LifecycleState#STOPPING } -> {@link #STOPT_EVENT }
     *  <li>{@link LifecycleState#STOPPED } -> {@link #AFTER_STOP_EVENT }
     * </ul>
     * 
     * @throws LifecycleException 停止失败
     */
    void stop() throws LifecycleException;

    /**
     * 销毁资源. 如果当前对象{@code getState() }大于{@link LifecycleState#DESTROYED }
     * 则将会得到{@link IllegalStateException }.
     * <p>状态改变及事件通知顺序：
     * <ul>
     *  <li>{@link LifecycleState#DESTROYING } -> {@link #BEFORE_DESTROY_EVENT }
     *  <li>{@link LifecycleState#DESTROYED } -> {@link #AFTER_DESTROY_EVENT }
     * </ul>
     * 
     * @throws LifecycleException 销毁资源失败
     */
    void destroy() throws LifecycleException;

    /**
     * 获取当前实例的状态.
     *
     * @return LifecycleState实例
     */
    LifecycleState getState();
}
