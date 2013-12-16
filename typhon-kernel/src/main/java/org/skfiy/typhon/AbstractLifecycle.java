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

import org.apache.commons.lang3.ArrayUtils;

/**
 * 抽象Lifecycle实现. 创建AbstractLifecycle实例时LifecycleState默认为NEW状态.
 * 该类定义{@link #fireLifecycleListener(java.lang.String) }
 * 当state改变时应该通过该接口通知所有的LifecycleListener.
 *
 * @author kevinz <kevinz@skfiy.org>
 */
public abstract class AbstractLifecycle implements Lifecycle {

    private LifecycleState state;
    private LifecycleListener[] listeners = new LifecycleListener[0];

    /**
     * 无参构造函数.
     */
    public AbstractLifecycle() {
        state = LifecycleState.NEW;
    }

    @Override
    public final void addListener(final LifecycleListener listener) {
        listeners = ArrayUtils.add(listeners, listener);
    }

    @Override
    public final void removeListener(final LifecycleListener listener) {
        int i = ArrayUtils.indexOf(listeners, listener);
        listeners = ArrayUtils.remove(listeners, i);
    }

    @Override
    public final LifecycleListener[] findListeners() {
        return ArrayUtils.clone(listeners);
    }

    @Override
    public final void init() throws LifecycleException {
        setState(LifecycleState.INITIALIZING);
        fireLifecycleListener(BEFORE_INIT_EVENT);

        initInternal();

        setState(LifecycleState.INITIALIZED);
        fireLifecycleListener(AFTER_INIT_EVENT);
    }

    @Override
    public final void start() throws LifecycleException {
        setState(LifecycleState.STARTING_PREP);
        fireLifecycleListener(BEFORE_START_EVENT);

        startInternal();

        setState(LifecycleState.STARTED);
        fireLifecycleListener(AFTER_START_EVENT);
    }

    @Override
    public final void stop() throws LifecycleException {
        setState(LifecycleState.STOPPING_PREP);
        fireLifecycleListener(BEFORE_STOP_EVENT);

        stopInternal();

        setState(LifecycleState.STOPPED);
        fireLifecycleListener(AFTER_STOP_EVENT);
    }

    @Override
    public final void destroy() throws LifecycleException {
        setState(LifecycleState.DESTROYING);
        fireLifecycleListener(BEFORE_DESTROY_EVENT);

        destroyInternal();

        setState(LifecycleState.DESTROYED);
        fireLifecycleListener(AFTER_DESTROY_EVENT);
    }

    @Override
    public final LifecycleState getState() {
        return state;
    }

    /**
     * 内部初始化.
     *
     * @throws LifecycleException 缺少资源
     */
    protected abstract void initInternal() throws LifecycleException;

    /**
     * 内部启动.
     *
     * @throws LifecycleException 缺少资源
     */
    protected abstract void startInternal() throws LifecycleException;

    /**
     * 内部停止.
     *
     * @throws LifecycleException 缺少资源
     */
    protected abstract void stopInternal() throws LifecycleException;

    /**
     * 内部销毁资源.
     *
     * @throws LifecycleException 缺少资源
     */
    protected abstract void destroyInternal() throws LifecycleException;

    /**
     * 设置新的LifecycleState. 根据{@link LifecycleState}的枚举定义, newState应该大于当前的state.
     * 如果当前状态为{@link LifecycleState#STOPPED},newState等于{@link LifecycleState#NEW}时,
     * 则将会得到IllegalStateException.
     *
     * @param newState 新的状态
     */
    protected final void setState(final LifecycleState newState) {
        if (state.compareTo(newState) >= 0) {
            throw new IllegalStateException(state + " don't updated " + newState);
        }
        state = newState;
    }

    /**
     * 执行所有的LifecycleListener.
     *
     * @param event 生命周期状态值{@link Lifecycle}
     */
    protected final void fireLifecycleListener(final String event) {
        LifecycleEvent e = new LifecycleEvent(this, event);
        for (LifecycleListener listener : listeners) {
            listener.execute(e);
        }
    }
}
