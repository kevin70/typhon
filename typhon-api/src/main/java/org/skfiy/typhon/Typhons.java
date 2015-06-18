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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.skfiy.util.Assert;

/**
 * {@code Typhons }可获取/设置{@link System }中的属性. 该类存在一个内部缓存. 当第一个使用某个键的
 * 属性时该类会先从{@code System }中查找, 存在则加入缓存并返回, 当第二次使用到某个键时会直接从缓存中获取
 * 参数值, 并不会从{@code System }中获取. 可通过该类的{@link #refresh() }刷新缓存, 该方法会重新从System
 * 中加载已经缓存过键的属性值.
 * 
 * @author Kevin Zou <kevinz@skfiy.com>
 */
public final class Typhons {

    private static final Map<String, Object> props = new ConcurrentHashMap<>();

    private Typhons() {
        throw new IllegalAccessError("No org.skfiy.typhon.Typhons instances for you!");
    }
    
    /**
     * 创建一个新的{@code ObjectName }实例.
     *
     * @param str 字符串名称
     * @return {@code ObjectName }
     */
    public static ObjectName newObjectName(String str) {
        try {
            return ObjectName.getInstance(str);
        } catch (MalformedObjectNameException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    /**
     * 创建一个新的{@code URL }实例.
     *
     * @param str 字符串{@code URL }
     * @return {@code URL }
     */
    public static URL toURL(String str) {
        Assert.notNull(str);
        
        try {
            return new URL(str);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    /**
     * {@code File }转换至{@code URL }.
     *
     * @param file 文件
     * @return {@code URL }
     */
    public static URL toURL(File file) {
        Assert.notNull(file);
        return toURL(file.toURI());
    }
    
    /**
     * {@code URI }转换至{@code URL }实例.
     *
     * @param uri {@code URI }
     * @return {@code URL }
     */
    public static URL toURL(URI uri) {
        Assert.notNull(uri);
        try {
            return uri.toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    /**
     * 获取指定的属性值
     *
     * @param key 一个字符串
     * @return 字符串
     */
    public static String getProperty(String key) {
        return System.getProperty(key);
    }

    /**
     * 获取指定键的属性值. 如果指定的键并没有对应的值则返回其指定的默认值.
     * 
     * @param key 一个字符串
     * @param def 默认值
     * @return 字符串
     */
    public static String getProperty(String key, String def) {
        return System.getProperty(key, def);
    }

    /**
     * 设置指定键的属性值.
     * 
     * @param key 一个字符串
     * @param value 字符串
     * @return 设置的值
     */
    public static String setProperty(String key, String value) {
        return System.setProperty(key, value);
    }

    /**
     * 获取一个{@code boolean }属性值.
     * 
     * @param key 一个字符串
     * @return {@code boolean }
     */
    public static boolean getBoolean(String key) {
        if (props.containsKey(key)) {
            return (boolean) props.get(key);
        }

        return setBooleanFromSystem(key);
    }

    /**
     * 获取一个{@code int }属性值. 如果指定的键并不存在则默认返回{@code -1 }.
     * 
     * @param key 一个字符串
     * @return 一个数值
     */
    public static int getInteger(String key) {
        return getInteger(key, 0);
    }

    /**
     * 获取一个{@code int }值, 如果指定的键不存在则返回其指定的默认值.
     * 
     * @param key 一个字符串
     * @param def 默认值
     * @return 一个数值
     */
    public static int getInteger(String key, int def) {
        if (props.containsKey(key)) {
            return (int) props.get(key);
        }

        return setIntegerFromSystem(key, def);
    }

    /**
     * 获取一个{@code long }值, 如果指定的键不存在则默认返回{@code  -1L }.
     * 
     * @param key 一个字符串
     * @return 一个长整型
     */
    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * 获取一个{@code long }值, 如果指定的键不存在则返回其指定的默认值.
     * 
     * @param key 一个字符串
     * @param def 默认值
     * @return 长整数值
     */
    public static long getLong(String key, long def) {
        if (props.containsKey(key)) {
            return (long) props.get(key);
        }

        return setLongFromSystem(key, def);
    }

    /**
     * 获取一个{@code float }属性值. 如果指定的键不存在则默认返回{@code 0,0F }.
     * 
     * @param key 属性键
     * @return 浮点型数值
     */
    public static float getFloat(String key) {
        return getFloat(key, 0.0F);
    }

    /**
     * 获取一个{@code float }属性值. 如果指定的键不存在则返回其设置的默认值.
     * 
     * @param key 属性键
     * @param def 默认值
     * @return 浮点型数值
     */
    public static float getFloat(String key, float def) {
        if (props.containsKey(key)) {
            return (float) props.get(key);
        }

        return setFloatFromSystem(key, def);
    }

    /**
     * 获取一个{@code double }属性值. 如果指定的键不存在则默认返回{@code 0.0D }.
     * 
     * @param key 属性键
     * @return {@code double }
     */
    public static double getDouble(String key) {
        return getDouble(key, 0.0D);
    }

    /**
     * 获取一个{@code double }属性值. 如果指定的键不存在则返回其设置的默认值.
     * 
     * @param key 属性键
     * @param def 默认值
     * @return {@code double }
     */
    public static double getDouble(String key, double def) {
        if (props.containsKey(key)) {
            return (double) props.get(key);
        }

        return setDoubleFromSystem(key, def);
    }

    /**
     * 刷新缓存.
     */
    public static void refresh() {
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (clazz == Boolean.class || clazz == Boolean.TYPE) {
                setBooleanFromSystem(entry.getKey());
            } else if (clazz == Integer.class || clazz == Integer.TYPE) {
                setIntegerFromSystem(entry.getKey(), 0);
            } else if (clazz == Long.class || clazz == Long.TYPE) {
                setLongFromSystem(entry.getKey(), 0L);
            } else if (clazz == Float.class || clazz == Float.TYPE) {
                setFloatFromSystem(entry.getKey(), 0.0F);
            } else if (clazz == Double.class || clazz == Double.TYPE) {
                setDoubleFromSystem(entry.getKey(), 0.0D);
            } else {
                throw new UnsupportedOperationException("property no class ["
                        + clazz + "]");
            }
        }
    }
    
    private static boolean setBooleanFromSystem(String key) {
        boolean value = Boolean.getBoolean(key);
        props.put(key, value);
        return value;
    }

    private static int setIntegerFromSystem(String key, int def) {
        int value = Integer.getInteger(key, def);
        props.put(key, value);
        return value;
    }

    private static long setLongFromSystem(String key, long def) {
        long value = Long.getLong(key, def);
        props.put(key, value);
        return value;
    }

    private static float setFloatFromSystem(String key, float def) {
        String v = System.getProperty(key);
        float value = def;
        if (v != null) {
            try {
                value = Float.valueOf(v);
            } catch (NumberFormatException e) {
            }
        }
        props.put(key, value);
        return value;
    }

    private static double setDoubleFromSystem(String key, double def) {
        String v = System.getProperty(key);
        double value = def;
        if (v != null) {
            try {
                value = Double.valueOf(v);
            } catch (NumberFormatException e) {
            }
        }
        props.put(key, value);
        return value;
    }
}
