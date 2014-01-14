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
package org.skfiy.typhon;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Test {

    @Inject
    private Injector injector;
    
    public static void main(String[] args) {
        Injector inj = Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bind(Test.class);
                
                getProvider(Test.class).get().execute();
            }
        });
        
//        inj.getInstance(Test.class).execute();
    }
    
    public void execute() {
        Set<Map.Entry<Key<?>, Binding<?>>> entries = injector.getAllBindings().entrySet();
        for (Map.Entry<Key<?>, Binding<?>> entry : entries) {
            System.out.println(entry);
        }
    }
    
}


