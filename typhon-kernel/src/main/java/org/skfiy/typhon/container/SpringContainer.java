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
package org.skfiy.typhon.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.modeler.Registry;
import org.reflections.ReflectionUtils;
import org.skfiy.typhon.Component;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.util.MBeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Administrator
 */
public class SpringContainer implements Component, Container {

    private ApplicationContext ctx;
    
    @Override
    public void init() {
        ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:beans-spi.xml",
            "classpath:beans-kernel.xml", "classpath:beans-action.xml"});
        
        MBeanUtils.registerComponent(this, OBJECT_NAME, null);
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void destroy() {
        ((ConfigurableApplicationContext) ctx).close();
        
        // 注销Container MBean
        Registry.getRegistry(null, null).unregisterComponent(OBJECT_NAME);
    }

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    @Override
    public Collection<Class> getAllBindingClasses() {
        List<Class> classes = new ArrayList<>();
        for (String name : ctx.getBeanDefinitionNames()) {
            classes.add(ctx.getType(name));
        }
        return classes;
    }

    @Override
    public void injectMembers(Object obj) {
        ctx.getAutowireCapableBeanFactory().autowireBean(obj);
    }

}
