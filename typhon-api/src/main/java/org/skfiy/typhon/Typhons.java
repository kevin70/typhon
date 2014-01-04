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
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return System.getProperty(key);
    }

    /**
     *
     * @param key
     * @param def
     * @return
     */
    public static String getProperty(String key, String def) {
        return System.getProperty(key, def);
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public static String setProperty(String key, String value) {
        return System.setProperty(key, value);
    }

    /**
     *
     * @param key
     * @return
     */
    public static boolean getBoolean(String key) {
        if (props.containsKey(key)) {
            return (Boolean) props.get(key);
        }

        return setBooleanFromSystem(key);
    }

    /**
     *
     * @param key
     * @return
     */
    public static int getInteger(String key) {
        return getInteger(key, -1);
    }

    /**
     *
     * @param key
     * @param def
     * @return
     */
    public static int getInteger(String key, int def) {
        if (props.containsKey(key)) {
            return (Integer) props.get(key);
        }

        return setIntegerFromSystem(key, def);
    }

    /**
     *
     * @param key
     * @return
     */
    public static long getLong(String key) {
        return getLong(key, -1L);
    }

    /**
     *
     * @param key
     * @param def
     * @return
     */
    public static long getLong(String key, long def) {
        if (props.containsKey(key)) {
            return (Long) props.get(key);
        }

        return setLongFromSystem(key, def);
    }

    /**
     *
     * @param key
     * @return
     */
    public static float getFloat(String key) {
        return getFloat(key, 0.0F);
    }

    /**
     *
     * @param key
     * @param def
     * @return
     */
    public static float getFloat(String key, float def) {
        if (props.containsKey(key)) {
            return (Float) props.get(key);
        }

        return setFloatFromSystem(key, def);
    }

    /**
     *
     * @param key
     * @return
     */
    public static double getDouble(String key) {
        return getDouble(key, 0.0D);
    }

    /**
     *
     * @param key
     * @param def
     * @return
     */
    public static double getDouble(String key, double def) {
        if (props.containsKey(key)) {
            return (Double) props.get(key);
        }

        return setDoubleFromSystem(key, def);
    }

    /**
     *
     */
    public static void refresh() {
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            Class<?> clazz = entry.getKey().getClass();
            if (clazz == Boolean.class) {
                setBooleanFromSystem(entry.getKey());
            } else if (clazz == Integer.class) {
                setIntegerFromSystem(entry.getKey(), -1);
            } else if (clazz == Long.class) {
                setLongFromSystem(entry.getKey(), -1L);
            } else if (clazz == Float.class) {
                setFloatFromSystem(entry.getKey(), 0.0F);
            } else if (clazz == Double.class) {
                setDoubleFromSystem(entry.getKey(), 0.0D);
            } else {
                throw new UnsupportedOperationException("property no class ["
                        + clazz + "]");
            }
        }
    }
    
    /**
     * FIXME 
     * @return 
     */
    public static boolean isDevMode() {
        return Typhons.getBoolean(Globals.PROP_DEV_MODE);
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
