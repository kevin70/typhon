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

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.commons.modeler.Registry;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.Lifecycle;
import org.skfiy.typhon.LifecycleEvent;
import org.skfiy.typhon.LifecycleListener;
import org.skfiy.typhon.TyphonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class BuiltinJmxListener implements LifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(BuiltinJmxListener.class);

    private String host = "localhost";
    private int port = 1090;

    private JMXConnectorServer jcs;

    @Override
    public void execute(LifecycleEvent event) {
        if (Lifecycle.BEFORE_INIT_EVENT.equals(event.getEvent())) {
            
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            Registry.getRegistry(null, null).setMBeanServer(mbs);
        } else if (Lifecycle.AFTER_INIT_EVENT.equals(event.getEvent())) {
            // Ensure cryptographically strong random number generator used
            // to choose the object number - see java.rmi.server.ObjID
            //
            System.setProperty("java.rmi.server.randomIDs", "true");
            
            // Start an RMI registry on port.
            try {
                LocateRegistry.createRegistry(port);
                LOG.info("Create RMI registry on port {}", port);
            } catch (RemoteException ex) {
                LOG.error("Create RMI registry error", ex);
                throw new TyphonException(ex);
            }

            Map<String, Object> env = new HashMap<>();
            
            // Provide the password file used by the connector server to
            // perform user authentication. The password file is a properties
            // based text file specifying username/password pairs.
            //
            // File file = new File(System.getProperty("typhon.home"), "bin/jmxremote.password");
            // env.put("com.sun.management.jmxremote.password.file", file.getAbsolutePath());
            
            try {
                jcs = JMXConnectorServerFactory.newJMXConnectorServer(newUrl(), env,
                        ManagementFactory.getPlatformMBeanServer());
                jcs.start();
            } catch (IOException ex) {
                LOG.error("start JMXConnectorServer...", ex);
                throw new TyphonException(ex);
            }
        } else if (Lifecycle.AFTER_DESTROY_EVENT.equals(event.getEvent())) {
            if (jcs != null) {
                try {
                    jcs.stop();
                } catch (IOException ex) {
                    LOG.error("stop JMXConnectorServer...", ex);
                    throw new TyphonException(ex);
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

    private JMXServiceURL newUrl() {
        try {
            return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
                    + host + ":" + port + "/jmxrmi");
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
