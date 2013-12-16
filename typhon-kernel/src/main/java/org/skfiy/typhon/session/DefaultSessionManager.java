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
import java.util.Collections;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.util.v8.ConcurrentHashMapV8;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class DefaultSessionManager implements SessionManager {

    @Inject
    private Set<SessionListener> sessionListeners;
    private ConcurrentHashMapV8<Integer, Session> sessions;

    public DefaultSessionManager() {
        sessions = new ConcurrentHashMapV8<>();
    }

    @Override
    public void addSessionListener(SessionListener sessionListener) {
        sessionListeners.add(sessionListener);
    }

    @Override
    public void removeSessionListener(SessionListener sessionListener) {
        sessionListeners.remove(sessionListener);
    }

    @Override
    public Set<SessionListener> findSessionListeners() {
        return Collections.unmodifiableSet(sessionListeners);
    }

    @Override
    public void addSession(int sid, Session session) {
        sessions.putIfAbsent(sid, session);
        // fire
        for (SessionListener sessionListener : sessionListeners) {
            sessionListener.sessionCreated(session);
        }
    }
    @Override
    public Session removeSession(int sid) {
        Session session = sessions.remove(sid);
        // fire
        for (SessionListener sessionListener : sessionListeners) {
            sessionListener.sessionDestroyed(session);
        }
        return session;
    }

    @Override
    public Session getSession(int sid) {
        return sessions.get(sid);
    }

    @Override
    public Session searchSession(ConcurrentHashMapV8.Fun<Session, Session> fun) {
        return sessions.searchValuesInParallel(fun);
    }

    @Override
    public Collection<Session> findSessions() {
        return sessions.values();
    }
}
