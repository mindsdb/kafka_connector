package com.mindsdb.kafka.connect.client;

import com.mindsdb.kafka.connect.MindsDBConnectorConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MindsDBClientConfig {
    private final MindsDBConnectorConfig config;

    public MindsDBClientConfig(MindsDBConnectorConfig config) {
        this.config = config;
    }

    public String baseUrl() {
        return config.getMindsDbUrl();
    }

    public String predictorUri() {
        return "/api/predictors/" + config.getPredictorName();
    }

    public String integrationCreationUri() {
        return "/api/config/integrations/" + config.getApiName();
    }

    public Map<String, Object> integrationCreationRequest() {
        HashMap<String, Object> parameters = new HashMap<>();
        HashMap<String, Object> connection = new HashMap<>();

        connection.put("security_protocol", config.getSecurityProtocol());
        connection.put("sasl_mechanism", config.getSaslMechanism());
        connection.put("sasl_plain_username", config.getUsername());
        connection.put("sasl_plain_password", config.getPassword());
        connection.put("bootstrap_servers", config.getKafkaHost() + ":" + config.getKafkaPort());

        parameters.put("connection", connection);
        parameters.put("type", "kafka");
        parameters.put("enabled", true);

        return Collections.singletonMap("params", parameters);
    }

    public String streamCreationUri() {
        return "/api/streams/" + config.getTopics() + "_" + config.getPredictorName() + "_" + config.getForecastTopic();
    }

    public Map<String, Object> streamCreationRequest() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("predictor", config.getPredictorName());
        parameters.put("stream_in", config.getTopics());
        parameters.put("stream_out", config.getForecastTopic());
        parameters.put("stream_anomaly", config.getAnomalyTopic());
        parameters.put("integration_name", config.getApiName());
        parameters.put("type", config.getPredictorType());

        return Collections.singletonMap("params", parameters);
    }
}
