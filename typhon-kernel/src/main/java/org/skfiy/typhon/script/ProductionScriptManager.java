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
package org.skfiy.typhon.script;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.Component;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.Typhons;
import org.skfiy.util.Assert;
import org.skfiy.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生产版脚本管理器.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class ProductionScriptManager extends AbstractComponent implements ScriptManager {

    private static final Logger LOG = LoggerFactory.getLogger(ProductionScriptManager.class);
    
    @Inject
    private Container container;
    
    private File targetDir;
    
    private final Map<String, Script> scripts = new HashMap<>();
    private ScriptClassLoader scriptClassLoader = new ScriptClassLoader();
    private Component.Status status;

    @Override
    public void init() {
        targetDir = new File(Typhons.getProperty(Constants.SCRIPTS_OUT_DIR,
                Typhons.getProperty(Constants.SCRIPTS_DIR)));
        initClassLoader(targetDir);
        initScripts(targetDir);
        
        status = Component.Status.INITIALIZED;
        LOG.debug("ProductionScriptManager inited successful...");
    }

    @Override
    public void destroy() {
        status = Component.Status.DESTROYED;
        scripts.clear();
        container = null;
        targetDir = null;
        scriptClassLoader = null;
        LOG.debug("ProductionScriptManager destroyed successful...");
    }

    @Override
    public <T extends Script> T getScript(String name) {
        Assert.notNull(name);
        Assert.state(status == Component.Status.INITIALIZED,
                "[Assertion failed] - this state invariant must be initialized");
        
        Script script = scripts.get(name);
        if (script == null) {
            throw new NotFoundScriptException(name);
        }
        return (T) script;
    }

    private void initClassLoader(File dir) {
        scriptClassLoader.addFile(dir);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                initClassLoader(f);
            }
        }
    }

    private void initScripts(File dir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                initScripts(f);
            } else if (f.getName().endsWith(".class")) {
                String name = getScriptCanonicalName(f);
                scripts.put(name, newScript(name));
            }
        }
    }

    private Script newScript(String name) {
        try {
            Class clazz = ClassUtils.forName(name, scriptClassLoader);
            Script script = (Script) clazz.newInstance(); 
            container.injectMembers(script);
            return script;
        } catch (Exception ex) {
            LOG.error("创建[{}]脚本实例错误", name, ex);
            throw new ScriptException("创建[" + name + "]脚本实例错误", ex);
        }
    }

    private String getScriptCanonicalName(File f) {
        String name = f.getName();
        StringBuilder sb = new StringBuilder(name.substring(0, name.lastIndexOf(".")));
        for (;;) {
            File parentFile = f.getParentFile();
            if (targetDir.equals(parentFile)) {
                break;
            }
            sb.insert(0, ".").insert(0, parentFile.getName());
            f = parentFile;
        }
        return sb.toString();
    }
}
