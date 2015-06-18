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
package org.skfiy.typhon.jmx;

import com.sun.jdmk.comm.AuthInfo;
import com.sun.jdmk.comm.HtmlAdaptorServer;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.commons.modeler.Registry;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.Lifecycle;
import org.skfiy.typhon.LifecycleEvent;
import org.skfiy.typhon.LifecycleListener;
import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.Typhons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class HtmlJmxListener implements LifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(Globals.CONSOLE_LOG_NAME);

    private int port = 8088;
    private String username;
    private String password;
    private HtmlAdaptorServer htmlAdaptorServer;

    @Override
    public void execute(LifecycleEvent event) {
        if (Lifecycle.AFTER_INIT_EVENT.equals(event.getEvent())) {
            MBeanServer mbs = Registry.getRegistry(null, null).getMBeanServer();
            ObjectName adapterName = Typhons.newObjectName(
                    "HtmlAdaptorServer:name=HtmlAdapter,port=" + port);
            
            System.setProperty("openjdmk.charset", System.getProperty("file.encoding"));
            htmlAdaptorServer = new HtmlAdaptorServer(port,
                    new AuthInfo[]{new AuthInfo(username, password)});
            htmlAdaptorServer.setMaxActiveClientCount(1);

            try {
                mbs.registerMBean(htmlAdaptorServer, adapterName);
            } catch (Exception ex) {
                throw new TyphonException(ex);
            }

            htmlAdaptorServer.start();
            LOG.debug("Html JMX server started on port {}", port);
        } else if (Lifecycle.AFTER_DESTROY_EVENT.equals(event.getEvent())) {
            if (htmlAdaptorServer != null) {
                htmlAdaptorServer.stop();
            }
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
