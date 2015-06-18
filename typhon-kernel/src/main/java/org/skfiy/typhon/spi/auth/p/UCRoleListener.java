/*
 * Copyright 2015 The Skfiy Open Association.
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
package org.skfiy.typhon.spi.auth.p;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.skfiy.typhon.domain.Player;
import org.skfiy.typhon.domain.Role;
import org.skfiy.typhon.session.Session;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.session.SessionListener;
import org.skfiy.typhon.session.SessionUtils;
import org.skfiy.typhon.spi.ConfigurationLoader;
import org.skfiy.typhon.spi.auth.OAuth2Exception;
import org.skfiy.typhon.spi.role.AbstractRoleListener;
import org.skfiy.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class UCRoleListener extends AbstractRoleListener {

    private static final Logger LOG = LoggerFactory.getLogger(UCRoleListener.class);
    
    @Resource
    private ConfigurationLoader configurationLoader;

    @Override
    public void roleCreated(Role role) {
        Object p = SessionContext.getSession().getAttribute(UCAuthenticator.SESSION_SID_KEY);
        if (p == null) {
            return;
        }

        CloseableHttpClient hc = UCAuthenticator.HC_BUILDER.build();
        HttpPost httpPost = new HttpPost("http://sdk.g.uc.cn/cp/account.verifySession");

        JSONObject json = new JSONObject();
        json.put("id", System.currentTimeMillis());
        json.put("service", "ucid.game.gameData");

        String sid = (String) p;
        String gameData = getGameData();

        JSONObject data = new JSONObject();
        data.put("sid", sid);
        data.put("gameData", gameData);
        json.put("data", data);

        JSONObject game = new JSONObject();
        game.put("gameId", UCAuthenticator.gameId);
        json.put("game", game);

        json.put("sign", getSign(gameData, sid));

        try {
            String body = json.toJSONString();
            LOG.debug("UC loginGameRole request: {}", body);

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));
            hc.execute(httpPost, new ResponseHandler<JSONObject>() {

                @Override
                public JSONObject handleResponse(HttpResponse response)
                        throws ClientProtocolException, IOException {
                    String str = StreamUtils.copyToString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                    LOG.debug("UC loginGameRole response: {}", str);
                    return JSON.parseObject(str);
                }
            });
        } catch (IOException e) {
            throw new OAuth2Exception("UC提交失败", e);
        } finally {
            try {
                hc.close();
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public void roleLoaded(Role role) {
        roleCreated(role);
    }

    private String getSign(String gameData, String sid) {
        StringBuilder sb = new StringBuilder();
        sb.append("gameData=").append(gameData);
        sb.append("sid=").append(sid);
        sb.append(UCAuthenticator.apikey);

        return DigestUtils.md5Hex(sb.toString());
    }

    private String getGameData() {
        JSONObject json = new JSONObject();
        json.put("category", "loginGameRole");

        Player player = SessionUtils.getPlayer();
        Role role = player.getRole();

        JSONObject content = new JSONObject();
        content.put("roleId", role.getRid());
        content.put("roleLevel", role.getLevel());
        content.put("roleName", role.getName());
        content.put("zoneId", configurationLoader.getServerInt("server.zoneId"));
        content.put("zoneName", configurationLoader.getServerString("server.zoneName"));
        json.put("content", content);

        try {
            return URLEncoder.encode(json.toJSONString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
