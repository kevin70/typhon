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
package org.skfiy.typhon.spi.cglib;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.skfiy.typhon.domain.Changeable;
import org.skfiy.typhon.domain.Indexable;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.packet.PacketIndexPropertyChange;
import org.skfiy.typhon.packet.PacketPropertyChange;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.util.DomainUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class DomainProxyCallback implements MethodInterceptor {

    public static DomainProxyCallback INSTANCE = new DomainProxyCallback();
    private static final Map<Object, PropertyMapping> MAPPINGS = new ConcurrentHashMap<>();

    private DomainProxyCallback() {

    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object rs = proxy.invokeSuper(obj, args);
        
        Changeable c = (Changeable) obj;
        if (c.getPlayer() == null) {
            return null;
        }
        
        PropertyMapping mapping = MAPPINGS.get(method);
        if (mapping == null) {
            synchronized (MAPPINGS) {
                mapping = newMapping(method);
                MAPPINGS.put(method, mapping);
            }
        }

        Session session = c.getPlayer().getSession();
        if (obj instanceof Indexable) {
            Indexable ine = (Indexable) obj;
            PacketIndexPropertyChange pipc = new PacketIndexPropertyChange();
            pipc.setNs(c.getNs());
            pipc.setType(Packet.Type.st);
            pipc.setPn(mapping.propertyName);
            pipc.setPt(mapping.propertyType);
            pipc.setVal(args[0]);
            pipc.setIdx(ine.getIndex());
            session.write(pipc);
        } else {
            PacketPropertyChange ppc = new PacketPropertyChange();
            ppc.setNs(c.getNs());
            ppc.setType(Packet.Type.st);
            ppc.setPn(mapping.propertyName);
            ppc.setPt(mapping.propertyType);
            ppc.setVal(args[0]);
            session.write(ppc);
        }
        return rs;
    }

    private PropertyMapping newMapping(Method method) {
        PropertyMapping mapping = new PropertyMapping();
        mapping.propertyName = method.getName().substring(3, 4).toLowerCase()
                + method.getName().substring(4);

        Class<?> propertyClass = method.getParameterTypes()[0];
        if (Boolean.TYPE == propertyClass || Boolean.class == propertyClass) {
            mapping.propertyType = DomainUtils.BOOLEAN;
        } else if (Character.TYPE == propertyClass || Character.class == propertyClass) {
            mapping.propertyType = DomainUtils.CHAR;
        } else if (Byte.TYPE == propertyClass || Byte.class == propertyClass) {
            mapping.propertyType = DomainUtils.BYTE;
        } else if (Short.TYPE == propertyClass || Short.class == propertyClass) {
            mapping.propertyType = DomainUtils.SHORT;
        } else if (Integer.TYPE == propertyClass || Integer.class == propertyClass) {
            mapping.propertyType = DomainUtils.INT;
        } else if (Float.TYPE == propertyClass || Float.class == propertyClass) {
            mapping.propertyType = DomainUtils.FLOAT;
        } else if (Long.TYPE == propertyClass || Long.class == propertyClass) {
            mapping.propertyType = DomainUtils.LONG;
        } else if (Double.TYPE == propertyClass || Double.class == propertyClass) {
            mapping.propertyType = DomainUtils.DOUBLE;
        } else if (String.class == propertyClass
                || Appendable.class.isAssignableFrom(propertyClass)) {
            mapping.propertyType = DomainUtils.STRING;
        } else if (Collection.class.isAssignableFrom(propertyClass) || propertyClass.isArray()) {
            mapping.propertyType = DomainUtils.ARRAY;
        } else {
            mapping.propertyType = DomainUtils.OBJECT;
        }
        return mapping;
    }

    private class PropertyMapping {

        String propertyName;
        int propertyType;
    }
}
