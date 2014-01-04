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

import com.google.inject.Injector;
import com.google.inject.Module;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.testng.IModuleFactory;
import org.testng.ITestContext;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TestModuleFactory implements IModuleFactory {

    private static final List INJECTOR_KEY = new ArrayList();

    @Override
    public Module createModule(ITestContext context, Class<?> testClass) {
        try {
            Method m = TestBase.CONTAINER.getClass().getMethod("getInjector", new Class[]{});
            Object r = m.invoke(TestBase.CONTAINER, new Object[]{});
            context.addInjector(INJECTOR_KEY, (Injector) r);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
}
