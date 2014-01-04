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

import com.alibaba.fastjson.JSONObject;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import org.skfiy.typhon.packet.Packet;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Session {

    /**
     * 
     * @return 
     */
    int getId();
    
    /**
     * 
     * @param sessionId 
     */
    void setId(int sessionId);

    /**
     * 
     * @param key
     * @param value 
     */
    void setAttribute(String key, Object value);
    
    /**
     * 
     * @param key
     * @return 
     */
    Object getAttribute(String key);
    
    /**
     * 
     * @param key
     * @return 
     */
    Object removeAttribute(String key);
    
    /**
     * 
     * @return 
     */
    Map<String, Object> getAttributes();
    
    /**
     * 
     * @return 
     */
    Collection<Object> getAttributeValues();
    
    /**
     * 
     * @return 
     */
    Enumeration<String> getAttributeKeys();
    
    /**
     * 
     * @return 
     */
    String getAuthType();
    
    /**
     * 
     * @param authType 
     */
    void setAuthType(String authType);
    
    /**
     * 
     * @return 
     */
    long getCreationTime();

    /**
     * 
     * @return 
     */
    long getLastAccessedTime();

    /**
     * 
     * @param packet 
     */
    void write(Packet packet);
    
    /**
     * 
     * @param ns
     * @param json 
     */
    void write(String ns, JSONObject json);
    
    /**
     * 
     * @param ns
     * @param body 
     */
    void write(String ns, String body);
    
    /**
     * 
     */
    void close();
}
