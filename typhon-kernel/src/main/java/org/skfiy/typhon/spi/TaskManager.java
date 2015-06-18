/*
 * Copyright 2015 The Skfiy Open Association.
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionContextHelper;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TaskManager {

    private final ExecutorService executorService;

    public TaskManager() {
        executorService = new ThreadPoolExecutor(1, 2, 3000, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {

                    private final AtomicInteger SEQ = new AtomicInteger();

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("daemon-task-" + SEQ.getAndIncrement());
                        t.setDaemon(true);
                        return t;
                    }
                });
    }

    public void execute(final Task task) {
        final Session session = SessionContext.getSession();
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                SessionContextHelper.set(session);
                task.run();
            }
        });
    }

    public static interface Task {

        /**
         *
         */
        void run();
    }

}
