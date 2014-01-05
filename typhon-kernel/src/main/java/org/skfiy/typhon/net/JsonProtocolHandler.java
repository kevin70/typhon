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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.Component;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.dispatcher.Dispatcher;
import org.skfiy.typhon.dispatcher.DispatcherFactory;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.util.CustomizableThreadCreator;

/**
 * {@code JSON } 消息协议处理器.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class JsonProtocolHandler implements Component, ProtocolHandler {

    @Inject
    protected DispatcherFactory dispatcherFactory;
    protected Dispatcher dispatcher;
    protected ExecutorService executorService;
    protected CustomizableThreadCreator threadCreator;
    protected ThreadLocal<Session> _local_session;

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
        
        dispatcher = dispatcherFactory.getDispatcher();
        
        threadCreator = new CustomizableThreadCreator("json-exec-");
        threadCreator.setThreadGroupName("Protocol-ThreadGroup");
        executorService = new ThreadPoolExecutor(Integer.getInteger("protocol.corePoolSize", 2),
                Integer.getInteger("protocol.maxPoolSize", 200),
                Integer.getInteger("protocol.keepAliveTime", 3000),
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(Integer.getInteger("protocol.workQueueSize", 50)),
                new CustomizableThreadFactory(),
                new NewThreadPolicy());
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void destroy() {
        executorService.shutdown();
    }

    @Override
    public void handle(final Session session, final byte[] nsbs, final byte[] datas) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (session) {
                    // 保存Session至上下文
                    _local_session.set(session);

                    String ns = new String(nsbs, StandardCharsets.UTF_8);
                    Packet packet = JSON.parseObject(datas, dispatcherFactory.getPacketClass(ns));
                    dispatcher.dispatch(ns, packet);
                }
            }
        });
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
