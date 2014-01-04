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
package org.skfiy.typhon.startup;

import org.skfiy.typhon.LifecycleException;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TyphonTest {

    private Typhon daemon = new Typhon();

    public TyphonTest() {
//        System.setProperty("typhon.rcf.class",
//                    "org.skfiy.typhon.startup.DebugResourceContextFactory");
    }
    
    @Test(groups = "daemon")
    public void start() throws LifecycleException {
        daemon.start();
    }

    @Test(groups = "daemon", dependsOnMethods = {"start"})
    public void stop() throws LifecycleException {
        daemon.stop();
    }
    
    // 测试使用Socket方式停止服务
//    @Test(dependsOnGroups = "daemon")
//    public void start2() {
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                Typhon typhon = new Typhon();
//                typhon.setAwait(true);
//                typhon.start();
//            }
//        };
//        thread.start();
//    }
//
//    @Test(dependsOnMethods = "start2")
//    public void stop2() throws LifecycleException {
//        Typhon typhon = new Typhon();
//        typhon.stop();
//    }
}
