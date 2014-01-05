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
import org.skfiy.typhon.LifecycleException;
import org.skfiy.typhon.LifecycleState;
import org.skfiy.typhon.Server;
import org.skfiy.typhon.Service;

/**
 * 标准服务器实现.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class StandardServer extends AbstractMBeanLifecycle implements Server {

    private final Lock lock = new ReentrantLock();
    
    private String host = "localhost";
    private int port = 7432;
    private String shutdown = "SHUTDOWN";
    
    private Service[] services = new Service[0];

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getShutdown() {
        return shutdown;
    }
    
    @Override
    public void setShutdown(String shutdown) {
        this.shutdown = shutdown;
    }

    @Override
    public synchronized void addService(Service service) {
        try {
            lock.lock();
            services = ArrayUtils.add(services, service);
            service.setServer(this);
            
            // FIXME
            if (!getState().isAvailable()) {
                return;
            }
            
            try {
                service.start();
            } catch (LifecycleException e) {
                // FIXME
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeService(Service service) {
        try {
            lock.lock();
            
            int i = ArrayUtils.indexOf(services, service);
            if (i < 0) {
                return;
            }
            services = ArrayUtils.remove(services, i);

            // FIXME
            if (!service.getState().isAvailable()) {
                return;
            }
            try {
                service.stop();
            } catch (LifecycleException e) {
                // FIXME
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Service[] findServices() {
        return ArrayUtils.clone(services);
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        fireLifecycleListener(START_EVENT);
        
        for (Service service : services) {
            service.init();
            service.start();
        }
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        fireLifecycleListener(STOP_EVENT);
        
        for (Service service : services) {
            service.stop();
            service.destroy();
        }
    }

    @Override
    protected String getMBeanDomain() {
        return null;
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return "name=StandardServer";
    }

}
