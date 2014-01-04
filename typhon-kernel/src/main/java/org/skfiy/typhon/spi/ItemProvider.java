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
import com.alibaba.fastjson.util.TypeUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.beanutils.BeanUtils;
import org.skfiy.typhon.Component;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.Globals;
import org.skfiy.typhon.domain.item.StaticComplexItem;
import org.skfiy.typhon.domain.item.StaticItem;
import org.skfiy.typhon.domain.item.StaticSimpleItem;
import org.skfiy.typhon.domain.item.Subitem;
import org.skfiy.typhon.spi.item.ItemCompleter;
import org.skfiy.typhon.spi.item.NotFoundItemException;
import org.skfiy.util.AntPathMatcher;
import org.skfiy.util.Assert;
import org.skfiy.util.PathMatcher;
import org.skfiy.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class ItemProvider implements Component {

    private static final Logger LOG = LoggerFactory.getLogger(ItemProvider.class);
    private Map<String, StaticItem> items = new HashMap<>();
    
    @Inject
    private Set<ItemCompleter> itemCompleters;

    /**
     * 
     * @param <T>
     * @param id
     * @return 
     */
    public <T extends StaticItem> T getItem(String id) {
        Assert.notNull(id,
                "[Assertion failed] - item \"id\" argument is required; it must not be null");
        T item = (T) items.get(id);
        if (item == null) {
            throw new NotFoundItemException("Not found item [id=" + id + "]");
        }
        return item;
    }

    @Override
    public void init() {
        items.putAll(loadItems());
        
        LOG.info("item init successful.");
    }

    @Override
    public void reload() {
        Map<String, StaticItem> itemMap = loadItems();
        for (Map.Entry<String, StaticItem> entry : itemMap.entrySet()) {
            StaticItem oldItem = items.get(entry.getKey());
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
    public void destroy() {
        items.clear();
        items = null;
        
        LOG.info("item destroy successful.");
    }

    private Map<String, StaticItem> loadItems() {
        File[] itemFiles = findItemFiles();
        JSONArray jsonArray = new JSONArray();

        InputStream in = null;
        for (File file : itemFiles) {
            try {
                in = new FileInputStream(file);
                String text = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
                jsonArray.addAll(JSON.parseObject(text).getJSONArray("datas"));
            } catch (IOException ex) {
                LOG.debug("init item", ex);
                throw new ComponentException(ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }

        Map<String, StaticItem> itemMap = new HashMap<>();
        for (Iterator<Object> it = jsonArray.iterator(); it.hasNext();) {
            JSONObject json = (JSONObject) it.next();
            ItemCompleter itemCompleter = findItemCompleter(json.getString("type"));
            StaticItem staticItem = itemCompleter.prepare(json);
            itemMap.put(staticItem.getId(), staticItem);
        }

        for (Iterator<Object> it = jsonArray.iterator(); it.hasNext();) {
            JSONObject json = (JSONObject) it.next();
            ItemCompleter itemCompleter = findItemCompleter(json.getString("type"));
            itemCompleter.complete(itemMap, json);
        }
        return itemMap;
    }

    private File[] findItemFiles() {
        final PathMatcher pathMatcher = new AntPathMatcher();
        File datasDir = new File(System.getProperty(Constants.COMPONENT_DATAS_DIR));
        return datasDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return pathMatcher.match("item_*.json", name);
            }
        });
    }
    
    private ItemCompleter findItemCompleter(String type) {
        for (ItemCompleter itemCompleter : itemCompleters) {
            if (type.equals(itemCompleter.getType())) {
                return itemCompleter;
            }
        }

        throw new IllegalArgumentException("Not found [" + type + "] ItemCompleter");
    }

}