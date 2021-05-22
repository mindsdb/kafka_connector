package com.mindsdb.kafka.connect.client;

import com.mindsdb.kafka.connect.MindsDBConnectorConfig;

import java.util.List;

public interface MindsDBClient {
    void createIntegration() throws MindsDBApiException;
    void createStream() throws MindsDBApiException;
    List<String> getPredictorColumns() throws MindsDBApiException;

    static MindsDBClient getInstance(MindsDBConnectorConfig config) {
        return new MindsDBClientImpl(config);
    }
}
