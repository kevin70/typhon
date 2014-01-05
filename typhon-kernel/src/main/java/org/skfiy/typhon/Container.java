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

import java.util.Collection;
import javax.management.ObjectName;

/**
 * Typhon IoC Bean对象容器接口定义. {@code Container }定义{@link #getInstance(java.lang.Class) }
 * 通过{@code Class }对象获取指定的Bean实例, {@link #injectMembers(java.lang.Object) }为指定对象注入
 * 依赖对象.
 * <p>如果被管理的对象是{@link Component }接口的子类实现, 那在{@code Container }初始时会自动初始化
 * {@code Component }. 在{@code Container }销毁之前{@code Component }会自动销毁.
 * 
 * @see javax.inject.Inject
 * @see javax.inject.Singleton
 * @see javax.inject.Named
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Container {

    /**
     * {@code Container } 组件名称.
     */
    String COMPONENT_NAME = "Container";
    
    /**
     * 在注册MBean时必须采用该{@code ObjectName }定义.
     */
    ObjectName OBJECT_NAME = Typhons.newObjectName(Globals.DEFAULT_MBEAN_DOMAIN
            + ":name=" + COMPONENT_NAME);

    /**
     * 通过{@code Class }对象获取管理的Bean对象. 如果指定的Bean被管理则直接返回Bean对象,
     * 反之将得到一个{@code null }.
     *
     * @param <T> Bean的类型
     * @param clazz Bean管理的{@code Class }
     * @return Bean对象/null
     */
    <T> T getInstance(Class<T> clazz);
    
    /**
     * 获取{@code Container }管理的所有{@code Class }.
     *
     * @return 被管理的{@code Class }集合
     */
    Collection<Class> getAllBindingClasses();

    /**
     * 依赖注入指定的Bean实例. 通过{@link javax.inject.Inject }注解来注入依赖对象.
     * 或者通过{@link javax.inject.Named }来获取指定属性名称的对象.
     * <pre>
     * e.g.
     * public class Example {
     * 
     *      {@code @Inject }
     *      private Role role;
     * 
     *      {@code @Inject }
     *      {@code @Named("first.name") }
     *      private String firstName;
     * 
     * }
     * </pre>
     *
     * @param obj 需要依赖注入的对象
     * @see javax.inject.Inject
     * @see javax.inject.Named
     */
    void injectMembers(Object obj);
}
