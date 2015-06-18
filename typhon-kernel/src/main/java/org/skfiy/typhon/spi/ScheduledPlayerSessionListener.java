/*
 * Copyright 2014 The Skfiy Open Association.
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
package org.skfiy.typhon.spi;

import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.RoleData;
import org.skfiy.typhon.repository.RoleRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionConstants;
import org.skfiy.typhon.session.SessionListener;
import org.skfiy.typhon.spi.role.RoleDatable;
import org.skfiy.util.CustomizableThreadCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class ScheduledPlayerSessionListener implements SessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledPlayerSessionListener.class);

    private final ScheduledExecutorService PLAYER_SL_SEC = Executors.newScheduledThreadPool(1,
            new ThreadFactory() {
                CustomizableThreadCreator threadCreator = new CustomizableThreadCreator("player-sl-");

                {
                    threadCreator.setDaemon(true);
                }

                @Override
                public Thread newThread(Runnable r) {
                    return threadCreator.createThread(r);
                }
            });

    @Inject
    private RoleRepository roleReposy;
    @Resource
    private Set<RoleDatable> roleDatables;

    @Override
    public void sessionCreated(Session session) {
        cancelFuture(session);

        SimpleTimerTask task = new SimpleTimerTask(session);
        int period = Typhons.getInteger("typhon.spi.scheduledPlayerSL.millis", 1000 * 60 * 10);

        ScheduledFuture<?> future = PLAYER_SL_SEC.scheduleAtFixedRate(task, period, period, TimeUnit.MILLISECONDS);
        session.setAttribute(SessionConstants.ATTR_PLAYER_SL_KEY, future);

        if (LOG.isDebugEnabled()) {
            LOG.debug("sessionId:{}, sessionCreated: [{}]", session.getId(), session);
        }
    }

    @Override
    public void sessionDestroyed(Session session) {
        cancelFuture(session);
        updateData(session);

        if (LOG.isDebugEnabled()) {
            LOG.debug("sessionId:{}, sessionDestroyed: [{}]", session.getId(), session);
        }
    }

    private void cancelFuture(Session session) {
        ScheduledFuture<?> future = (ScheduledFuture<?>) session.getAttribute(SessionConstants.ATTR_PLAYER_SL_KEY);
        if (future != null) {
            future.cancel(true);
        }
    }

    private void updateData(Session session) {
        Player player = (Player) session.getAttribute(SessionConstants.ATTR_PLAYER);
        if (player == null) {
            LOG.error("player is null. [{}]", session);
            return;
        }

        try {
            String cons = player.getRole().getRid() + "__updateData__";
            synchronized (cons) {
                player.getNormal().setLastLogoutTime(System.currentTimeMillis());

                // 更新钻石
                roleReposy.update(player.getRole());
                LOG.debug("sessionId:{}, username:{}\n{}", session.getId(), player.getRole().getName(), player.getRole());

                RoleData roleData = new RoleData();
                for (RoleDatable roleDatable : roleDatables) {
                    roleDatable.serialize(player, roleData);
                }

                // FIXME 这里只更新了RoleData数据
                // 后期还需要更新Role的数据信息
                roleData.setRid(player.getRole().getRid());
                roleReposy.update(roleData);

                LOG.debug("sessionId:{}, username:{}\n{}", session.getId(), player.getRole().getName(), roleData);
            }
        } catch (Exception e) {
            LOG.error("sessionId:{}, saveDataError: {}", session.getId(), player.getRole().getName(), e);
        }
    }

    private class SimpleTimerTask implements Runnable {

        final Session session;

        SimpleTimerTask(Session session) {
            this.session = session;
        }

        @Override
        public void run() {
            updateData(session);
        }
    }

}
