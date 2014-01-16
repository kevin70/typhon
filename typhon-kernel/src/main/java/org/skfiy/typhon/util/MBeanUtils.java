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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.modeler.ManagedBean;
import org.apache.commons.modeler.Registry;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.Typhons;
import org.skfiy.util.Assert;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public final class MBeanUtils {

    public static final Registry REGISTRY = Registry.getRegistry(null, null);

    /**
     * 
     * @param mb
     * @return 
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
     *
     * @param obj
     * @param mb
     * @return 
     */
    public static ObjectName registerComponent(Object obj, ManagedBean mb) {
        ObjectName objName = newObjectName(mb);
        registerComponent(obj, objName, null);
        return objName;
    }

    /**
     *
     * @param obj
     * @param objName
     * @param type
     */
    public static void registerComponent(Object obj, ObjectName objName, String type) {
        try {
            REGISTRY.registerComponent(obj, objName, type);
        } catch (Exception ex) {
            throw new TyphonException(objName.toString(), ex);
        }
    }
    
    /**
     * 
     * @param clazz
     * @return 
     */
    public static ManagedBean findManagedBean(Class<?> clazz) {
        try {
            return REGISTRY.findManagedBean(clazz, null);
        } catch (Exception ex) {
            throw new TyphonException("findManagedBean: " + clazz.getName(), ex);
        }
    }

}
