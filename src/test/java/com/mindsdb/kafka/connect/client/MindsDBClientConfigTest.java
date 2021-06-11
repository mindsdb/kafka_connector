package com.mindsdb.kafka.connect.client;

import com.mindsdb.kafka.connect.MindsDBConnectorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MindsDBClientConfigTest {
    private MindsDBConnectorConfig connectorConfig;

    private MindsDBClientConfig clientConfig;

    @BeforeEach
    void setup() {
        connectorConfig = mock(MindsDBConnectorConfig.class);
        clientConfig = new MindsDBClientConfig(connectorConfig);
    }

    @Test
    void predictorUri() {
        when(connectorConfig.getPredictorName()).thenReturn("testPredictor");
        assertEquals("/api/predictors/testPredictor", clientConfig.predictorUri());
    }

    @Test
    void integrationCreationUri() {
        when(connectorConfig.getApiName()).thenReturn("testApi");
        assertEquals("/api/config/integrations/testApi", clientConfig.integrationCreationUri());
    }

    @Test
    void integrationCreationRequest() {
        when(connectorConfig.getKafkaHost()).thenReturn("fakeKafkaHost");
        when(connectorConfig.getKafkaPort()).thenReturn("0000");

        Map<String, Object> expectedParams = Map.of(
                "connection", Collections.singletonMap("bootstrap_servers", "fakeKafkaHost:0000"),
                "type", "kafka",
                "enabled", true
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) clientConfig.integrationCreationRequest()
                .get("params");

        assertEquals(expectedParams, result);
    }

    @Test
    void streamCreationUri() {
        when(connectorConfig.getTopics()).thenReturn("testTopic");
        when(connectorConfig.getPredictorName()).thenReturn("testPredictor");
        when(connectorConfig.getForecastTopic()).thenReturn("exitTopic");

        assertEquals("/api/streams/testTopic_testPredictor_exitTopic", clientConfig.streamCreationUri());
    }

    @Test
    void streamCreationRequest() {
        when(connectorConfig.getPredictorName()).thenReturn("testPredictor");
        when(connectorConfig.getTopics()).thenReturn("testTopic");
        when(connectorConfig.getForecastTopic()).thenReturn("exitTopic");
        when(connectorConfig.getAnomalyTopic()).thenReturn("anomalyTopic");
        when(connectorConfig.getApiName()).thenReturn("testApi");
        when(connectorConfig.getPredictorType()).thenReturn("testType");


        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) clientConfig.streamCreationRequest()
                .get("params");

        assertEquals(
                Map.of(
                        "predictor", "testPredictor",
                        "stream_in", "testTopic",
                        "stream_out", "exitTopic",
                        "stream_anomaly", "anomalyTopic",
                        "integration_name", "testApi",
                        "type", "testType"
                ),
                result
        );
    }
}