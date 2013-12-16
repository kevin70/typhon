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
package org.skfiy.typhon.dispatcher;

import com.google.common.base.Predicate;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;
import org.reflections.adapters.JavaReflectionAdapter;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.annotation.Action;

/**
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
class ActionHelper {

    private Map<Class<?>, List<ActionMapping>> actionMappings;
    private Map<String, Class<Packet>> packetClass;
    
    ActionHelper(Container container) {
        actionMappings = new HashMap<>();
        packetClass = new HashMap<>();
        
        // 扫描已经被管理的所有Class
        final Map<String, Object> scanUrls = new HashMap<>();
        for (Class clazz : container.getAllBindingClasses()) {
            scanUrls.put(clazz.getCanonicalName() + ".class", null);
        }
        
        MethodAnnotationsScanner methodScanner = new MethodAnnotationsScanner();
        ConfigurationBuilder builder = ConfigurationBuilder.build(methodScanner,
                new Predicate<String>() {
            @Override
            public boolean apply(String t) {
                return scanUrls.containsKey(t);
            }
        });
        builder.setMetadataAdapter(new JavaReflectionAdapter());
        
        builder.setUrls(ClasspathHelper.forPackage("org.skfiy.typhon"));
        
        // 扫描Action注解描述的Method
        Reflections refls = builder.build();
        Set<Method> methods = refls.getMethodsAnnotatedWith(Action.class);
        for (Method m : methods) {
            Class clazz = m.getDeclaringClass();

            List<ActionMapping> anns;
            if (actionMappings.containsKey(clazz)) {
                anns = actionMappings.get(clazz);
            } else {
                anns = new ArrayList<>();
                actionMappings.put(clazz, anns);
            }

            Action ac = m.getAnnotation(Action.class);

            // @Action 注解映射详细
            ActionMapping mapping = new ActionMapping();
            mapping.setNs(ac.value());
            mapping.setMethod(m);
            mapping.setPacketClass(m.getParameterTypes()[0]);
            anns.add(mapping);

            packetClass.put(ac.value(), mapping.getPacketClass());
        }
    }
    
    Map<Class<?>, List<ActionMapping>> getActionMappings() {
        return actionMappings;
    }
    
    Class<Packet> getPacketClass(String ns) {
        return packetClass.get(ns);
    }
}
