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
package org.skfiy.typhon;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.nio.charset.StandardCharsets;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.skfiy.typhon.net.ProtocolHandler;
import org.skfiy.typhon.net.NettyEndpointHandler;
import org.skfiy.typhon.net.TestProtocolHandler;
import org.skfiy.typhon.packet.Auth;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.typhon.session.AbstractSession;
import org.testng.Assert;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TestProtocolBase extends TestBase {

    private final static SerializeConfig SERIALIZE_CONFIG;

    static {
        SERIALIZE_CONFIG = new SerializeConfig();
        SERIALIZE_CONFIG.setAsmEnable(false);
    }

    /**
     *
     * @param packet
     */
    public final void offer(Packet packet) {
        String msg = JSON.toJSONString(packet, SERIALIZE_CONFIG);
        offer(packet.getNs(), msg);
    }

    /**
     *
     * @param ns
     * @param json
     */
    public final void offer(String ns, JSON json) {
        offer(ns, json.toJSONString());
    }

    /**
     *
     * @param ns
     * @param body
     */
    public final void offer(String ns, String body) {
        byte[] b0 = ns.getBytes(StandardCharsets.UTF_8);
        byte[] b1 = body.getBytes(StandardCharsets.UTF_8);

        int l1 = b0.length + 1;
        int l2 = l1 + b1.length;

        byte[] buf = new byte[l2 + 1];
        System.arraycopy(b0, 0, buf, 0, b0.length);

        // 命名空间与消息主体分隔符
        buf[b0.length] = AbstractSession.NS_SEPARTOR;

        System.arraycopy(b1, 0, buf, l1, b1.length);
        buf[l2] = AbstractSession.MSG_SEPARTOR;
        // send
        offer(buf);
    }

    /**
     *
     * @return
     */
    public final Response poll() {
        ChannelBuffer buf = (ChannelBuffer) getDecoderEmbedder().poll();
        if (buf == null) {
            return null;
        }

        int eol = findEndOfLine(buf);
        ChannelBuffer frame = buf.factory().getBuffer(eol);
        frame.writeBytes(buf, buf.readerIndex(), eol);

        // 获取命名空间
        byte[] nsBytes = new byte[frame.indexOf(0, 32, (byte) ':')];
        frame.readBytes(nsBytes, 0, nsBytes.length);
        String ns = new String(nsBytes);
        // 跳过命名空间与消息的分隔符
        frame.skipBytes(1);

        // 消息长度
        byte[] dataBytes = new byte[frame.readableBytes()];
        frame.readBytes(dataBytes, 0, dataBytes.length);
        return (new Response(ns, (JSONObject) JSON.parse(dataBytes)));
    }

    /**
     * Returns the index in the buffer of the end of line found. Returns -1 if no end of line was found in the buffer.
     */
    private int findEndOfLine(final ChannelBuffer buffer) {
        final int n = buffer.writerIndex();
        for (int i = buffer.readerIndex(); i < n; i++) {
            final byte b = buffer.getByte(i);
            if (b == '\n') {
                return i;
            } else if (b == '\r' && i < n - 1 && buffer.getByte(i + 1) == '\n') {
                return i;  // \r\n
            }
        }
        return -1;  // Not found.
    }

    /**
     *
     */
    protected void auth() {
        String pid = generateId();
        Auth auth = new Auth();
        auth.setNs(Namespaces.AUTH);
        auth.setId(pid);
        auth.setUsername(TestConstants.USERNAME);
        auth.setPassword(TestConstants.PASSWORD);

        // 发送认证消息
        offer(auth);

        // 认证成功响应
        Response resp = poll();
        boolean a = Namespaces.USER_INFO.equals(resp.getNs())
                && pid.equals(resp.getData().getString("id"));
        Assert.assertTrue(a);
    }

    /**
     *
     */
    protected void removalOverMessage() {
        offer("logout", "{}");
        getDecoderEmbedder().pollAll();
        testContext.removeAttribute("test.decoderEmbedder");
    }

    /**
     *
     * @return
     */
    protected String generateId() {
        return Integer.toHexString((int) (Math.random() * 1000));
    }

    private void offer(byte[] bytes) {
        ChannelBuffer buf = ChannelBuffers.buffer(bytes.length);
        buf.writeBytes(bytes);
        getDecoderEmbedder().offer(buf);
    }

    protected DecoderEmbedder getDecoderEmbedder() {
        DecoderEmbedder embedder = (DecoderEmbedder) testContext.getAttribute("test.decoderEmbedder");
        if (embedder != null) {
            return embedder;
        }

        NettyEndpointHandler handler = new NettyEndpointHandler();
        ProtocolHandler protocolHandler = new TestProtocolHandler();
        containerWapper.injectMembers(protocolHandler);
        ((Component) protocolHandler).init();

        handler.setProtocolHandler(protocolHandler);

        embedder = new DecoderEmbedder(
                new DelimiterBasedFrameDecoder(65535, Delimiters.lineDelimiter()),
                new LoggingHandler(),
                handler);
        testContext.setAttribute("test.decoderEmbedder", embedder);
        return embedder;
    }
    
}
