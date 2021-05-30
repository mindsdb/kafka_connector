package com.mindsdb.kafka.connect.client;

import com.mindsdb.kafka.connect.MindsDBConnectorConfig;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.http.HttpClient;
import java.util.List;

public interface MindsDBClient {
    void createIntegration() throws MindsDBApiException;
    void createStream() throws MindsDBApiException;
    List<String> getPredictorColumns() throws MindsDBApiException;

    static MindsDBClient getInstance(MindsDBConnectorConfig config) {
        HttpClient.Builder clientBuilder = HttpClient.newBuilder();

        if (isValidString(config.getMindsDbUser()) && isValidString(config.getMindsDbPassword())) {
            clientBuilder.authenticator(
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    config.getMindsDbUser(),
                                    config.getMindsDbPassword().toCharArray()
                            );
                        }
                    }
            );
        }
        return new MindsDBClientImpl(clientBuilder.build(), new MindsDBClientConfig(config));
    }

    private static boolean isValidString(String s) {
        return s != null && !s.isBlank();
    }
}
