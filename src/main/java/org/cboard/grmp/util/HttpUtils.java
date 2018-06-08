package org.cboard.grmp.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by JunjieM on 2017-12-11.
 */
public class HttpUtils {

    private static int connectionRequestTimeout = 30000;

    private static int connectTimeout = 30000;

    private static int socketTimeout = 30000;

    private static int maxTotalpool = 200;

    private static int maxPerRoute = 20;

    private static PoolingHttpClientConnectionManager poolManager = null;

    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build());

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslConnectionSocketFactory).build();

            poolManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            poolManager.setMaxTotal(maxTotalpool);
            poolManager.setDefaultMaxPerRoute(maxPerRoute);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }


    public static CloseableHttpClient getConnection() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolManager).setDefaultRequestConfig(requestConfig).build();
        return httpClient;
    }

    public static CloseableHttpClient getConnection(int connectionRequestTimeout, int connectTimeout, int socketTimeout) {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolManager).setDefaultRequestConfig(requestConfig).build();
        return httpClient;
    }

}
