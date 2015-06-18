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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.management.ObjectName;
import org.apache.commons.modeler.ManagedBean;
import org.skfiy.typhon.AbstractComponent;
import org.skfiy.typhon.ComponentException;
import org.skfiy.typhon.Constants;
import org.skfiy.typhon.Typhons;
import org.skfiy.typhon.dobj.ComplexItemDobj;
import org.skfiy.typhon.dobj.SimpleItemDobj;
import org.skfiy.typhon.domain.GlobalData;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.item.ComplexItem;
import org.skfiy.typhon.domain.item.SimpleItem;
import org.skfiy.typhon.repository.GlobalDataRepository;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionManager;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.util.MBeanUtils;
import org.skfiy.util.CustomizableThreadCreator;
import org.skfiy.util.StreamUtils;
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
    private final ScheduledExecutorService SESSION_SEC = Executors.newScheduledThreadPool(1,
            new ThreadFactory() {
                CustomizableThreadCreator threadCreator = new CustomizableThreadCreator("session-");

                {
                    threadCreator.setDaemon(true);
                }

                @Override
                public Thread newThread(Runnable r) {
                    return threadCreator.createThread(r);
                }
            });
    /**
     *
     */
    private final Map<String, Object> SERVER_SETTINGS = new HashMap<>();

    @Inject
    private GlobalDataRepository globalDataReposy;
    @Inject
    private SessionManager sessionManager;
    @Resource
    private Set<Event<Player>> everydayLoopEvents;
    private ObjectName oname;

    @Override
    protected void doInit() {
        init0();

        // Fastjson 配置
        TypeUtils.addClassMapping(SimpleItemDobj.JSON_SHORT_TYPE, JSONObject.class);
        TypeUtils.addClassMapping(ComplexItemDobj.JSON_SHORT_TYPE, JSONObject.class);
        TypeUtils.addClassMapping(SimpleItem.JSON_SHORT_TYPE, SimpleItem.class);
        TypeUtils.addClassMapping(ComplexItem.JSON_SHORT_TYPE, ComplexItem.class);

        ManagedBean managedBean = MBeanUtils.findManagedBean(getClass());
        oname = MBeanUtils.registerComponent(this, managedBean);

        // 获取服务器设置数据
        GlobalData globalData = globalDataReposy.getGlobalData(GlobalData.Type.server_settings);
        SERVER_SETTINGS.putAll(JSON.parseObject(globalData.getData()));

        if (!SERVER_SETTINGS.containsKey(ServerSettingKeys.SERVER_INIT_TIME)) {
            SERVER_SETTINGS.put(ServerSettingKeys.SERVER_INIT_TIME, System.currentTimeMillis());
            SERVER_SETTINGS.put(ServerSettingKeys.SERVER_TIME_ZONE_ID, TimeZone.getDefault().getID());
        }

        Calendar nextCal = Calendar.getInstance();
        nextCal.add(Calendar.DAY_OF_MONTH, 1);
        nextCal.set(Calendar.HOUR_OF_DAY, 0);
        nextCal.set(Calendar.MINUTE, 0);
        nextCal.set(Calendar.SECOND, 0);

        SESSION_SEC.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                for (Session session : sessionManager.findSessions()) {
                    synchronized (session) {
                        if (session.isAvailable()) {
                            execute(session);
                        }
                    }
                } // end
            }

            private void execute(Session session) {
                try {
                    Player player = SessionUtils.getPlayer(session);
                    for (Event<Player> event : everydayLoopEvents) {
                        event.invoke(player);
                    }
                    player.getNormal().setLastResetTime(System.currentTimeMillis());
                } catch (Exception e) {
                    LOG.warn("每天12点重置信息失败 -> {}", session, e);
                }
            }
        }, nextCal.getTimeInMillis() - System.currentTimeMillis() + 1000, 24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void doReload() {
        init0();
        Typhons.refresh();
    }

    @Override
    protected void doDestroy() {
        // 更新服务器配置数据
        GlobalData globalData = new GlobalData();
        globalData.setType(GlobalData.Type.server_settings);
        globalData.setData(JSON.toJSONString(SERVER_SETTINGS));
        globalDataReposy.updateGlobalData(globalData);

        if (oname != null) {
            MBeanUtils.REGISTRY.unregisterComponent(oname);
        }
    }

    //==============================================================================================
    /**
     *
     * @param key
     * @param value
     */
    public void setServerProperty(String key, Object value) {
        SERVER_SETTINGS.put(key, value);
    }

    /**
     *
     * @param key
     * @return
     */
    public String getServerString(String key) {
        return getServerString(key, null);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getServerString(String key, String defaultValue) {
        Object o = SERVER_SETTINGS.get(key);
        return (o == null) ? defaultValue : String.valueOf(o);
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean getServerBoolean(String key) {
        return getServerBoolean(key, false);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getServerBoolean(String key, boolean defaultValue) {
        Object o = SERVER_SETTINGS.get(key);
        return o == null ? defaultValue : (boolean) o;
    }

    /**
     *
     * @param key
     * @return
     */
    public int getServerInt(String key) {
        return getServerInt(key, 0);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getServerInt(String key, int defaultValue) {
        Object o = SERVER_SETTINGS.get(key);
        return o == null ? defaultValue : (int) o;
    }

    /**
     *
     * @param key
     * @return
     */
    public long getServerLong(String key) {
        return getServerLong(key, 0);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getServerLong(String key, long defaultValue) {
        Object o = SERVER_SETTINGS.get(key);
        return (o == null) ? defaultValue : (long) o;
    }

    /**
     *
     * @param key
     * @return
     */
    public float getServerFloat(String key) {
        return getServerFloat(key, 0F);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getServerFloat(String key, float defaultValue) {
        Object o = SERVER_SETTINGS.get(key);
        return o == null ? defaultValue : (float) o;
    }

    /**
     *
     * @param key
     * @return
     */
    public double getServerDouble(String key) {
        return getServerDouble(key, 0D);
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public double getServerDouble(String key, double defaultValue) {
        Object o = SERVER_SETTINGS.get(key);
        return o == null ? defaultValue : (double) o;
    }

    //==============================================================================================
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
            String json = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            JSONArray jsonArray = JSON.parseArray(json);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                props.put(o.getString("key"), o.getString("value"));
            }
        } catch (IOException ex) {
            throw new ComponentException(ex);
        }
        return props;
    }

    private File getConfigFile() {
        File f = new File(System.getProperty(Constants.COMPONENT_DATAS_DIR), "properties.json");
        return f;
    }

}
