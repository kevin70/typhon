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
package org.skfiy.typhon;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Guice(moduleFactory = TestModuleFactory.class)
public abstract class TestBase extends TestSupport {

    @BeforeClass
    public final void beforeInvoke(ITestContext context) {
        setup();
    }

    @AfterClass
    public final void afterInvoke(ITestContext context) {
        teardown();
    }

    protected void setup() {
        //
    }

    protected void teardown() {
        //
    }
}
