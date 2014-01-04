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
package org.skfiy.typhon.dispatcher;

import org.skfiy.typhon.TestBase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class AsmDispatcherFactoryTest extends TestBase {

    private AsmDispatcherFactory adf;
    
    @Override
    protected void setup() {
        adf = new AsmDispatcherFactory(CONTAINER);
    }

    @Test
    public void getDispatcher() {
        Assert.assertNotNull(adf.getDispatcher());
    }

    @Test
    public void test1() {
        Dispatcher dispatcher = adf.getDispatcher();
        
        TestPacket tp = new TestPacket();
        tp.setNs("ACTION-1");
        dispatcher.dispatch(tp.getNs(), tp);
    }
    
    @Test
    public void test2() {
        Dispatcher dispatcher = adf.getDispatcher();
        
        TestPacket tp = new TestPacket();
        tp.setNs("ACTION-2");
        dispatcher.dispatch(tp.getNs(), tp);
    }
    
    @Test
    public void test3() {
        Dispatcher dispatcher = adf.getDispatcher();
        TestPacket tp = new TestPacket();
        tp.setNs("ACTION-XXX");
        
        try {
            // 不存在的命名空间会抛出Exception
            dispatcher.dispatch(tp.getNs(), tp);
            Assert.fail("found namespace [" + tp.getNs() + "]");
        } catch (NoNamespaceDefException e) {
        }
    }
    
}
