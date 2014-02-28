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
package org.skfiy.typhon.agent;

import java.lang.instrument.Instrumentation;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

/**
 * Java进程代理实现.
 * 
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Agent {

    /**
     * 代理主函数入口. 通过传入的{@code str }{@code ObjectName }执行其{@code setInstrumentation }
     * 方法.
     * <p>
     * e.g.<pre>
     * public void setInstrumentation(Instrumentation instrumentation) {
     *  this.instrumentation = instrumentation;
     * }
     * </pre>
     * 
     * @param str 一个{@link ObjectName }字符串
     * @param inst {@link Instrumentation }实现
     * @throws Exception 异常
     */
    public static void agentmain(String str, Instrumentation inst) throws Exception {
        ObjectName objectName = ObjectName.getInstance(str);
        MBeanServer mbeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
        mbeanServer.invoke(objectName, "setInstrumentation",
                new Object[]{inst},
                new String[]{Instrumentation.class.getName()});
    }
}
