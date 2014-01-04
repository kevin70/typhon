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
 * 抽象的空实现{@code RoleListener }. 该类不做任何实际有意义的操作.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class AbstractRoleListener implements RoleListener {

    @Override
    public void roleCreated(Role role) {
    }

    @Override
    public void roleLoaded(Role role) {
    }

    @Override
    public void roleUnloaded(Role role) {
    }

    @Override
    public void roleDestroyed(Role role) {
    }

}
