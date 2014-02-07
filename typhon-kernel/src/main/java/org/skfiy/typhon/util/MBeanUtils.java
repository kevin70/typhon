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
package org.skfiy.typhon.util;

import javax.management.ObjectName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.modeler.ManagedBean;
import org.apache.commons.modeler.Registry;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.Typhons;
import org.skfiy.util.Assert;

/**
 * MBean工具类.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public final class MBeanUtils {

    /**
     * 默认的{@link Registry }实例.
     */
    public static final Registry REGISTRY = Registry.getRegistry(null, null);

    /**
     * 新建一个{@code ObjectName }实例.
     *
     * @param mb {@link ManagedBean }
     * @return {@code ObjectName }
     */
    public static ObjectName newObjectName(ManagedBean mb) {
        Assert.notNull(mb.getName());

        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(mb.getDomain())) {
            sb.append(mb.getDomain());
        } else {
            sb.append(Globals.DEFAULT_MBEAN_DOMAIN);
        }

        sb.append(":name=").append(mb.getName());

        if (!StringUtils.isEmpty(mb.getType())) {
            sb.append(",type=").append(mb.getType());
        }
        if (!StringUtils.isEmpty(mb.getGroup())) {
            sb.append(",group=").append(mb.getGroup());
        }
        return Typhons.newObjectName(sb.toString());
    }

    /**
     * 注册一个Bean实例部件. 并返回注册成功的{@code ObjectName }对象.
     *
     * @param obj 目标实例
     * @param mb {@link ManagedBean }
     * @return 注册成功的{@code ObjectName }对象
     */
    public static ObjectName registerComponent(Object obj, ManagedBean mb) {
        ObjectName objName = newObjectName(mb);
        registerComponent(obj, objName, null);
        return objName;
    }

    /**
     * 注册一个Bean实例部件.
     *
     * @param obj 目标实例
     * @param objName {@code ObjectName }对象
     * @param type 注册的类型
     */
    public static void registerComponent(Object obj, ObjectName objName, String type) {
        try {
            REGISTRY.registerComponent(obj, objName, type);
        } catch (Exception ex) {
            throw new TyphonException(objName.toString(), ex);
        }
    }

    /**
     * 根据{@code Class }查询注册的{@link ManagedBean }实例.
     *
     * @param clazz 注册类型
     * @return 符合的{@code ManagedBean }实例
     */
    public static ManagedBean findManagedBean(Class<?> clazz) {
        try {
            return REGISTRY.findManagedBean(clazz, null);
        } catch (Exception ex) {
            throw new TyphonException("findManagedBean: " + clazz.getName(), ex);
        }
    }
}
