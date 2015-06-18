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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.session.AbstractSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Sharable
public class Netty4EndpointHandler extends ChannelDuplexHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Netty4EndpointHandler.class);

    private final static AttributeKey<Netty4Session> SESSION_KEY
            = AttributeKey.valueOf("Session");

    private ProtocolHandler protocolHandler;

    public ProtocolHandler getProtocolHandler() {
        return protocolHandler;
    }

    public void setProtocolHandler(ProtocolHandler protocolHandler) {
        this.protocolHandler = protocolHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Netty4Session session = new Netty4Session(ctx);
        ctx.attr(SESSION_KEY).set(session);

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        Thread.sleep(1000 * 60);
        
        sendLogout(ctx);
        super.channelInactive(ctx);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
//        sendLogout(ctx);
//        super.close(ctx, future);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        Netty4Session session = ctx.attr(SESSION_KEY).get();

        // 更新最后访问时间
        session.updateLastAccessedTime();

        // read namespace
        int len = buf.indexOf(0, 32, AbstractSession.NS_SEPARTOR);
        if (len <= 0) {
            return;
        }

        byte[] nsBytes = new byte[len];
        buf.readBytes(nsBytes, 0, nsBytes.length);
        buf.skipBytes(1);
        byte[] dataBytes = new byte[buf.readableBytes()];
        buf.readBytes(dataBytes, 0, dataBytes.length);

        protocolHandler.handle(session, nsBytes, dataBytes);
    }

    private void sendLogout(ChannelHandlerContext ctx) {
        // 登出
        Netty4Session session = ctx.attr(SESSION_KEY).get();
        if (StringUtils.isNotEmpty(session.getAuthType())) {
            protocolHandler.handle(session, Namespaces.LOGOUT.getBytes(),
                    "{}".getBytes());
        }
    }
}
