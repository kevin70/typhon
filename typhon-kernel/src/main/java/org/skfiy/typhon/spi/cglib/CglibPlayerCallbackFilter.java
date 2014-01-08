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
import net.sf.cglib.proxy.CallbackFilter;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class CglibPlayerCallbackFilter implements CallbackFilter {

    public static CglibPlayerCallbackFilter INSTANCE = new CglibPlayerCallbackFilter();

    private CglibPlayerCallbackFilter() {
    }

    @Override
    public int accept(Method method) {
        // 不代理非JDK标准的set的方法
        //
        if (method.getReturnType() != Void.TYPE
                || method.getParameterTypes().length != 1
                || !method.getName().startsWith("set")) {
            return 0;
        }
        return 1;
    }

}
