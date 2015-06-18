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
package org.skfiy.typhon.net;

import com.alibaba.fastjson.JSON;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.Component;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.dispatcher.DispatcherFactory;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionConstants;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.util.CustomizableThreadCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code JSON } 消息协议处理器.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class JsonProtocolHandler implements Component, ProtocolHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JsonProtocolHandler.class);

    @Inject
    protected DispatcherFactory dispatcherFactory;
    @Resource
    protected Set<SessionErrorHandler> sessionErrorHandlers;

    protected ExecutorService executorService;
    protected CustomizableThreadCreator threadCreator;
    protected ThreadLocal<Session> _local_session;

    private boolean shutdown = false;

    @PostConstruct
    @Override
    public void init() {
        try {
            Field field = SessionContext.class.getDeclaredField("LOCAL_SESSION");
            field.setAccessible(true);
            _local_session = (ThreadLocal<Session>) field.get(SessionContext.class);
        } catch (NoSuchFieldException ex) {
            throw new ComponentException("SessionContext中不存在[LOCAL_SESSION]属性", ex);
        } catch (SecurityException ex) {
            throw new ComponentException(
                    getClass() + " 缺少访问SessionContext [LOCAL_SESSION] 属性的权限", ex);
        } catch (Exception ex) {
            throw new ComponentException(ex);
        }

        threadCreator = new CustomizableThreadCreator("json-exec-");
        threadCreator.setThreadGroupName("Protocol-ThreadGroup");
        threadCreator.setDaemon(true);
        executorService = new ThreadPoolExecutor(Integer.getInteger("protocol.corePoolSize", 4),
                Integer.getInteger("protocol.maxPoolSize", 50),
                Integer.getInteger("protocol.keepAliveTime", 3000),
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(Integer.getInteger("protocol.workQueueSize", 30)),
                new CustomizableThreadFactory(),
                new NewThreadPolicy());
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @PreDestroy
    @Override
    public void destroy() {
        shutdown = true;
        executorService.shutdown();
    }

    @Override
    public void handle(final Session session, final byte[] nsbs, final byte[] datas) {
        if (shutdown) {
            LOG.warn("shutdown......");
            return;
        }

        final String ns = new String(nsbs, StandardCharsets.UTF_8);
        Runnable impl = new Runnable() {

            @Override
            public void run() {
                synchronized (session) {
                    _local_session.set(session);
                    try {

                        Packet packet = JSON.parseObject(datas,
                                dispatcherFactory.getPacketClass(ns));
                        packet.setNs(ns);

                        session.setAttribute(SessionConstants.ATTR_CONTEXT_PACKET, packet);
                        dispatcherFactory.getDispatcher().dispatch(ns, packet);
                        session.removeAttribute(SessionConstants.ATTR_CONTEXT_PACKET);
                    } catch (Throwable t) {
                        LOG.error("{}:{} --> {}", new String(nsbs, StandardCharsets.UTF_8),
                                new String(datas, StandardCharsets.UTF_8), t);

                        for (SessionErrorHandler seh : sessionErrorHandlers) {
                            if (seh.getErrorType().isAssignableFrom(t.getClass())) {
                                seh.handleError(session, t);
                                break;
                            }
                        }
                    }
                }
            }
        };

        if (Namespaces.LOGOUT.equals(ns)) {
            impl.run();
        } else {
            executorService.execute(impl);
        }
    }

    private class CustomizableThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return threadCreator.createThread(r);
        }
    }

    private class NewThreadPolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Thread t = threadCreator.createThread(r);
            t.setDaemon(true);
            t.start();
        }
    }
}
