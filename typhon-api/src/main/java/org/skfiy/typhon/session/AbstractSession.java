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
package org.skfiy.typhon.session;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.skfiy.typhon.packet.Packet;
import org.skfiy.util.Assert;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class AbstractSession implements Session {

    private int sessionId;
    private String authType;
    private Map<String, Object> attributes;

    public AbstractSession() {
        attributes = new ConcurrentHashMap<String, Object>();
    }

    @Override
    public int getId() {
        return sessionId;
    }

    @Override
    public void setId(int sessionId) {
        this.sessionId = sessionId;
    }
    
    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public Map<String, Object> getAttributeMap() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public Collection<Object> getAttributeValues() {
        return attributes.values();
    }

    @Override
    public Enumeration<String> getAttributeKeys() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public String getAuthType() {
        return authType;
    }

    @Override
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override
    public void write(Packet packet) {
        Assert.notNull(packet);
        Assert.notNull(packet.getNs());
        
        write(packet.getNs(), JSON.toJSONString(packet));
    }

    @Override
    public void write(String ns, JSONObject json) {
        Assert.notNull(ns);
        Assert.notNull(json);
        
        write(ns, json.toJSONString());
    }

    @Override
    public void write(String ns, String body) {
        byte[] b0 = ns.getBytes(StandardCharsets.UTF_8);
        byte[] b1 = body.getBytes(StandardCharsets.UTF_8);

        int nsSplitLen = b0.length + 1;
        byte[] buf = new byte[nsSplitLen + b1.length];
        System.arraycopy(b0, 0, buf, 0, b0.length);
        // 命名空间与消息主体分隔符
        buf[b0.length] = 0;
        
        System.arraycopy(b1, 0, buf, nsSplitLen, b1.length);
        
        write(buf, 0, buf.length);
    }

    protected abstract void write(byte[] buf, int off, int len);
}
