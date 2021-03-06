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

import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface RoleDatable {

    /**
     * 
     * @param player 
     */
    void initialize(Player player);
    
    /**
     *
     * @param player
     * @param roleData
     */
    void serialize(Player player, RoleData roleData);

    /**
     *
     * @param roleData
     * @param player
     */
    void deserialize(RoleData roleData, Player player);
}
