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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.session.AbstractSession;

/**
 * Netty 3 端点处理器实现.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class NettyEndpointHandler extends IdleStateAwareChannelHandler {

    private ProtocolHandler protocolHandler;

    public ProtocolHandler getProtocolHandler() {
        return protocolHandler;
    }

    public void setProtocolHandler(ProtocolHandler protocolHandler) {
        this.protocolHandler = protocolHandler;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        NettySession session = new NettySession(ctx.getChannel());
        ctx.setAttachment(session);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        // 登出
        NettySession session = (NettySession) ctx.getAttachment();
        if (session.isAvailable()) {
            protocolHandler.handle(session, Namespaces.LOGOUT.getBytes(), "{}".getBytes());
        }
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
        if (e.getState() == IdleState.READER_IDLE) {
            // e.getChannel().close();
            // 超时会话
            NettySession session = (NettySession) ctx.getAttachment();
//            protocolHandler.handle(session, nsBytes, dataBytes);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        NettySession session = (NettySession) ctx.getAttachment();

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
}