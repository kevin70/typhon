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
package org.skfiy.typhon.spi.atlasloot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.domain.Lootable;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.spi.ItemProvider;
import org.skfiy.typhon.util.ComponentUtils;

/**
 *
 * @author Kevin
 */
@Singleton
public class AtlaslootProvider extends AbstractComponent {

    private final Map<String, RandomAtlaslootBean> allAtlasloots;

    @Inject
    private ItemProvider itemProvider;

    public AtlaslootProvider() {
        allAtlasloots = new HashMap<>();
    }

    @Override
    protected void doInit() {
        loadDatas();
    }

    @Override
    protected void doReload() {
        loadDatas();
    }

    @Override
    protected void doDestroy() {
    }

    /**
     * 
     * @param session
     * @param lootable
     * @param atlasloots
     * @param ids 
     */
    public void calculateAtlasloot(Session session, Lootable lootable,
            List<AtlaslootBean> atlasloots, String... ids) {
        RandomAtlaslootBean randomAtlaslootBean;
        AtlaslootBean atlaslootBean;

        for (String id : ids) {
            randomAtlaslootBean = allAtlasloots.get(id);
            if (randomAtlaslootBean == null) {
                throw new ComponentException("Not found atlasloot[" + id + "]");
            }

            atlaslootBean = randomAtlaslootBean.calculate(session, lootable);
            if (atlaslootBean != null) {
                atlasloots.add(atlaslootBean);
            }
        }
    }
    
    private void loadDatas() {
        JSONArray jsonArray = JSON.parseArray(ComponentUtils.readDataFile("single_atlasloot.json"));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            SingleAtlasloot atlasloot = new SingleAtlasloot();
            atlasloot.setId(jsonObject.getString("id"));
            atlasloot.setItem(itemProvider.getItem(jsonObject.getString("#item.id")));
            atlasloot.setCount(jsonObject.getIntValue("count"));
            atlasloot.setProb(jsonObject.getDoubleValue("prob"));
            atlasloot.setFactor(jsonObject.getIntValue("factor"));
            atlasloot.prepare();
            allAtlasloots.put(atlasloot.getId(), atlasloot);
        }

        jsonArray = JSON.parseArray(ComponentUtils.readDataFile("multiple_atlasloot.json"));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            MultipleAtlasloot atlasloot = new MultipleAtlasloot();
            atlasloot.setId(jsonObject.getString("id"));

            JSONArray lootArray = jsonObject.getJSONArray("loots");
            AtlaslootBean[] atlaslootBeans = new AtlaslootBean[lootArray.size()];
            for (int j = 0; j < atlaslootBeans.length; j++) {
                JSONObject lootObject = lootArray.getJSONObject(j);

                AtlaslootBean bean = new AtlaslootBean();
                bean.setItem(itemProvider.getItem(lootObject.getString("#item.id")));
                bean.setCount(lootObject.getIntValue("count"));
                bean.setProb(lootObject.getDoubleValue("prob"));

                atlaslootBeans[j] = bean;
            }
            
            double prevProb = 0;
            for (AtlaslootBean ab : atlaslootBeans) {
                ab.setProb(prevProb + ab.getProb());
                prevProb = ab.getProb();
            }
            atlasloot.setAtlasloots(atlaslootBeans);
            allAtlasloots.put(atlasloot.getId(), atlasloot);
        }
    }

}
