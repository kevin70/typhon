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

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.commons.modeler.Registry;

/**
 * 抽象的MBeanLifecycle定义.该类实现{@code MBeanRegistration}接口.
 * 如果目标实现需要注册当前实例至MBeanServer应该继承该类.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class AbstractMBeanLifecycle extends AbstractLifecycle
        implements MBeanRegistration {

    private String domain;
    private ObjectName oname;

    /**
     * 获取当前对象的MBean域名.
     * 如果返回{@code NULL}则使用默认域名{@link Globals#DEFAULT_MBEAN_DOMAIN}.
     *
     * @return 返回{@code NULL}则使用默认域名{@link Globals#DEFAULT_MBEAN_DOMAIN}
     */
    protected abstract String getMBeanDomain();

    /**
     * {@code ObjectName}属性.
     *
     * @return 一个不等于{@code NULL}的字符
     */
    protected abstract String getObjectNameKeyProperties();

    @Override
    public ObjectName preRegister(final MBeanServer server, final ObjectName name)
            throws Exception {
        this.oname = name;
        this.domain = name.getDomain();
        return oname;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() throws Exception {
    }

    @Override
    public void postDeregister() {
    }

    @Override
    protected void initInternal() throws LifecycleException {
        if (oname == null) {
            oname = register(this, getObjectNameKeyProperties());
        }
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        unregister(oname);
    }

    /**
     * 获取{@code ObjectName}实例.
     *
     * @return {@code ObjectName}实例
     */
    protected final ObjectName getObjectName() {
        return oname;
    }

    /**
     * 获取当前对象的MBean域名.
     * 如果返回{@code NULL}则使用默认域名{@link Globals#DEFAULT_MBEAN_DOMAIN}.
     *
     * @return 返回{@code NULL}则使用默认域名{@link Globals#DEFAULT_MBEAN_DOMAIN}
     */
    protected final String getDomain() {
        if (domain == null) {
            domain = getMBeanDomain();
        }
        if (domain == null) {
            domain = Globals.DEFAULT_MBEAN_DOMAIN;
        }
        return domain;
    }

    /**
     * 设置MBean域名.
     *
     * @param domain MBean域名
     */
    public final void setDomain(final String domain) {
        this.domain = domain;
    }
    
    /**
     * 注册MBean实例.
     *
     * @param obj 注册进MBeanServer的目标对象
     * @param objectNameKeyProperties {@code ObjectName}名称属性
     * @return 最终被注册的{@code ObjectName}
     * @throws LifecycleException 当注册失败时将会得到{@code LifecycleException}
     */
    protected final ObjectName register(final Object obj,
            final String objectNameKeyProperties) throws LifecycleException {

        String name = getDomain() + ":" + objectNameKeyProperties;

        try {
            ObjectName on = new ObjectName(name);

            Registry.getRegistry(null, null).registerComponent(obj, on, null);
            return on;
        } catch (Exception e) {
            throw new LifecycleException("注册" + name + " MBean时错误", e);
        }

    }

    /**
     * 移除注册MBean实例.
     *
     * @param on 已经注册的{@code ObjectName}
     */
    protected final void unregister(ObjectName on) {
        // If null ObjectName, just return without complaint
        if (on == null) {
            return;
        }

        Registry.getRegistry(null, null).unregisterComponent(on);
    }
}
