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

import com.google.common.collect.Lists;
import org.skfiy.typhon.Container;
import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.modeler.Registry;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.skfiy.typhon.Component;
import org.skfiy.typhon.ConfigurationException;
import org.skfiy.typhon.util.MBeanUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Guice IoC Bean容器实现.
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
public class GuiceContainer implements Component, Container {

    @Inject
    private Injector injector;

    @Override
    public void init() {
        Injector inj = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Container.class).toInstance(GuiceContainer.this);
            }
        }, new Jsr250Module(), new XmlModule()
        );
        
        MBeanUtils.registerComponent(this, OBJECT_NAME, null);
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void destroy() {
        Destroyable destroyable = injector.getInstance(Destroyable.class);
        Set<Map.Entry<Key<?>, Binding<?>>> entries = injector.getAllBindings().entrySet();
        for (Map.Entry<Key<?>, Binding<?>> entry : entries) {
            Binding<?> binding = entry.getValue();
            destroyable.destroy(binding.getSource());
        }

        injector = null;
        // 注销Container MBean
        Registry.getRegistry(null, null).unregisterComponent(OBJECT_NAME);
    }

    @Override
    public <T> T getInstance(final Class<T> clazz) {
        if (injector == null) {
            throw new IllegalStateException("Don't init container.");
        }

        return injector.getInstance(clazz);
    }

    @Override
    public Collection<Class> getAllBindingClasses() {
        List<Class> classes = Lists.newArrayList();
        Set<Map.Entry<Key<?>, Binding<?>>> entries = injector.getAllBindings().entrySet();
        for (Map.Entry<Key<?>, Binding<?>> entry : entries) {
            Key<?> key = entry.getKey();
            classes.add(key.getTypeLiteral().getRawType());
        }
        return classes;
    }

    @Override
    public void injectMembers(final Object obj) {
        if (injector == null) {
            throw new IllegalStateException("Don't init container.");
        }

        injector.injectMembers(obj);
    }
    
    /**
     * 获取Guice Injector实例.
     *
     * @return Injector实例
     */
    public Injector getInjector() {
        return injector;
    }

    class XmlModule extends AbstractModule {

        @Override
        protected void configure() {
            
            // 加载classpath环境下所有bean-*.xml文件
            ClassLoader classLoader = getClass().getClassLoader();
            ConfigurationBuilder builder = ConfigurationBuilder.build(
                    new ResourcesScanner(), classLoader);
            Reflections refs = new Reflections(builder);
            Set<String> resources = refs.getResources(Pattern.compile("beans-.*\\.xml"));

            for (String res : resources) {
                URL url = classLoader.getResource(res);
                InputStream in = null;
                
                try {
                    in = url.openStream();
                    configure0(in);
                } catch (IOException ex) {
                    throw new ConfigurationException(ex);
                } catch (Exception ex) {
                    throw new ConfigurationException("解析" + url + "文件失败", ex);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        }
        
        private void configure0(InputStream stream) throws Exception {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(stream);
            Element root = document.getDocumentElement();
            NodeList nodes = root.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                switch (node.getNodeName()) {
                    case "property":
                        bindProperty(node);
                        break;
                    case "bean":
                        bindBean(node);
                        break;
                    case "multi-set":
                        bindSetBean(node);
                        break;
                    default:
                        // throw new ConfigurationException(
                        //         "beans.xml中没有定义[" + node.getNodeName() + "]节点");
                }
            }
        }

        private void bindProperty(Node node) {
            NamedNodeMap attrMap = node.getAttributes();
            Node nameNode = attrMap.getNamedItem("name");
            Node valueNode = attrMap.getNamedItem("value");
            Node typeNode = attrMap.getNamedItem("type");

            String name = nameNode.getNodeValue();
            String value = valueNode.getNodeValue();
            String typeName = typeNode.getNodeValue();

            switch (typeName) {
                case "byte":
                case "java.lang.Byte":
                    bind(Byte.class).annotatedWith(Names.named(name))
                            .toInstance(Byte.valueOf(value));
                    break;
                case "boolean":
                case "java.lang.Boolean":
                    bind(Boolean.class).annotatedWith(Names.named(name))
                            .toInstance(Boolean.valueOf(value));
                    break;
                case "int":
                case "java.lang.Integer":
                    bind(Integer.class).annotatedWith(Names.named(name))
                            .toInstance(Integer.valueOf(value));
                    break;
                case "long":
                case "java.lang.Long":
                    bind(Long.class).annotatedWith(Names.named(name))
                            .toInstance(Long.valueOf(value));
                    break;
                case "short":
                case "java.lang.Short":
                    bind(Short.class).annotatedWith(Names.named(name))
                            .toInstance(Short.valueOf(value));
                    break;
                case "double":
                case "java.lang.Double":
                    bind(Double.class).annotatedWith(Names.named(name))
                            .toInstance(Double.valueOf(value));
                    break;
                default:
                    bind(String.class).annotatedWith(Names.named(name))
                            .toInstance(value);
                    break;
            }
        }

        protected void bindBean(Node node) {
            NamedNodeMap attrMap = node.getAttributes();
            Node typeAttr = attrMap.getNamedItem("type");
            Node classAttr = attrMap.getNamedItem("class");
            Node lazyAttr = attrMap.getNamedItem("lazy");
            boolean lazy = false;
            if (lazyAttr != null) {
                try {
                    lazy = Boolean.valueOf(lazyAttr.getNodeValue());
                } catch (Exception e) {
                }
            }
            
            Class type = null;
            if (typeAttr != null) {
                try {
                    type = Class.forName(typeAttr.getNodeValue());
                } catch (ClassNotFoundException ex) {
                    throw new ConfigurationException(
                            typeAttr.getNodeName()
                            + " > type 属性值配置错误["
                            + typeAttr.getNodeValue() + "]", ex);
                }
            }

            Class clazz = null;
            try {
                clazz = Class.forName(classAttr.getNodeValue());
            } catch (ClassNotFoundException ex) {
                throw new ConfigurationException(
                        classAttr.getNodeName()
                        + " > class 属性值配置错误["
                        + classAttr.getNodeValue() + "]", ex);
            }

            // 如果没有接口，则直接绑定实现类
            ScopedBindingBuilder sbber;
            if (type == null) {
                sbber = bind(clazz);
            } else {
                sbber = bind(type).to(clazz);
            }
            
            if (!lazy) {
                sbber.asEagerSingleton();
            }
        }
        
        protected void bindSetBean(Node node) {
            String typeStr = node.getAttributes().getNamedItem("type").getNodeValue();
            Class type = null;
            
            try {
                type = Class.forName(typeStr);
            } catch (ClassNotFoundException ex) {
                throw new ConfigurationException(
                        node.getNodeName()
                        + " > type 属性值配置错误["
                        + typeStr + "]", ex);
            }
            
            Multibinder multibinder = Multibinder.newSetBinder(binder(), type);
            
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node elementNode = nodeList.item(i);
                if ("element".equals(elementNode.getNodeName())) {
                    String clazzStr = elementNode.getAttributes()
                            .getNamedItem("class").getNodeValue();
                    try {
                        Class clazz = Class.forName(clazzStr);
                        multibinder.addBinding().to(clazz);
                    } catch (ClassNotFoundException ex) {
                        throw new ConfigurationException(
                                node.getNodeName() + " > "
                                + elementNode.getNodeName()
                                + " > class 属性值配置错误["
                                + typeStr + "]", ex);
                    }
                }
            }
        }
    }
}