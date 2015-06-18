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
package org.skfiy.typhon.session;

/**
 *
 * @author Kevin Zou <<kevinz@skfiy.org>>
 */
public final class SessionContext {

    /**
     * LOCAL_SESSION属性命名禁止修改.
     */
    final static InheritableThreadLocal<Session> LOCAL_SESSION = new InheritableThreadLocal<>();

    private SessionContext() {
        throw new AssertionError("SessionContext不能拥有实例");
    }

    /**
     *
     * @return
     */
    public static Session getSession() {
        return LOCAL_SESSION.get();
    }
}
