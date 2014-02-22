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
package org.skfiy.typhon;

/**
 * Typhon 运行时异常类型定义.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TyphonException extends RuntimeException {

    /**
     * 默认构造函数.
     */
    public TyphonException() {
    }

    /**
     * 具有详细信息的构造函数.
     *
     * @param message 详细信息
     */
    public TyphonException(String message) {
        super(message);
    }

    /**
     * 具有目标例外的构造函数.
     *
     * @param cause 目标例外
     */
    public TyphonException(Throwable cause) {
        super(cause);
    }

    /**
     * 具有详细信息且有目标例外的构造函数.
     *
     * @param message 详细信息
     * @param cause 目标例外
     */
    public TyphonException(String message, Throwable cause) {
        super(message, cause);
    }
}
