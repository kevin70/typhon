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
package org.skfiy.typhon.net;

import java.net.InetSocketAddress;
import javax.management.MBeanServer;
import org.apache.commons.modeler.Registry;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.skfiy.typhon.AbstractMBeanLifecycle;
import org.skfiy.typhon.Connector;
import org.skfiy.typhon.LifecycleException;
import org.skfiy.typhon.LifecycleState;
import org.skfiy.typhon.Service;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.TyphonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty3 连接器实现.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class NettyConnector extends AbstractMBeanLifecycle
        implements Connector {

    private static final Logger CLOG = LoggerFactory.getLogger(Globals.CONSOLE_LOG_NAME);

    private Service service;
    private String host;
    private int port;
    private boolean logEnabled;
    private long connectionTimeout;
    private ServerBootstrap nettyServer;
    
    @Override
    public Service getService() {
        return service;
    }
    
    @Override
    public void setService(Service service) {
        this.service = service;
    }
    
    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean isLogEnabled() {
        return logEnabled;
    }

    @Override
    public void setLogEnabled(boolean enabled) {
        this.logEnabled = enabled;
    }

    @Override
    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    protected void initInternal() throws LifecycleException {
        nettyServer = new ServerBootstrap(new NioServerSocketChannelFactory());
        super.initInternal();
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        fireLifecycleListener(START_EVENT);
        
        final NettyEndpointHandler handler = new NettyEndpointHandler();
        nettyServer.setPipelineFactory(new ChannelPipelineFactory() {
            
            private final Timer timer = new HashedWheelTimer();
            private final ChannelHandler idleStateHandler = new IdleStateHandler(timer, 60, 30, 0);
            
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("IdleState", idleStateHandler);
                pipeline.addLast("FrameDecoder",
                        new LengthFieldBasedFrameDecoder(
                        Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("FrameEncoder", new LengthFieldPrepender(4, false));
                if (isLogEnabled()) {
                    pipeline.addLast("Logging", new LoggingHandler());
                }
                pipeline.addLast("Endpoint-Inbound", handler);
                return pipeline;
            }
        });
        
        nettyServer.bind(new InetSocketAddress(host, port));
        CLOG.debug("NettyConnector started on port {}", port);
        
        MBeanServer mbeanServer = Registry.getRegistry(null, null).getMBeanServer();
        Object obj = null;
        try {
            obj = mbeanServer.invoke(Container.OBJECT_NAME,
                    "getInstance",
                    new Object[]{ProtocolHandler.class},
                    new String[]{Class.class.getName()});
        } catch (Exception ex) {
            CLOG.error("获取ProtocolHandler错误", ex);
            throw new TyphonException(ex);
        }
        handler.setProtocolHandler((ProtocolHandler) obj);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
        fireLifecycleListener(STOP_EVENT);
        
        nettyServer.shutdown();
    }

    @Override
    protected void destroyInternal() throws LifecycleException {
        super.destroyInternal();
    }
    

    @Override
    protected String getMBeanDomain() {
        return "Connector";
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return "name=NettyConnector";
    }
}
