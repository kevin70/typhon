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
package org.skfiy.typhon.spi.role;

import org.skfiy.typhon.domain.Role;

/**
 * 角色监听器.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface RoleListener {

    /**
     * 创建角色.
     *
     * @param role 角色实例
     */
    void roleCreated(Role role);

    /**
     * 加载角色.
     *
     * @param role 角色实例
     */
    void roleLoaded(Role role);

    /**
     * 卸载角色.
     *
     * @param role 角色实例
     */
    void roleUnloaded(Role role);

    /**
     * 销毁角色. FIXME 当前版本不支持此方法实现.
     *
     * @param role 角色实例
     */
    void roleDestroyed(Role role);
}
