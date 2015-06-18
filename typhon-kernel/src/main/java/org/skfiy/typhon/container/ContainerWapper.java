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
import org.skfiy.typhon.Container;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 *
 * @author Administrator
 */
public class ContainerWapper implements Container {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return beanFactory.getBean(clazz);
    }

    @Override
    public Collection<Class> getAllBindingClasses() {
        ListableBeanFactory bf = (ListableBeanFactory) beanFactory;
        
        List<Class> classes = new ArrayList<>();
        for (String name : bf.getBeanDefinitionNames()) {
            classes.add(bf.getType(name));
        }
        return classes;
    }

    @Override
    public void injectMembers(Object obj) {
//        MBeanServer mbs = MBeanUtils.REGISTRY.getMBeanServer();
//        try {
//            mbs.invoke(Container.OBJECT_NAME,
//                    "injectMembers",
//                    new Object[]{obj},
//                    new String[]{Object.class.getName()});
//        } catch (Exception ex) {
//            throw new TyphonException(ex);
//        }
        beanFactory.autowireBean(obj);
    }

}
