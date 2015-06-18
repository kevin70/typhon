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
package org.skfiy.typhon.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.packet.Packet;

/**
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
@Singleton
public class ReflectionDispatcherFactory implements DispatcherFactory {

    @Inject
    private Container container;
    
    private ReflectionDispatcher dispatcher;
    private ActionHelper helper;

    @Override
    public Dispatcher getDispatcher() {
        if (dispatcher == null) {
            helper = new ActionHelper(container);
            dispatcher = new ReflectionDispatcher();
        }
        return dispatcher;
    }

    @Override
    public Class<?> getPacketClass(String ns) {
        return helper.getPacketClass(ns);
    }

    class ReflectionDispatcher implements Dispatcher {

        Map<String, ActionMapping> actionMappings;

        ReflectionDispatcher() {
            actionMappings = new HashMap<>();
            for (List<ActionMapping> ams
                    : helper.getActionMappings().values()) {
                for (ActionMapping am : ams) {
                    actionMappings.put(am.getNs(), am);
                }
            }
        }

        @Override
        public void dispatch(String ns, Packet packet) {
            ActionMapping am = actionMappings.get(ns);
            if (am == null) {
                throw new NoNamespaceDefException("No namespace [" + ns + "] define");
            }
            
            Object obj = container.getInstance(am.getMethod().getDeclaringClass());
            
            try {
                am.getMethod().invoke(obj, new Object[] {packet});
            } catch (InvocationTargetException e) {
                throw findException(e);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new DispatcherException(e);
            }
        }
        
        private RuntimeException findException(Throwable t) {
            if (t.getCause() == null) {
                if (!(t instanceof RuntimeException)) {
                    throw new RuntimeException(t);
                }
            } else if (t.getCause() != null) {
                return findException(t.getCause());
            }
            return (RuntimeException) t;
        }
    }
}
