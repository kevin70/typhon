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
package org.skfiy.typhon.script;

import javax.management.ObjectName;
import org.skfiy.typhon.Component;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.Typhons;

/**
 * 脚本管理接口定义.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface ScriptManager extends Component {

    /**
     * 脚本管理{@code ObjectName }名称. 该对象可通过{@code MBeanServer }操作开放的接口.
     */
    ObjectName OBJECT_NAME = Typhons.newObjectName(Globals.DEFAULT_MBEAN_DOMAIN
            + ":name=ScriptManager");

    /**
     * 根据全限类定名获取脚本实例.
     *
     * @param <T> 实际的脚本类型, 该类型是{@link Script }实现
     * @param name 脚本名称
     * @return 脚本实例
     */
    <T extends Script> T getScript(String name);
}
