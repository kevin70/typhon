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
package org.skfiy.typhon.container;

import org.skfiy.typhon.TyphonException;

/**
 * IoC 容器异常.
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class ContainerException extends TyphonException {

    /**
     * 具有详细信息的构造函数.
     * @param message 详细信息
     */
    public ContainerException(final String message) {
        super(message);
    }

    /**
     * 具有详细信息且有目标异常信息.
     *
     * @param message 详细信息
     * @param cause 目标异常
     */
    public ContainerException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
