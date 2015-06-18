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

package org.skfiy.typhon.repository;

import java.util.List;

import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.domain.VacantData;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface RoleRepository {

    /**
     *
     * @param rid
     */
    void delete(int rid);

    /**
     *
     * @param name
     * @return
     */
    int existsName(String name);

    /**
     *
     * @param rid
     * @return      *
     */
    Role get(int rid);

    /**
     * 根据Role ID查询{@code RoleData }. 如果指定的rid没有查询到对应的{@code RoleData }
     * 则将会收到一个{@link ObjectNotFoundException }.
     *
     * @param rid Role 标识
     * @return 一个合法的{@code RoleData }
     */
    RoleData loadRoleData(int rid);

    /**
     *
     * @param role
     */
    void save(Role role);

    /**
     *
     * @param role
     */
    void update(Role role);
    
    /**
     * 更新角色名称.
     *
     * @param rid 角色ID
     * @param newName 新的角色名称
     */
    void updateRoleName(int rid, String newName);
    
    /**
     * 
     * @param rid
     * @param diamond 
     */
    void updateDiamond(int rid, int diamond);
    
    /**
     *
     * @param rid
     */
    void updateLastLoginedTime(int rid);
    
    /**
     * 
     * @param roleName
     * @return list<role>
     */
    List<Role> findRoles(String roleName,int number);
    
    //=========================================================================================//
    //=========================================================================================//
    //===========================           Role Data             =============================//
    //=========================================================================================//
    //=========================================================================================//
    void update(RoleData roleData);
    
    /**
     *
     * @param rid
     * @return
     */
    VacantData loadVacantData(int rid);
}
