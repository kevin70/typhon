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
package org.skfiy.typhon.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import javax.management.MBeanServer;
import org.skfiy.typhon.AbstractMBeanLifecycle;
import org.skfiy.typhon.Connector;
import org.skfiy.typhon.Container;
import org.skfiy.typhon.Globals;
import static org.skfiy.typhon.Lifecycle.START_EVENT;
import org.skfiy.typhon.LifecycleException;
import org.skfiy.typhon.LifecycleState;
import org.skfiy.typhon.Service;
import org.skfiy.typhon.TyphonException;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.util.MBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin
 */
public class Netty4Connector extends AbstractMBeanLifecycle
        implements Connector {

    private static final Logger CLOG = LoggerFactory.getLogger(Globals.CONSOLE_LOG_NAME);

    private Service service;
    private String host;
    private int port;
    private boolean logEnabled;
    private long connectionTimeout;

    private Channel channel;
    private ServerBootstrap serverBootstrap;

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
    protected void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
        fireLifecycleListener(START_EVENT);

        System.setProperty("io.netty.noJdkZlibDecoder", "false");
        
        final byte[] delimiters = new byte[]{'\n'};
        
        final String compressionMode = Typhons.getProperty("typhon.spi.net.compressionMode");
        final Netty4EndpointHandler handler = new Netty4EndpointHandler();
        final Netty4ConnectionLimitHandler limitHandler = new Netty4ConnectionLimitHandler();
        
        // 协议解析处理器
        final LengthFieldPrepender lengthFieldPrepender;
        final DelimiterBasedFrameEncoder delimiterBasedFrameEncoder;
        if ("zlib".equals(compressionMode)) {
            lengthFieldPrepender = new LengthFieldPrepender(4);
            delimiterBasedFrameEncoder = null;
        } else {
            lengthFieldPrepender = null;
            delimiterBasedFrameEncoder = new DelimiterBasedFrameEncoder(delimiters);
        }
        
        // 日志记录器
        final LoggingHandler loggingHandler;
        if (isLogEnabled()) {
            loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        } else {
            loggingHandler = null;
        }

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(new ChannelInitializer() {

                    @Override
                    protected void initChannel(Channel c) throws Exception {
                        ChannelPipeline pipeline = c.pipeline();
                        if ("zlib".equals(compressionMode)) {
                            pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
                            pipeline.addLast("lengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast("deflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.ZLIB));
                        } else {
                            pipeline.addLast("delimiterBasedFrameDecoder", new DelimiterBasedFrameDecoder(65535, new ByteBuf[]{
                                Unpooled.wrappedBuffer(delimiters)
                            }));
                            pipeline.addLast("delimiterBasedFrameEncoder", delimiterBasedFrameEncoder);
                        }

                        if (isLogEnabled()) {
                            pipeline.addLast(loggingHandler);
                        }
                        
                        pipeline.addLast(new IdleStateHandler(60 * 10, 60 * 10, 0));
                        pipeline.addLast(limitHandler, handler);
                    }
                });

        channel = serverBootstrap.bind(host, port).channel();
        CLOG.debug("Netty4Connector started on port {}", port);

        MBeanServer mbs = MBeanUtils.REGISTRY.getMBeanServer();
        Object obj = null;
        try {
            obj = mbs.invoke(Container.OBJECT_NAME,
                    "getInstance",
                    new Object[]{ProtocolHandler.class},
                    new String[]{Class.class.getName()});
        } catch (Exception ex) {
            CLOG.error("ProtocolHandler", ex);
            throw new TyphonException(ex);
        }
        handler.setProtocolHandler((ProtocolHandler) obj);
    }

    @Override
    protected void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);

        ChannelFuture channelFuture = channel.closeFuture();
        serverBootstrap.group().shutdownGracefully();
        serverBootstrap.childGroup().shutdownGracefully();

        channelFuture.getNow();

        fireLifecycleListener(STOP_EVENT);
    }

    @Override
    protected String getMBeanDomain() {
        return "Connector";
    }

    @Override
    protected String getObjectNameKeyProperties() {
        return "name=Netty4Connector";
    }
}
