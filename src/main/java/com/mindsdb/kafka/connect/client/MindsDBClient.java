package com.mindsdb.kafka.connect.client;

import com.mindsdb.kafka.connect.MindsDBSinkConnectorConfig;

import java.util.List;

public interface MindsDBClient {
    void createIntegration() throws MindsDBApiException;
    void createStream() throws MindsDBApiException;
    List<String> getPredictorColumns() throws MindsDBApiException;

    static MindsDBClient getInstance(MindsDBSinkConnectorConfig config) {
        return new MindsDBClientImpl(config);
    }
}
