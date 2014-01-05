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

import java.util.Collection;
import java.util.Set;
import org.skfiy.util.v8.ConcurrentHashMapV8;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface SessionManager {

    /**
     * 
     * @param sessionListener 
     */
    void addSessionListener(SessionListener sessionListener);
    
    /**
     * 
     * @param sessionListener 
     */
    void removeSessionListener(SessionListener sessionListener);
    
    /**
     * 
     * @return 
     */
    Set<SessionListener> findSessionListeners();
    
    /**
     * 
     * @param sid
     * @param session
     */
    void addSession(int sid, Session session);

    /**
     * 
     * @param sid
     * @return 
     */
    Session removeSession(int sid);
    
    /**
     * 
     * @param sid
     * @return 
     */
    Session getSession(int sid);
    
    /**
     * 
     * @param fun
     * @return 
     */
    Session searchSession(ConcurrentHashMapV8.Fun<Session, Session> fun);

    /**
     * 
     * @return 
     */
    Collection<Session> findSessions();
}
