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
package org.skfiy.typhon.jmx;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.http.XSLTProcessor;
import org.apache.commons.modeler.Registry;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.Lifecycle;
import org.skfiy.typhon.LifecycleEvent;
import org.skfiy.typhon.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MX4J MBean 管理监听器.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Mx4jListener implements LifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(Mx4jListener.class);
    private String host;
    private int port;
    private HttpAdaptor adapter;

    /**
     * 获取MX4J连接IP地址.
     *
     * @return IP地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 设置MX4J连接IP地址.
     *
     * @param host IP地址
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取MX4j连接端口.
     *
     * @return MX4J端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置MX4J连接端口.
     *
     * @param port MX4J端口
     */
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void execute(LifecycleEvent event) {
        if (Lifecycle.AFTER_INIT_EVENT.equals(event.getEvent())) {
            MBeanServer mserver = Registry.getRegistry(null, null).getMBeanServer();

            adapter = new HttpAdaptor();
            adapter.setHost(host);
            adapter.setPort(port);

            try {
                ObjectName oname = ObjectName.getInstance(
                        Globals.DEFAULT_MBEAN_DOMAIN, "name", "HttpAdaptor");
                mserver.registerMBean(adapter, oname);
                adapter.setProcessor(new XSLTProcessor());
                adapter.start();
            } catch (Exception ex) {
                // FIXME
                LOG.error("start mx4j...", ex);
            }
        } else if (Lifecycle.AFTER_DESTROY_EVENT.equals(event.getEvent())) {
            // If the HttpAdaptor is null, log a warning & return
            if (adapter == null) {
                return;
            }
            
            MBeanServer mserver = Registry.getRegistry(null, null).getMBeanServer();
            try {
                ObjectName oname = ObjectName.getInstance(
                        Globals.DEFAULT_MBEAN_DOMAIN, "name", "HttpAdaptor");
                mserver.unregisterMBean(oname);
                adapter.stop();
            } catch (Exception ex) {
                // FIXME
                LOG.error("stop mx4j...", ex);
            }
        }
    }
}
