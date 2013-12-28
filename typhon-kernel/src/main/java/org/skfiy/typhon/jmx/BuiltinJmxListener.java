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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.commons.modeler.Registry;
import org.skfiy.typhon.Lifecycle;
import org.skfiy.typhon.LifecycleEvent;
import org.skfiy.typhon.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class BuiltinJmxListener implements LifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(BuiltinJmxListener.class);

    private String host = "localhost";
    private int port = 9090;

    private JMXConnectorServer connectorServer;

    @Override
    public void execute(LifecycleEvent event) {
        if (Lifecycle.AFTER_INIT_EVENT.equals(event.getEvent())) {
            MBeanServer mbeanServer = Registry.getRegistry(null, null).getMBeanServer();
            try {
                connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(
                        newJmxUrl(), null, mbeanServer);
                connectorServer.start();
            } catch (IOException ex) {
                LOG.error("start JMXConnectorServer...", ex);
            }
        } else if (Lifecycle.AFTER_DESTROY_EVENT.equals(event.getEvent())) {
            if (connectorServer != null) {
                try {
                    connectorServer.stop();
                } catch (IOException ex) {
                    LOG.error("stop JMXConnectorServer...", ex);
                }
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private JMXServiceURL newJmxUrl() {
        try {
            return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
                    + host + ":" + port + "/server");
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
