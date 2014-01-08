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

package org.skfiy.typhon.spi.cglib;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.skfiy.typhon.domain.Changeable;
import org.skfiy.typhon.session.Session;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class DomainProxyCallback implements MethodInterceptor {

    public static DomainProxyCallback INSTANCE = new DomainProxyCallback();
    
    private DomainProxyCallback() {
        
    }
    
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        proxy.invokeSuper(obj, args);
        
        Changeable c = (Changeable) obj;
        if (c.getPlayer() == null) {
            return null;
        }
        
        Session session = c.getPlayer().getSession();
        
        return null;
    }
    
}
