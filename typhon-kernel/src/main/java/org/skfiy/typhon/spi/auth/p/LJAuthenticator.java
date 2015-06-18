package org.skfiy.typhon.spi.auth.p;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.skfiy.typhon.packet.OAuth2;
import org.skfiy.typhon.packet.Platform;
import org.skfiy.typhon.spi.auth.OAuth2Exception;
import org.skfiy.typhon.spi.auth.OAuthenticator;
import org.skfiy.typhon.spi.auth.UserInfo;
import org.skfiy.util.StreamUtils;

public class LJAuthenticator implements OAuthenticator {

    private final HttpClientBuilder HC_BUILDER = HttpClientBuilder.create();

    //产品标识
    private static final String productCode = "p1349";

    @Override
    public UserInfo authentic(OAuth2 oauth) {
        CloseableHttpClient hc = HC_BUILDER.build();

        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("productCode", productCode));
        nvps.add(new BasicNameValuePair("channel", oauth.getChannel()));
        nvps.add(new BasicNameValuePair("userId", oauth.getUid()));
        nvps.add(new BasicNameValuePair("token", oauth.getCode()));

        try {
            for (int i = 0; i < 10; i++) {
                try {
                    HttpPost httpPost = new HttpPost("http://gameproxy.xinmei365.com/game_agent/checkLogin");
                    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

                    Boolean successed = hc.execute(httpPost, new ResponseHandler<Boolean>() {
                        @Override
                        public Boolean handleResponse(HttpResponse response)
                                throws ClientProtocolException, IOException {
                            String str = StreamUtils.copyToString(response.getEntity().getContent(),
                                    StandardCharsets.UTF_8);
                            return Boolean.parseBoolean(str);
                        }
                    });

                    if (!successed) {
                        continue;
                    }

                    Platform realPlatform = toRealPlatform(oauth.getLabel());

                    UserInfo info = new UserInfo();
                    info.setUsername(realPlatform.getLabel() + "-" + oauth.getUid());
                    info.setPlatform(realPlatform);
                    return info;
                } catch (IOException ex) {
                    throw new OAuth2Exception("lj login failed", ex);
                }
            }
        } finally {
            try {
                hc.close();
            } catch (IOException ex) {
            }
        }

        throw new OAuth2Exception("lj login failed");
    }

    @Override
    public Platform getPlatform() {
        return Platform.lj;
    }

    private Platform toRealPlatform(String label) {
        switch (label) {
            case "360":
                return Platform.qihoo;
            case "4399":
                return Platform.four399;
            case "dangle":
                return Platform.dangle;
            case "xiaomi":
                return Platform.xiaomi;
            case "yyb":
                return Platform.yyb;
            case "gfan":
                return Platform.gfan;
            case "anzhi":
                return Platform.anzhi;
            case "wandoujia":
                return Platform.wandoujia;
            case "baidumobilegame":
                return Platform.baidu;
            case "lenovoopenid":
                return Platform.lenovo;
            case "meizu":
                return Platform.meizu;
            case "vivo":
                return Platform.vivo;
            case "oppo":
                return Platform.oppo;
            case "zhuoyi":
                return Platform.zhuoyi;
            case "huawei":
                return Platform.huawei;
            case "muzhiwan":
                return Platform.muzhiwan;
            case "lj_test":
                return Platform.lj;
        }
        throw new IllegalArgumentException("No label: " + label);
    }

}
