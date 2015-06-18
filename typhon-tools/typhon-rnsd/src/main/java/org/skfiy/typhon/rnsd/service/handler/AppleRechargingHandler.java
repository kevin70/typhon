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
package org.skfiy.typhon.rnsd.service.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.skfiy.typhon.rnsd.domain.Platform;
import org.skfiy.typhon.rnsd.domain.Recharging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class AppleRechargingHandler implements RechargingHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AppleRechargingHandler.class);
    private static final String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";
    private static final String VERIFY_URL = "https://buy.itunes.apple.com/verifyReceipt";

    private final HttpClientBuilder HC_BUILDER = HttpClientBuilder.create();
    private final Pattern P = Pattern.compile("(\"signature\" = \"(.*?)\";)");

    @Resource
    private Map<String, Integer> goodsPrices;

    public AppleRechargingHandler() throws Exception {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                new java.security.SecureRandom());
        HC_BUILDER.setSslcontext(sc);
    }

    @Override
    public RechargingBO handle(JSONObject json) throws TradeValidatedException {
        String uri;
        if ("Sandbox".equalsIgnoreCase(json.getString("environment"))) {
            uri = SANDBOX_URL;
        } else {
            uri = VERIFY_URL;
        }

        CloseableHttpClient hc = HC_BUILDER.build();
        HttpPost post = new HttpPost(uri);

        List<NameValuePair> nvps = new ArrayList<>();

        String receiptStr = json.getString("data");
        Matcher m = P.matcher(receiptStr);
        m.find();

        String signature = m.group(2).replaceAll(" ", "+");
        receiptStr = receiptStr.replace(m.group(2), signature);

        String receiptData = org.skfiy.typhon.rnsd.Base64.encodeBytes(receiptStr.getBytes());
        JSONObject receiptJson = new JSONObject();
        receiptJson.put("receipt-data", receiptData);

        try {

            post.setEntity(new StringEntity(receiptJson.toJSONString(), ContentType.APPLICATION_JSON));
            CloseableHttpResponse resp = hc.execute(post);

            String str = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
            JSONObject result = JSON.parseObject(str);

            if (result.getIntValue("status") != 0) {
                throw new TradeValidatedException("success", "no verify");
            }

            JSONObject receipt = result.getJSONObject("receipt");

            // result
            Recharging recharging = new Recharging();
            recharging.setTradeId(receipt.getString("transaction_id"));
            recharging.setPlatform(Platform.apple.getLabel());

            String callbackInfo = StringUtils.newStringUtf8(Base64.decodeBase64(json.getString("callbackInfo")));
            JSONObject extra = JSON.parseObject(callbackInfo);

            recharging.setUid(extra.getString("uid"));
            recharging.setRegion(extra.getString("region"));
            recharging.setGoods(extra.getString("goods"));

            LOG.debug("{}", extra);

            recharging.setAmount(extra.getInteger("goods"));
            recharging.setCreationTime(System.currentTimeMillis() / 1000);
            recharging.setChannel(Platform.apple.getLabel());

            return (new RechargingBO(recharging, "success"));
        } catch (Exception ex) {
            throw new TradeValidatedException("success", ex.getMessage());
        } finally {
            try {
                hc.close();
            } catch (IOException ex) {
            }
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
