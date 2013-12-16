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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.skfiy.util.ResourceUtils;
import org.skfiy.util.SystemPropertyUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public abstract class TestRoot {

    static {
        System.setProperty(Globals.PROP_DEV_MODE, "true");
        
        InputStream stream = null;
        try {
            Properties props = new Properties();
            stream = ResourceUtils.getURL("classpath:typhon.properties").openStream();
            props.load(stream);

            // loading... ext.properties
            loadExtProperties(props);

            // put all to system properties
            System.getProperties().putAll(props);

            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                String val = SystemPropertyUtils.resolvePlaceholders(
                        (String) entry.getValue());
                System.setProperty((String) entry.getKey(), val);
            }

        } catch (FileNotFoundException ex) {
            throw new TyphonException("没有在classpath环境中找到typhon.properties文件", ex);
        } catch (IOException ex) {
            throw new TyphonException("加载typhon.propertiesr失败", ex);
        } finally {
            try {
                stream.close();
            } catch (Exception ex) {
                // nothing
            }
        }
    }

    private static void loadExtProperties(Properties props) {
        File extFile = new File(System.getProperty("typhon.home"),
                "/conf/ext.properties");
        if (extFile.exists()) {
            InputStream stream = null;
            try {
                stream = new FileInputStream(extFile);
                props.load(stream);
            } catch (IOException ex) {
                throw new TyphonException("加载 $TYPHON_HOME/conf/ext.properties 失败", ex);
            } finally {
                try {
                    stream.close();
                } catch (Exception ex) {
                }
            }
        }
    }
}
