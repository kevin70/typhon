
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.ProductPurchase;
import java.io.File;
import java.io.FileInputStream;
import java.security.PrivateKey;

/**
 * Google Play 支付校验示例.
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
public class GooglePlaySample {

    public static void main(String[] args) throws Exception {

        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

        PrivateKey privateKey = SecurityUtils.loadPrivateKeyFromKeyStore(
                SecurityUtils.getPkcs12KeyStore(),
                new FileInputStream(new File("{P12 key file}")), // 生成的P12文件
                "notasecret", "privatekey", "notasecret");

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(transport).setJsonFactory(JacksonFactory.getDefaultInstance())
                .setServiceAccountId("{Email address}") // e.g.: 626891557797-frclnjv31rn4ss81ch746g9t6pd3mmej@developer.gserviceaccount.com
                .setServiceAccountScopes(AndroidPublisherScopes.all())
                .setServiceAccountPrivateKey(privateKey).build();

        AndroidPublisher publisher = new AndroidPublisher.Builder(transport,
                JacksonFactory.getDefaultInstance(), credential).build();

        AndroidPublisher.Purchases.Products products = publisher.purchases().products();

        // 参数详细说明: https://developers.google.com/android-publisher/api-ref/purchases/products/get
        AndroidPublisher.Purchases.Products.Get product = products.get("{packageName}",
                "{productId}", "{token}");

        // 获取订单信息
        // 返回信息说明: https://developers.google.com/android-publisher/api-ref/purchases/products
        // 通过consumptionState, purchaseState可以判断订单的状态
        ProductPurchase purchase = product.execute();
    }
}
