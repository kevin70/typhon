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
package org.skfiy.typhon.container;

import java.lang.reflect.Method;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.Lifecycle;
import org.skfiy.typhon.LifecycleEvent;
import org.skfiy.typhon.LifecycleListener;
import org.skfiy.typhon.TyphonException;
import org.skfiy.util.ClassUtils;
import org.skfiy.util.ReflectionUtils;

/**
 * IoC容器初始/销毁监听器.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class ContainerListener implements LifecycleListener {

    private String impl;
    private Container container;

    @Override
    public void execute(final LifecycleEvent event) {
        if (Lifecycle.START_EVENT.equals(event.getEvent())) {
            try {
                Class clazz = Class.forName(impl);
                container = (Container) clazz.newInstance();
                Method m = ReflectionUtils.findMethod(clazz, "init");
                m.invoke(container);
            } catch (Exception ex) {
                throw new TyphonException("初始化[" + impl + "]失败", ex);
            }
        } else if (Lifecycle.BEFORE_STOP_EVENT.equals(event.getEvent())) {
            if (container != null) {
                Method m = ReflectionUtils.findMethod(container.getClass(), "destroy");
                ReflectionUtils.invokeMethod(m, container);
            }
        }
    }

    /**
     * 获取IoC容器实现类(全限类定名).
     *
     * @return 全限类名字符串
     */
    public String getImpl() {
        return impl;
    }

    /**
     * 设置IoC容器实现类(全限类定名).
     *
     * @param impl 全限类名字符串
     */
    public void setImpl(String impl) {
        this.impl = impl;
    }
}
