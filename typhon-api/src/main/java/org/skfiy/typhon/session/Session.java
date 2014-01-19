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
 * 会话接口定义.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public interface Session {

    /**
     * 获取SessionId.
     *
     * @return SessionId
     */
    int getId();

    /**
     * 设置Session属性. 如果已经存在对应key的属性则将会替换原有的值.
     *
     * @param key 键
     * @param value 值
     */
    void setAttribute(String key, Object value);

    /**
     * 获取Session属性. 如果指定的key不存在则返回{@code null }.
     *
     * @param key 键
     * @return 一个Object的对象
     */
    Object getAttribute(String key);

    /**
     * 移除属性并返回key对应的属性值. 如果指定的key不返回则返回{@code null }.
     *
     * @param key 键
     * @return 一个可能为{@code null }的对象
     */
    Object removeAttribute(String key);

    /**
     * 获取Session的属性映射. 返回的{@code Map }是一个只读对象.
     *
     * @return 返回一个只读的{@code Map }
     */
    Map<String, Object> getAttributes();

    /**
     * 获取Session属性值集合.
     *
     * @return 属性值集合
     */
    Collection<Object> getAttributeValues();

    /**
     * 获取Session属性键集合.
     *
     * @return 属性键集合
     */
    Enumeration<String> getAttributeKeys();

    /**
     * 获取Session认证类型.
     *
     * @return 一个字符串
     */
    String getAuthType();

    /**
     * 设置Session认证类型.
     *
     * @param authType 一个字符串
     */
    void setAuthType(String authType);

    /**
     * 获取Session的创建时间(ms).
     *
     * @return 创建时间(ms)
     */
    long getCreationTime();

    /**
     * 获取Session最后访问时间(ms).
     *
     * @return 最后访问时间(ms)
     */
    long getLastAccessedTime();

    /**
     * 向客户端写入一条消息.
     *
     * @param packet 写入的包
     */
    void write(Packet packet);

    /**
     * 向客户端写入一条消息.
     *
     * @param ns 消息的命名空间
     * @param json 消息的主体
     */
    void write(String ns, JSONObject json);

    /**
     * 向客户端写入一条消息.
     *
     * @param ns 消息的命名空间
     * @param body 消息的主体
     */
    void write(String ns, String body);

    /**
     * 关闭Session.
     */
    void close();
}
