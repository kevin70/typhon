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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.LifecycleException;
import org.skfiy.typhon.Server;
import org.skfiy.typhon.TyphonException;
import org.skfiy.util.ResourceUtils;
import org.skfiy.util.SystemPropertyUtils;
import org.slf4j.LoggerFactory;

/**
 * 通过Typhon启动应用程序和停止应用程序.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public final class Typhon {

    private boolean await;
    private Server server;

    /**
     * 是否启动Socket停止服务功能.
     *
     * @param await The value is true/false
     */
    public void setAwait(final boolean await) {
        this.await = await;
    }
    
    /**
     * 设置应用主服务对象.
     *
     * @param server Server object
     */
    public void setServer(final Server server) {
        this.server = server;
    }
    
    /**
     * 加载server.xml, typhon.properties配置文件.
     */
    public void load() {
        try {
            // Create and execute our Digester
            Digester digester = createStartDigester();
            digester.push(this);
            digester.parse(ResourceUtils.getURL("classpath:server.xml").openStream());
            
        } catch (Exception e) {
            throw new TyphonException("加载server.xml失败", e);
        }
        
        // load typhon.properties
        loadProperties();
    }
    
    /**
     * 启动应用.
     */
    public void start() {
        if (server == null) {
            load();
        }
        
        try {
            server.init();
            server.start();
        } catch (LifecycleException ex) {
            throw new TyphonException(ex);
        }

        // await
        if (await) {
            Thread shutdown = new Thread(new Runnable() {
                @Override
                public void run() {
                    await();
                }
            }, "Typhon-Shutdown");
            shutdown.setDaemon(true);
            shutdown.start();
        }
    }

    /**
     * 停止应用.
     */
    public void stop() {
        if (server == null) {
            try {
                
                // Create and execute our Digester
                Digester digester = createStopDigester();

                digester.push(this);
                digester.parse(ResourceUtils.getFile("classpath:server.xml"));
                
                // load typhon.properties
                loadProperties();
            } catch (Exception e) {
                throw new TyphonException(e);
            }
            
            Socket socket = null;
            try {
                socket = new Socket(server.getHost(), server.getPort());
                OutputStream out = socket.getOutputStream();
                out.write(server.getShutdown().getBytes(StandardCharsets.UTF_8));
                out.flush();
            } catch (Exception e) {
                throw new TyphonException(e);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // nothing
                    }
                }
            }
            
            return;
        }
        
        try {
            server.stop();
            server.destroy();
        } catch (LifecycleException ex) {
            throw new TyphonException(ex);
        }
    }
    
    /**
     * Create and configure the Digester we will be using for startup.
     */
    private Digester createStartDigester() {
        long t1=System.currentTimeMillis();
        
        // Initialize the digester
        Digester digester = new Digester();
        digester.setValidating(false);
        
        digester.setClassLoader(Server.class.getClassLoader());
        
        digester.addObjectCreate(
                "Server", "org.skfiy.typhon.kernel.StandardServer");
        digester.addSetProperties("Server");
        digester.addSetNext("Server", "setServer", "org.skfiy.typhon.Server");
        digester.addObjectCreate("Server/Listener",
                null, // MUST be specified in the element
                "className");
        digester.addSetProperties("Server/Listener");
        digester.addSetNext("Server/Listener", "addListener",
                            "org.skfiy.typhon.LifecycleListener");
        
        digester.addObjectCreate(
                "Server/Service", "org.skfiy.typhon.kernel.StandardService");
        digester.addSetProperties("Server/Service");
        digester.addSetNext(
                "Server/Service", "addService", "org.skfiy.typhon.Service");
        digester.addObjectCreate("Server/Service/Listener",
                null, // MUST be specified in the element
                "className");
        digester.addSetProperties("Server/Service/Listener");
        digester.addSetNext("Server/Service/Listener", "addListener",
                            "org.skfiy.typhon.LifecycleListener");
        
        // connector
        digester.addObjectCreate("Server/Service/Connector",
                "org.skfiy.typhon.net.NettyConnector", "className");
        digester.addSetProperties("Server/Service/Connector");
        digester.addSetNext("Server/Service/Connector", "addConnector",
                "org.skfiy.typhon.Connector");
        digester.addObjectCreate("Server/Service/Connector/Listener",
                null, // MUST be specified in the element
                "className");
        digester.addSetProperties("Server/Service/Connector/Listener");
        digester.addSetNext("Server/Service/Connector/Listener", "addListener",
                            "org.skfiy.typhon.LifecycleListener");
        
        // container
        // digester.addObjectCreate("Server/Service/Container", null, "className");
        // digester.addSetProperties("Server/Service/Container");
        // digester.addSetNext("Server/Service/Container", "setContainer",
        //        "org.skfiy.typhon.Container");
        
        long t2 = System.currentTimeMillis();
        LoggerFactory.getLogger(Globals.CONSOLE_LOG_NAME)
                .debug("Digester for server.xml created " + (t2 - t1));

        return digester;
    }
    
    /**
     * Create and configure the Digester we will be using for shutdown.
     */
    private Digester createStopDigester() {

        // Initialize the digester
        Digester digester = new Digester();

        // Configure the rules we need for shutting down
        digester.addObjectCreate(
                "Server", "org.skfiy.typhon.kernel.StandardServer");
        digester.addSetProperties("Server");
        digester.addSetNext("Server", "setServer", "org.skfiy.typhon.Server");

        return digester;
    }
    
    private void loadProperties() {
        InputStream stream = null;
        try {
            Properties props = new Properties();
            stream = ResourceUtils.getURL("classpath:typhon.properties").openStream();
            props.load(stream);
            
            // loading... ext.properties
            loadExtProperties(props);
            
            // put all to system properties
            System.getProperties().putAll(props);
            
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                String val = SystemPropertyUtils.resolvePlaceholders(
                        (String) entry.getValue());
                System.setProperty((String) entry.getKey(), val);
            }
            
        } catch (FileNotFoundException ex) {
            throw new TyphonException("没有在classpath环境中找到typhon.properties文件", ex);
        } catch (IOException ex) {
            throw new TyphonException("加载typhon.propertiesr失败", ex);
        } finally {
            try {
                stream.close();
            } catch (Exception ex) {
                // nothing
            }
        }
    }
    
    private void loadExtProperties(Properties props) {
        File extFile = new File(System.getProperty("typhon.home"),
                "/conf/ext.properties");
        if (extFile.exists()) {
            InputStream stream = null;
            try {
                stream = new FileInputStream(extFile);
                props.load(stream);
            } catch (IOException ex) {
                throw new TyphonException("加载 $TYPHON_HOME/conf/ext.properties 失败", ex);
            } finally {
                try {
                    stream.close();
                } catch (Exception ex) {
                }
            }
        }
    }
    
    private void await() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(
                    new InetSocketAddress(
                    server.getHost(), server.getPort()), 1);
        } catch (IOException e) {
            throw new TyphonException(e);
        }
        
        for (;;) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                InputStream in = socket.getInputStream();
                byte[] cur = server.getShutdown().getBytes(StandardCharsets.UTF_8);
                byte[] buf = new byte[cur.length];
                int l = in.read(buf);
                // 接收到退出命令
                if (l == cur.length && Arrays.equals(cur, buf)) {
                    break;
                }
            } catch (IOException e) {
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // nothing
                    }
                }
            }
        }
        
        try {
            serverSocket.close();
        } catch (IOException e) {
            // nothing
        }
        
        // 停止服务
        stop();
    }
    
}
