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
package org.skfiy.typhon.kernel;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.lang3.ArrayUtils;
import org.skfiy.typhon.AbstractMBeanLifecycle;
import org.skfiy.typhon.Connector;
import org.skfiy.typhon.LifecycleException;
import org.skfiy.typhon.LifecycleState;
import org.skfiy.typhon.Server;
import org.skfiy.typhon.Service;

/**
 * 标准服务实现.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class StandardService extends AbstractMBeanLifecycle implements Service {

    private Server server;
    private String name;
    private Connector[] connectors = new Connector[0];
    private Lock lock = new ReentrantLock();

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addConnector(Connector connector) {
        try {
            lock.lock();
            connectors = ArrayUtils.add(connectors, connector);
            connector.setService(this);

            // FIXME
            if (!getState().isAvailable()) {
                return;
            }
            
            try {
                connector.start();
            } catch (LifecycleException e) {
                // FIXME
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeConnector(Connector connector) {
        try {
            lock.lock();
            int i = ArrayUtils.indexOf(connectors, connector);
            if (i < 0) {
                return;
            }
            connectors = ArrayUtils.remove(connectors, i);
            
            if (!connector.getState().isAvailable()) {
                return;
            }
            try {
                connector.stop();
            } catch (LifecycleException e) {
                // FIXME
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Connector[] findConnectors() {
        return ArrayUtils.clone(connectors);
    }

    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        fireLifecycleListener(START_EVENT);
        
        for (Connector conn : connectors) {
            conn.init();
            conn.start();
        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        fireLifecycleListener(STOP_EVENT);
        
        for (Connector conn : connectors) {
            conn.stop();
            conn.destroy();
        }
    }


    @Override
    protected String getMBeanDomain() {
        return null;
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return "name=StandardService";
    }
    
}
