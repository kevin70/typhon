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
 * 在实现{@link Lifecycle}接口中使用.
 *
 * @author Kevin Zou <kevinz@skfiy.com>
 */
public class LifecycleException extends Exception {

    /**
     * 创建一个空信息的{@code LifecycleException}实例.
     */
    public LifecycleException() {
    }

    /**
     * 创建一个有信息的{@code LifecycleException}实例.
     *
     * @param message 异常详细信息
     */
    public LifecycleException(final String message) {
        super(message);
    }

    /**
     * 创建一个目标异常的{@code LifecycleException}实例.
     *
     * @param cause 目标异常对象
     */
    public LifecycleException(final Throwable cause) {
        super(cause);
    }

    /**
     * 创建一个有详细信息并且有目标异常的{@code LifecycleException}实例.
     *
     * @param message 异常详细信息
     * @param cause 目标异常对象
     */
    public LifecycleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
