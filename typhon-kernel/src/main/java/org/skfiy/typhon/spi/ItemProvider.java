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
package org.skfiy.typhon.spi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.management.ObjectName;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.modeler.ManagedBean;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.dobj.ItemDobj;
import org.skfiy.typhon.script.ScriptManager;
import org.skfiy.typhon.spi.item.ItemCompleter;
import org.skfiy.typhon.spi.item.NotFoundItemException;
import org.skfiy.typhon.util.ComponentUtils;
import org.skfiy.typhon.util.MBeanUtils;
import org.skfiy.util.AntPathMatcher;
import org.skfiy.util.Assert;
import org.skfiy.util.PathMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class ItemProvider extends AbstractComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ItemProvider.class);
    
    private ObjectName oname;
    private Map<String, ItemDobj> items = new HashMap<>();
    
    @Resource
    private Set<ItemCompleter> itemCompleters;
    @Resource
    private ScriptManager scriptManager;

    /**
     * 
     * @param <T>
     * @param id
     * @return 
     */
    public <T extends ItemDobj> T getItem(String id) {
        Assert.notNull(id,
                "[Assertion failed] - item \"id\" argument is required; it must not be null");
        T item = (T) items.get(id);
        if (item == null) {
            throw new NotFoundItemException("Not found item [" + id + "]");
        }
        return item;
    }

    @Override
    public void doInit() {
        items.putAll(loadItems());
        
        ManagedBean managedBean = MBeanUtils.findManagedBean(getClass());
        MBeanUtils.registerComponent(this, managedBean);
        
        LOG.info("item init successful.");
    }

    @Override
    public void doReload() {
        Map<String, ItemDobj> itemMap = loadItems();
        for (Map.Entry<String, ItemDobj> entry : itemMap.entrySet()) {
            ItemDobj oldItem = items.get(entry.getKey());
            if (oldItem == null) {
                items.put(entry.getKey(), entry.getValue());
            } else {
                try {
                    BeanUtils.copyProperties(oldItem, entry.getValue());
                } catch (Exception ex) {
                    LOG.error("resetting item[id={}] failed.", entry.getKey(), ex);
                    throw new ComponentException(ex);
                }
            }
        }
        
        LOG.info("item reload successful.");
    }

    @Override
    public void doDestroy() {
        items.clear();
        items = null;
        
        if (oname != null) {
            MBeanUtils.REGISTRY.unregisterComponent(oname);
        }
        LOG.info("item destroy successful.");
    }

    private Map<String, ItemDobj> loadItems() {
        File[] itemFiles = findItemFiles();
        JSONArray jsonArray = new JSONArray();

        for (File file : itemFiles) {
            jsonArray.addAll(JSON.parseArray(ComponentUtils.readDataFile(file)));
        }

        Map<String, ItemDobj> itemMap = new HashMap<>();
        for (Iterator<Object> it = jsonArray.iterator(); it.hasNext();) {
            JSONObject json = (JSONObject) it.next();
            ItemCompleter itemCompleter = findItemCompleter(json.getString("type"));
            
            try {
                ItemDobj staticItem = itemCompleter.prepare(json);
                
                if (json.containsKey("script")) {
                    staticItem.setScript(scriptManager.getScript(json.getString("script")));
                }
                itemMap.put(staticItem.getId(), staticItem);
            } catch (Exception e) {
                LOG.error("item {}", json.toJSONString(), e);
                throw new ComponentException(e);
            }
        }

        for (Iterator<Object> it = jsonArray.iterator(); it.hasNext();) {
            JSONObject json = (JSONObject) it.next();
            ItemCompleter itemCompleter = findItemCompleter(json.getString("type"));
            
            try {
                itemCompleter.complete(itemMap, json);
            } catch (Exception e) {
                LOG.error("item {}", json, e);
                throw new ComponentException(e);
            }
        }
        return itemMap;
    }

    private File[] findItemFiles() {
        final PathMatcher pathMatcher = new AntPathMatcher();
        File datasDir = new File(System.getProperty(Constants.COMPONENT_DATAS_DIR));
        return datasDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return (!"item_monster.json".equals(name)
                        && pathMatcher.match("item_*.json", name));
            }
        });
    }
    
    private ItemCompleter findItemCompleter(String type) {
        for (ItemCompleter itemCompleter : itemCompleters) {
            if (itemCompleter.getType().equals(type)) {
                return itemCompleter;
            }
        }

        throw new IllegalArgumentException("Not found ["
                + type + "] ItemCompleter");
    }
    
}
