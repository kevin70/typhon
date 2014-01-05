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

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import org.skfiy.typhon.Component;
import org.skfiy.typhon.Container;
import org.skfiy.util.ReflectionUtils;

/**
 *
 * @author kevin
 */
class Jsr250Module extends AbstractModule {

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new TypeListener() {

            @Override
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> encounter) {
                Class<? super I> type = typeLiteral.getRawType();
                if (Container.class.isAssignableFrom(type)) {
                    return;
                }
                
                if (Component.class.isAssignableFrom(type)) {
                    register(encounter, ReflectionUtils.findMethod(type, "init"));
                    return;
                }
                
                Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(type);
                for (final Method m : methods) {
                    if (m.getAnnotation(PostConstruct.class) != null) {
                        register(encounter, m);
                        break;
                    }
                }
            }
        });

        bind(Destroyable.class).to(Jsr250PreDestroy.class);
    }
    
    private <I> void register(TypeEncounter<I> encounter, final Method method) {
        encounter.register(new InjectionListener<I>() {
            @Override
            public void afterInjection(I injectee) {
                ReflectionUtils.invokeMethod(method, injectee);
            }
        });
    }
    
}
