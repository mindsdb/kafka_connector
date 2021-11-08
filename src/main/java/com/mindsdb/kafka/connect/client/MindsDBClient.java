package com.mindsdb.kafka.connect.client;

import com.mindsdb.kafka.connect.MindsDBConnectorConfig;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.util.List;

public interface MindsDBClient {
    void createIntegration() throws MindsDBApiException;
    void createStream() throws MindsDBApiException;
    List<String> getPredictorColumns() throws MindsDBApiException;

    static MindsDBClient getInstance(MindsDBConnectorConfig config) {
        HttpClient.Builder clientBuilder = HttpClient.newBuilder();
        MindsDBClientConfig clientConfig = new MindsDBClientConfig(config);

        if (isValidString(config.getMindsDbUser()) && isValidString(config.getMindsDbPassword())) {
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            clientBuilder.cookieHandler(cookieManager);

            return new MindsDBCloudClientImpl(clientBuilder.build(), clientConfig);
        }

        return new MindsDBClientImpl(clientBuilder.build(), clientConfig);
    }

    private static boolean isValidString(String s) {
        return s != null && !s.isBlank();
    }
}
