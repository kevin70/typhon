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

import org.skfiy.typhon.domain.CDKeyObject;
import org.skfiy.typhon.domain.User;
import org.skfiy.typhon.packet.Platform;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface UserRepository {

    /**
     *
     * @param uid
     */
    void delete(int uid);

    /**
     *
     * @param uid
     * @return
     */
    User findByUid(final int uid);

    /**
     *
     * @param username
     * @return
     */
    User findByUsername(final String username);

    /**
     * 
     * @param username
     * @param password
     * @return 
     */
    int save(final String username, final String password);
    
    /**
     *
     * @param username
     * @param password
     * @param platform
     * @return
     */
    int save(final String username, final String password, final Platform platform);

    /**
     *
     * @param uid
     * @return
     */
    boolean updateLastAccessedTime(int uid);
    
    void updatePassowrd(int uid, String newPassword);

    /**
     *
     * @param CDKEYObject
     * @return
     */
//    void saveCDKEY(CDKeyObject object);
    /**
     *
     * @param cdkey
     * @return CDKeyObject
     */
    CDKeyObject findByCDKEY(String key);

    /**
     *
     * @param cdkey
     * @return
     */
    boolean updateCDKey(String key);

    /**
     *
     * @param plog
     * @return
     */
    void savePlayerLog(int uid, int changeValue, String changeType, String description);
    
    /**
     * 
     * @return 
     */
    int getNextTempId();
}
