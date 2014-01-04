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

/**
 * 调试脚本包装器.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
final class ScriptWapper {

    private File scriptSource;
    private File scriptTarget;
    private Script scriptObject;
    private long lastModified;

    /**
     * 通过源文件, 目标文件, 及脚本实例构造{@code ScriptWapper }实例.
     *
     * @param source 源文件
     * @param target 目标文件
     * @param obj 脚本实例
     */
    ScriptWapper(File source, File target, Script obj) {
        scriptSource = source;
        scriptTarget = target;
        scriptObject = obj;
        updateLastModified();
    }

    /**
     * 脚本源文件.
     *
     * @return 源文件
     */
    File getScriptSource() {
        return scriptSource;
    }

    /**
     * 脚本目标文件.
     *
     * @return 目标文件
     */
    File getScriptTarget() {
        return scriptTarget;
    }

    /**
     * 脚本实例.
     *
     * @return 脚本实例
     */
    Script getScriptObject() {
        return scriptObject;
    }

    /**
     * 获取当前实例最后一次编译的时间.
     *
     * @return 最后一次编译的时间
     */
    long getLastModified() {
        return lastModified;
    }

    /**
     * 更新当前脚本最后编译的时间.
     */
    void updateLastModified() {
        lastModified = scriptSource.lastModified();
    }
}
