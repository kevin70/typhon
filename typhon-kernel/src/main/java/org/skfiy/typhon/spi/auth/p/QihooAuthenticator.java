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
package org.skfiy.typhon.spi.auth.p;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.skfiy.typhon.packet.OAuth2;
import org.skfiy.typhon.packet.Platform;
import org.skfiy.typhon.spi.auth.OAuth2Exception;
import org.skfiy.typhon.spi.auth.OAuthenticator;
import org.skfiy.typhon.spi.auth.UserInfo;
import org.skfiy.util.StreamUtils;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class QihooAuthenticator implements OAuthenticator {

    private final HttpClientBuilder HC_BUILDER = HttpClientBuilder.create();

    public QihooAuthenticator() {
        try {
            SSLContextBuilder sslBuilder = new SSLContextBuilder();
            sslBuilder.loadTrustMaterial(null, new TrustStrategy() {

                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            });

            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslBuilder.build());
            HC_BUILDER.setSSLSocketFactory(sslFactory);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public UserInfo authentic(OAuth2 oauth) {
        CloseableHttpClient hc = HC_BUILDER.build();
        HttpPost httpPost = new HttpPost("https://openapi.360.cn/user/me");

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("access_token", oauth.getCode()));

        try {

            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            JSONObject json = hc.execute(httpPost, new ResponseHandler<JSONObject>() {

                @Override
                public JSONObject handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    String str = StreamUtils.copyToString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                    return JSON.parseObject(str);
                }
            });

            if (json.containsKey("error_code")) {
                throw new OAuth2Exception(json.getString("error_code"));
            }
            
            UserInfo info = new UserInfo();
            info.setUsername(getPlatform().getLabel() + "-" + json.getString("name"));
            info.setPlatform(getPlatform());
            return info;
        } catch (IOException ex) {
            throw new OAuth2Exception("qihoo认证失败", ex);
        } finally {
            try {
                hc.close();
            } catch (IOException ex) {
            }
        }
    }

    @Override
    public Platform getPlatform() {
        return Platform.qihoo;
    }

}
