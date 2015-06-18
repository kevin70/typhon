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

import org.skfiy.typhon.session.Session;

/**
 * 脚本接口定义.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Script {

    /**
     * 执行脚本实例.
     *
     * @param session 用户会话{@code Session }
     * @param obj 附加实例
     * @return 
     */
    Object invoke(Session session, Object obj);
}
