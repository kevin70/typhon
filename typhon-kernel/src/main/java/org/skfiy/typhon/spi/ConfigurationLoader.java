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
package org.skfiy.typhon.spi;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import javax.management.ObjectName;
import org.apache.commons.modeler.Registry;
import org.skfiy.typhon.Component;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.item.DynamicComplexItem;
import org.skfiy.typhon.domain.item.DynamicSimpleItem;
import org.skfiy.typhon.domain.item.StaticComplexItem;
import org.skfiy.typhon.domain.item.StaticSimpleItem;
import org.skfiy.util.SystemPropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class ConfigurationLoader implements Component {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLoader.class);
    
    private ObjectName oname;
    
    @Override
    public void init() {
        initInertal();
        
        // Fastjson 配置
        TypeUtils.addClassMapping(StaticSimpleItem.JSON_SHORT_TYPE, JSONObject.class);
        TypeUtils.addClassMapping(StaticComplexItem.JSON_SHORT_TYPE, JSONObject.class);
        TypeUtils.addClassMapping(DynamicSimpleItem.JSON_SHORT_TYPE, DynamicSimpleItem.class);
        TypeUtils.addClassMapping(DynamicComplexItem.JSON_SHORT_TYPE, DynamicComplexItem.class);
        
        // MBean 注册
        oname = Typhons.newObjectName(
                Globals.DEFAULT_MBEAN_DOMAIN + ".spi:name=ConfigurationLoader");
        try {
            Registry.getRegistry(null, null).registerComponent(this, oname, null);
        } catch (Exception ex) {
            LOG.error("registry component: {}", oname, ex);
            throw new ComponentException(ex);
        }
    }

    @Override
    public void reload() {
        initInertal();
        Typhons.refresh();
    }

    @Override
    public void destroy() {
        if (oname != null) {
            Registry.getRegistry(null, null).unregisterComponent(oname);
        }
    }

    private void initInertal() {
        Properties props = loadConfig();
        System.getProperties().putAll(props);
        
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String val = SystemPropertyUtils.resolvePlaceholders(
                    (String) entry.getValue());
            System.setProperty((String) entry.getKey(), val);
        }
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(getConfigFile());) {
            props.loadFromXML(in);
        } catch (IOException ex) {
            throw new ComponentException(ex);
        }
        return props;
    }

    private File getConfigFile() {
        File f = new File(System.getProperty(Constants.COMPONENT_DATAS_DIR), "properties.xml");
        return f;
    }

}
