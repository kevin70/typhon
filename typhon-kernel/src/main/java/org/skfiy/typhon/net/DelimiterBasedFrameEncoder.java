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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@ChannelHandler.Sharable
public class DelimiterBasedFrameEncoder extends MessageToByteEncoder<ByteBuf> {

    private final byte[] delimiters;

    public DelimiterBasedFrameEncoder(byte[] delimiters) {
        this.delimiters = delimiters;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        out.writeBytes(msg, msg.readerIndex(), msg.readableBytes());
        out.writeBytes(delimiters);
    }

}
