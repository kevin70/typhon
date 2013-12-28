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
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.skfiy.typhon.net.ProtocolHandler;
import org.skfiy.typhon.net.NettyEndpointHandler;
import org.skfiy.typhon.net.TestProtocolHandler;
import org.skfiy.typhon.packet.Auth;
import org.skfiy.typhon.packet.Namespaces;
import org.skfiy.typhon.packet.Packet;
import org.testng.Assert;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class TestProtocolBase extends TestBase {

    private final static SerializeConfig SERIALIZE_CONFIG;
    private final static DecoderEmbedder DECODER;
    private final static EncoderEmbedder ENCODER;

    static {
        SERIALIZE_CONFIG = new SerializeConfig();
        SERIALIZE_CONFIG.setAsmEnable(false);
        
        NettyEndpointHandler handler = new NettyEndpointHandler();
        
        ProtocolHandler protocolHandler = new TestProtocolHandler();
        CONTAINER.injectMembers(protocolHandler);
        //
        ((Component)protocolHandler).init();
        handler.setProtocolHandler(protocolHandler);

        DECODER = new DecoderEmbedder(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                new LoggingHandler(),
                handler);

        ENCODER = new EncoderEmbedder(
                new LengthFieldPrepender(4, false));
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
     * @param msg 
     */
    public final void offer(String ns, String msg) {
        byte[] b0 = ns.getBytes(StandardCharsets.UTF_8);
        byte[] b1 = msg.getBytes(StandardCharsets.UTF_8);

        byte[] bytes = new byte[b0.length + b1.length + 1];
        System.arraycopy(b0, 0, bytes, 0, b0.length);
        // 命名空间与消息之间的分隔符
        bytes[b0.length] = 0;
        System.arraycopy(b1, 0, bytes, b0.length + 1, b1.length);
        
        // send
        offer(bytes);
    }

    /**
     * 
     * @return 
     */
    public final Response poll() {
        Object r = DECODER.poll();
        if (r == null) {
            return null;
        }
        
        ENCODER.offer(r);
        ChannelBuffer buf = (ChannelBuffer) ENCODER.poll();
        
        buf.skipBytes(4); // 字节长度
        
        // 获取命名空间
        byte[] nsBytes = new byte[buf.indexOf(4, 32, (byte) 0) - 4];
        buf.readBytes(nsBytes, 0, nsBytes.length);
        String ns = new String(nsBytes);
        
        // 路过命名空间与消息的分隔符
        buf.skipBytes(1);
        
        // 消息长度
        byte[] dataBytes = new byte[buf.readableBytes()];
        buf.readBytes(dataBytes, 0, dataBytes.length);
        return (new Response(ns, (JSONObject) JSON.parse(dataBytes)));
    }
    
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
        for (;;) {
            if (poll() == null) {
                break;
            }
        }
    }
    
    /**
     * 
     * @return 
     */
    protected String generateId() {
        return Integer.toHexString((int) (Math.random() * 1000));
    }
    
    private void offer(byte[] bytes) {
        ChannelBuffer buf = ChannelBuffers.buffer(bytes.length + 4);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        DECODER.offer(buf);
    }
    
}
