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

/**
 * 脚本异常类型.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class ScriptException extends RuntimeException {

    /**
     * 无参构造函数.
     */
    public ScriptException() {
    }

    /**
     * 带有详细信息的构造函数.
     *
     * @param message 详细信息
     */
    public ScriptException(String message) {
        super(message);
    }

    /**
     * 带有详细信息且有起因例外的构造函数.
     *
     * @param message 详细信息
     * @param cause 起因例外
     */
    public ScriptException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带有起因例外的构造函数.
     *
     * @param cause 起因例外
     */
    public ScriptException(Throwable cause) {
        super(cause);
    }
}
