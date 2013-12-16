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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import org.skfiy.typhon.Typhons;

/**
 * 脚本类加载器. 该加载器使用{@code Thread.currentThread().getContextClassLoader() }
 * 作为父{@code ClassLoader }.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
class ScriptClassLoader extends URLClassLoader {

    /**
     * 无参构造函数.
     */
    ScriptClassLoader() {
        super(new URL[]{}, Thread.currentThread().getContextClassLoader());
    }

    /**
     * 添加类文件至{@code ClassLoader }中.
     *
     * @param file 类文件
     */
    void addFile(File file) {
        addURL(Typhons.toURL(file));
    }
}
