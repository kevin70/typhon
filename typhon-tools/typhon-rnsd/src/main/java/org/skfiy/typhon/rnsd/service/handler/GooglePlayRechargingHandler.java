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
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.skfiy.typhon.rnsd.domain.Platform;
import org.skfiy.typhon.rnsd.domain.Recharging;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class GooglePlayRechargingHandler implements RechargingHandler {

    private HttpTransport transport;
    private AndroidPublisher publisher;

    public GooglePlayRechargingHandler() {
        try {
            transport = GoogleNetHttpTransport.newTrustedTransport();

            PrivateKey privateKey = SecurityUtils.loadPrivateKeyFromKeyStore(
                    SecurityUtils.getPkcs12KeyStore(),
                    getClass().getClassLoader().getResourceAsStream("eagle7.p12"),
                    "notasecret", "privatekey", "notasecret");

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(transport).setJsonFactory(JacksonFactory.getDefaultInstance())
                    .setServiceAccountId("123224341041-5j8ahccu09vst950kt6bkpu1r3l1qbta@developer.gserviceaccount.com")
                    .setServiceAccountScopes(AndroidPublisherScopes.all())
                    .setServiceAccountPrivateKey(privateKey).build();

            publisher = new AndroidPublisher.Builder(transport,
                    JacksonFactory.getDefaultInstance(), credential).build();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RechargingBO handle(JSONObject json) throws TradeValidatedException {
        AndroidPublisher.Purchases.Products products = publisher.purchases().products();

        try {
            AndroidPublisher.Purchases.Products.Get product = products.get("com.vsg.eagle7",
                    json.getString("productId"), json.getString("token"));

            ProductPurchase purchase = product.execute();
            if (purchase.getConsumptionState() != 1 || purchase.getPurchaseState() != 0) {
                throw new TradeValidatedException("success", "no verify: " + purchase);
            }

            JSONObject extra = JSON.parseObject(purchase.getDeveloperPayload());

            Recharging recharging = new Recharging();
            recharging.setTradeId(json.getString("orderId"));
            recharging.setPlatform(Platform.googleplay.getLabel());

            recharging.setUid(extra.getString("uid"));
            recharging.setRegion(extra.getString("region"));
            recharging.setGoods(extra.getString("goods"));
            recharging.setAmount(extra.getIntValue("goods"));

            recharging.setCreationTime(System.currentTimeMillis() / 1000);
            recharging.setChannel(json.getString("channelLabel"));

            return (new RechargingBO(recharging, "success"));
        } catch (IOException e) {
            throw new TradeValidatedException("success", e.getMessage());
        }
    }

}
