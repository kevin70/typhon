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
import org.apache.commons.modeler.ManagedBean;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.domain.item.ComplexItem;
import org.skfiy.typhon.domain.item.SimpleItem;
import org.skfiy.typhon.dobj.ComplexItemDobj;
import org.skfiy.typhon.dobj.SimpleItemDobj;
import org.skfiy.typhon.util.MBeanUtils;
import org.skfiy.util.SystemPropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加载默认基础配置.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class ConfigurationLoader extends AbstractComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationLoader.class);
    private ObjectName oname;

    @Override
    public void doInit() {
        init0();

        // Fastjson 配置
        TypeUtils.addClassMapping(SimpleItemDobj.JSON_SHORT_TYPE, JSONObject.class);
        TypeUtils.addClassMapping(ComplexItemDobj.JSON_SHORT_TYPE, JSONObject.class);
        TypeUtils.addClassMapping(SimpleItem.JSON_SHORT_TYPE, SimpleItem.class);
        TypeUtils.addClassMapping(ComplexItem.JSON_SHORT_TYPE, ComplexItem.class);

        ManagedBean managedBean = MBeanUtils.findManagedBean(getClass());
        oname = MBeanUtils.registerComponent(this, managedBean);
    }

    @Override
    public void doReload() {
        init0();
        Typhons.refresh();
    }

    @Override
    public void doDestroy() {
        if (oname != null) {
            MBeanUtils.REGISTRY.unregisterComponent(oname);
        }
    }

    private void init0() {
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
