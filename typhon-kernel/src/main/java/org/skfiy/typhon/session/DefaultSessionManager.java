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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;

import org.skfiy.typhon.Component;
import org.skfiy.typhon.repository.UserRepository;
import org.skfiy.util.v8.ConcurrentHashMapV8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class DefaultSessionManager implements SessionManager, Component {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSessionManager.class);

    @Inject
    private UserRepository userRepository;

    @Resource
    private Set<SessionListener> sessionListeners;
    private final ConcurrentHashMapV8<Integer, Session> sessions;

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
        sessions.put(sid, session);

        if (LOG.isDebugEnabled()) {
            LOG.debug("addSession-> sid: {} -- {}", sid, session);
        }

        // fire
        for (SessionListener sessionListener : sessionListeners) {
            sessionListener.sessionCreated(session);
        }
    }

    @Override
    public Session removeSession(int sid) {
        Session session = sessions.remove(sid);

        if (LOG.isInfoEnabled()) {
            LOG.info("removeSession-> sid: {} -- {}", sid, session);
        }

        // fire
        if (session != null) {
            for (SessionListener sessionListener : sessionListeners) {
                sessionListener.sessionDestroyed(session);
            }
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

    //==============================================================================================
    @PostConstruct
    @Override
    public void init() {
        SessionUtils.setUserRepository(userRepository);
        BagUtils.setUserRepository(userRepository);
    }

    @Override
    public void reload() {
    }

    @PreDestroy
    @Override
    public void destroy() {
        for (int id : sessions.keySet()) {
            removeSession(id);
        }
    }
    //==============================================================================================
}
