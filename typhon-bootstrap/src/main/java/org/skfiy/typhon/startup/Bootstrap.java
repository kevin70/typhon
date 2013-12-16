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

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class Bootstrap {

    private static Bootstrap boot;
    private ClassLoader classLoader;
    private Class deamonClass;
    private Object deamonObject;

    public void setAwait() throws Exception {
        Method m = deamonClass.getMethod("setAwait", new Class[]{Boolean.TYPE});
        m.invoke(deamonObject, new Object[]{true});
    }
    
    public void load() throws Exception {
        Method m = deamonClass.getMethod("load", new Class[]{});
        m.invoke(deamonObject, new Object[]{});
    }
    
    public void init(String[] args) throws Exception {
        init();
        load();
    }
    
    public void init() throws Exception {
        String home = System.getProperty("typhon.home");
        if (home != null && (new File(home)).isDirectory()) {
            classLoader = new BootstrapClassLoader(
                    Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(classLoader);
        } else {
            System.setProperty("typhon.home", System.getProperty("user.dir"));
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        
        deamonClass = classLoader.loadClass("org.skfiy.typhon.startup.Typhon");
        deamonObject = deamonClass.newInstance();
    }

    public void start() throws Exception {
        Method m = deamonClass.getMethod("start", (Class[]) null);
        m.invoke(deamonObject, (Object[]) null);
    }

    public void stop() throws Exception {
        Method m = deamonClass.getMethod("stop", (Class[]) null);
        m.invoke(deamonObject, (Object[]) null);
    }
    
    public void destroy() throws Exception {
        System.exit(0);
    }

    void usage() {
        // FIXME 
    }

    public static void main(String[] args) {
        if (boot == null) {
            boot = new Bootstrap();
            try {
                boot.init();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            Thread.currentThread().setContextClassLoader(boot.classLoader);
        }

        try {

            String command = "start";
            if (args.length > 0) {
                command = args[0];
            }

            if ("-h".equals(command)) {
                boot.usage();
                return;
            }
            
            if ("startd".equals(command)) {
                boot.start();
            } else if ("stopd".equals(command)) {
                boot.stop();
            } else if ("start".equals(command)) {
                boot.setAwait();
                boot.start();
            } else if ("stop".equals(command)) {
                boot.stop();
            }

        } catch (Exception e) {
            //
            e.printStackTrace();
            System.exit(1);
        }

    }

    static class BootstrapClassLoader extends URLClassLoader {

        public BootstrapClassLoader(ClassLoader parent) {
            super(new URL[]{}, parent);
            
            File home = new File(System.getProperty("typhon.home"));
            File confDir = new File(home, "conf");
            if (!confDir.isDirectory()
                    || !confDir.canRead()) {
                return;
            }
            
            try {
                addURL(confDir.toURI().toURL());
                
                for (File f : confDir.listFiles()) {
                    addURL(f.toURI().toURL());
                }
            } catch (MalformedURLException ex) {
                // FIXME
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
            
            File binDir = new File(home, "lib");
            if (!binDir.isDirectory()
                    || !binDir.canRead()) {
                return;
            }
            
            File[] files = binDir.listFiles();

            try {
                for (File file : files) {
                    addURL(file.toURI().toURL());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
