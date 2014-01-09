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
package org.skfiy.typhon.spi.role;

import com.alibaba.fastjson.JSON;
import org.skfiy.typhon.spi.cglib.CglibPlayerCallbackFilter;
import java.lang.reflect.Method;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import org.skfiy.typhon.domain.Normal;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.spi.cglib.DomainProxyCallback;
import org.skfiy.util.ReflectionUtils;
import org.skfiy.util.StringUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class NormalRoleDatable implements RoleDatable {

    private final Class<Normal> proxyClass;

    public NormalRoleDatable() {
        Enhancer enhancer = new Enhancer();
        enhancer.setUseCache(false);
        enhancer.setUseFactory(false);
        enhancer.setSuperclass(Normal.class);
        enhancer.setCallbackFilter(CglibPlayerCallbackFilter.INSTANCE);
        
        Class[] callbackTypes = {Callback.class, Callback.class};
        enhancer.setCallbackTypes(callbackTypes);
        proxyClass = enhancer.createClass();
        
        Method m = ReflectionUtils.findMethod(proxyClass, "CGLIB$SET_STATIC_CALLBACKS");
        Object[] callbacks = {null, DomainProxyCallback.INSTANCE};
        ReflectionUtils.invokeMethod(m, proxyClass, callbacks);
    }

    @Override
    public void serialize(Player player, RoleData roleData) {
        roleData.setNormalData(JSON.toJSONString(player.getNormal()));
    }

    @Override
    public void deserialize(RoleData roleData, Player player) {
        String data = roleData.getNormalData();
        Normal normal;
        if (StringUtils.isEmpty(data)) {
            try {
                normal = proxyClass.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("cglib: normal class", ex);
            }
        } else {
            normal = JSON.parseObject(data, proxyClass);
        }
        player.setNormal(normal);
    }

}
