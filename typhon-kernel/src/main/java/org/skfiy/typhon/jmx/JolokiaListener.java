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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jolokia.jvmagent.JolokiaServer;
import org.jolokia.jvmagent.JolokiaServerConfig;
import org.skfiy.typhon.Lifecycle;
import org.skfiy.typhon.LifecycleEvent;
import org.skfiy.typhon.LifecycleListener;
import org.skfiy.typhon.TyphonException;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class JolokiaListener implements LifecycleListener {

    private String host;
    private int port = 1090;

    private JolokiaServer jolokiaServer;

    @Override
    public void execute(LifecycleEvent event) {
        if (Lifecycle.AFTER_INIT_EVENT.equals(event.getEvent())) {
            Map<String, String> configMap = new HashMap<>();
            configMap.put("host", host);
            configMap.put("port", String.valueOf(port));

            JolokiaServerConfig serverConfig = new JolokiaServerConfig(configMap);

            try {
                jolokiaServer = new JolokiaServer(serverConfig, true);
                jolokiaServer.start();
            } catch (IOException ex) {
                throw new TyphonException(ex);
            }
        } else if (Lifecycle.AFTER_DESTROY_EVENT.equals(event.getEvent())) {
            if (jolokiaServer != null) {
                jolokiaServer.stop();
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

}
