package org.skfiy.typhon.spi.auth.p;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.skfiy.typhon.packet.OAuth2;
import org.skfiy.typhon.packet.Platform;
import org.skfiy.typhon.spi.auth.OAuth2Exception;
import org.skfiy.typhon.spi.auth.OAuthenticator;
import org.skfiy.util.StreamUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.skfiy.typhon.session.SessionContext;
import org.skfiy.typhon.spi.auth.UserInfo;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class UCAuthenticator implements OAuthenticator {

    static final HttpClientBuilder HC_BUILDER = HttpClientBuilder.create();
    static final String apikey = "2e0662d2220d70a1692fa2d724c8a46d";
    static final int gameId = 552397;
    static final String SESSION_SID_KEY = "__uc.platform.sid__";

    @Override
    public UserInfo authentic(OAuth2 oauth) {
        CloseableHttpClient hc = HC_BUILDER.build();
        HttpPost httpPost = new HttpPost("http://sdk.g.uc.cn/cp/account.verifySession");

        StringBuilder buffer = new StringBuilder();
        buffer.append("sid=");
        buffer.append(oauth.getCode());
        buffer.append(apikey);
        byte[] signByte = DigestUtils.md5(buffer.toString());

        String sign = toHex(signByte);
        JSONObject data = new JSONObject();
        data.put("sid", oauth.getCode());

        JSONObject game = new JSONObject();
        game.put("gameId", gameId);

        JSONObject parameter = new JSONObject();
        parameter.put("id", System.currentTimeMillis());
        parameter.put("data", data);
        parameter.put("game", game);
        parameter.put("sign", sign);
        String boby = parameter.toString();

        try {

            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new ByteArrayEntity(boby.getBytes("UTF-8")));
            JSONObject json = hc.execute(httpPost, new ResponseHandler<JSONObject>() {

                @Override
                public JSONObject handleResponse(HttpResponse response)
                        throws ClientProtocolException, IOException {
                    String str = StreamUtils.copyToString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                    return JSON.parseObject(str);
                }
            });

            JSONObject object = json.getJSONObject("state");
            if (object.getInteger("code") != 1) {
                throw new OAuth2Exception(object.getString("msg"));
            }

            UserInfo info = new UserInfo();
            info.setUsername(getPlatform().getLabel() + "-"
                    + json.getJSONObject("data").getString("accountId"));
            info.setPlatform(getPlatform());
            
            // 设置SID
            SessionContext.getSession().setAttribute(SESSION_SID_KEY, oauth.getCode());
            return info;
        } catch (IOException e) {
            throw new OAuth2Exception("UC认证失败", e);
        } finally {
            try {
                hc.close();
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public Platform getPlatform() {
        return Platform.uc;
    }

    private String toHex(byte[] byteArray) {
        StringBuilder md5StrBuff = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }
    
}
