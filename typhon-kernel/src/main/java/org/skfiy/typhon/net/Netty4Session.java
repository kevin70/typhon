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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.skfiy.typhon.session.AbstractSession;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
class Netty4Session extends AbstractSession {

    private final ChannelHandlerContext ctx;
    private final long creationTime;
    private long lastAccessedTime;

    Netty4Session(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public int getId() {
        return ctx.hashCode();
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
    public boolean isAvailable() {
        return ctx.channel().isActive();
    }

    @Override
    public void close() {
        setAuthType(null);
    }

    @Override
    protected void write(byte[] buf, int off, int len) {
//        byte[] a_arr = Arrays.copyOfRange(buf, off, off + len / 2);
//        byte[] b_arr = Arrays.copyOfRange(buf, off + len / 2, off + len);

        ByteBuf byteBuf = ctx.alloc().buffer(len);
        byteBuf.writeBytes(buf, off, len);
        ctx.write(byteBuf);
        ctx.flush();
//        ByteBuf byteBuf = ctx.alloc().buffer(a_arr.length);
//        byteBuf.writeBytes(a_arr, off, a_arr.length);
//        ctx.write(byteBuf);
//        ctx.flush();
//        
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//        
//        ByteBuf byteBuf2 = ctx.alloc().buffer(b_arr.length);
//        byteBuf2.writeBytes(b_arr, off, b_arr.length);
//        ctx.write(byteBuf2);
//        ctx.flush();
    }

    void updateLastAccessedTime() {
        lastAccessedTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.channel());
        sb.append(" >> [creationTime: ").append(creationTime).append(", ");
        sb.append("lastAccessedTime: ").append(lastAccessedTime).append("]");
        return sb.toString();
    }

}
