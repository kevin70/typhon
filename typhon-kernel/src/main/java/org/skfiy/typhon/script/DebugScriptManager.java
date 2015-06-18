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

import com.sun.tools.attach.VirtualMachine;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.util.MBeanUtils;
import org.skfiy.util.Assert;
import org.skfiy.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调试版脚本管理器, 该对象支持重新定义{@code Script }字节码.
 * {@link #reload() }方法在执行前去尝试加载{@code Agent }实现(通过{@link Constants#AGENT_JAR_PATH }
 * 配置JAR路径), 获得{@code Instrumentation }实例之后会检查{@link Constants#SCRIPTS_DIR }对应的脚本 源码,
 * 然后重新编译被修改的脚本重定义到当前应用中.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public final class DebugScriptManager extends AbstractComponent implements ScriptManager {

    private static final Logger LOG = LoggerFactory.getLogger(DebugScriptManager.class);

    @Inject
    private Container container;
    private Instrumentation instrumentation;

    private File sourceDir;
    private File targetDir;

    private final Map<String, ScriptWapper> scripts = new ConcurrentHashMap<>();
    private ScriptClassLoader scriptClassLoader = new ScriptClassLoader();

    @Override
    public void doInit() {
        sourceDir = new File(Typhons.getProperty(Constants.SCRIPTS_DIR));
        targetDir = new File(Typhons.getProperty(Constants.SCRIPTS_OUT_DIR,
                Typhons.getProperty(Constants.SCRIPTS_DIR)));
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        // compiler
        List<File> javaSources = new ArrayList<>();
        findJavaSources(sourceDir, javaSources);
        compile(javaSources.toArray(new File[]{}));

        // init
        initClassLoader(targetDir);
        initScripts(targetDir);

        MBeanUtils.registerComponent(this, OBJECT_NAME, null);
        LOG.debug("DebugScriptManager inited successful...");
    }

    @Override
    public void doReload() {
        if (instrumentation == null) {
            loadAgent();
        }

        // compiler
        List<File> javaSources = new ArrayList<>();
        findJavaSources(sourceDir, javaSources);
        compile(javaSources.toArray(new File[]{}));

        redefineScripts(targetDir);
    }

    @Override
    public void doDestroy() {
        scripts.clear();
        container = null;
        targetDir = null;
        scriptClassLoader = null;
        MBeanUtils.REGISTRY.unregisterComponent(OBJECT_NAME);
    }

    @Override
    public <T extends Script> T getScript(String name) {
        Assert.notNull(name);

        ScriptWapper wapper = scripts.get(name);
        if (wapper == null) {
            throw new NotFoundScriptException(name);
        }
        return (T) wapper.getScriptObject();
    }

    /**
     * 设置{@link Instrumentation }实例.
     *
     * @param instrumentation {@link Instrumentation }实例
     */
    public void setInstrumentation(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
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
                String path = name.replaceAll("\\.", "/");

                File source = new File(sourceDir, path + ".java");
                File target = new File(targetDir, path + ".class");

                Script script = newScript(name);
                if (script == null) {
                    continue;
                }
                ScriptWapper scriptWapper = new ScriptWapper(source, target, script);
                scripts.put(name, scriptWapper);
            }
        }
    }

    private void redefineScripts(File dir) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                redefineScripts(f);
            } else if (f.getName().endsWith(".class")) {
                String name = getScriptCanonicalName(f);
                String path = name.replaceAll("\\.", "/");
                File target = new File(targetDir, path + ".class");

                try {
                    InputStream in = new FileInputStream(target);
                    byte[] buf = StreamUtils.copyToByteArray(in);
                    ClassDefinition cdef = new ClassDefinition(scriptClassLoader.loadClass(name), buf);
                    instrumentation.redefineClasses(cdef);
                } catch (Exception ex) {
                    LOG.debug("重定义[{}]错误", name);
                    throw new ScriptException(ex);
                }
            }
        }
    }

    private Script newScript(String name) {
        try {

            Class clazz = scriptClassLoader.loadClass(name);
            if (!Script.class.isAssignableFrom(clazz)) {
                return null;
            }

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

    private void compile(File... files) {
        if (files.length <= 0) {
            return;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                null, Locale.CHINA, StandardCharsets.UTF_8);
        try {

            StringBuilder classpath = new StringBuilder();
            if (this.getClass().getClassLoader() instanceof URLClassLoader) {
                URLClassLoader urlClassLoader = (URLClassLoader) this.getClass().getClassLoader();
                for (URL url : urlClassLoader.getURLs()) {
                    classpath.append(url.getFile());
                    classpath.append(File.pathSeparator);
                }
            }

            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files);
            Iterable<String> options = Arrays.asList("-d", targetDir.getAbsolutePath(),
                    "-encoding", "UTF-8", "-classpath", classpath.toString(), "-g");
            
            OutputStream stream = new ByteArrayOutputStream();
            Writer write = new OutputStreamWriter(stream);
            CompilationTask task = compiler.getTask(
                    write, fileManager, null, options, null, compilationUnits);
            if (task.call()) {
                LOG.debug("Scripts compilation successful...");
            }
        } catch (Exception ex) {
            throw new ComponentException(ex);
        } finally {
            try {
                fileManager.close();
            } catch (IOException ex) {
            }
        }
    }

    private void findJavaSources(File dir, List<File> javaFiles) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                findJavaSources(file, javaFiles);
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
    }

    private void loadAgent() {
        try {
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(Typhons.getProperty(Constants.AGENT_JAR_PATH), OBJECT_NAME.toString());
            vm.detach();
        } catch (Exception ex) {
            throw new RuntimeException(
                    "无法加载代理JAR文件["
                    + Typhons.getProperty(Constants.AGENT_JAR_PATH) + "]", ex);
        }
    }
}
