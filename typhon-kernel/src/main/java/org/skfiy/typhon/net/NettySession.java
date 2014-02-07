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

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.session.AbstractSession;

/**
 * Netty 3 Session实现.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
class NettySession extends AbstractSession {
    
    private final Channel channel;
    private final long creationTime;
    private long lastAccessedTime;

    /**
     * Netty 3 Channel 构造函数.
     *
     * @param channel 通道对象
     */
    public NettySession(Channel channel) {
        this.channel = channel;
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public int getId() {
        return channel.getId();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        channel.write(ChannelBuffers.wrappedBuffer(buf, off, len));
    }

    @Override
    public void close() {
        ChannelFuture future = channel.close();
        if (future.isSuccess()) {
            // FIXME
        }
    }
    
    /**
     * 更新最后访问时间.
     */
    void updateLastAccessTime() {
        lastAccessedTime = System.currentTimeMillis();
    }
}
