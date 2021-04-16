package com.mindsdb.kafka.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MindsDBSinkConnector extends SinkConnector {
    private static final Logger LOG = LoggerFactory.getLogger(MindsDBSinkConnector.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String AUTH_USER = "mindsdb.user";
    private static final String AUTH_PASSWORD = "mindsdb.password";

    private MindsDBSinkConnectorConfig configProperties;

    @Override
    public void start(Map<String, String> map) {
        try {
            configProperties = new MindsDBSinkConnectorConfig(map);
            initMindsDBIntegration();
        } catch (ConfigException e) {
            throw new ConnectException("Couldn't start MindsDBSinkConnector due to configuration errors", e);
        }
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MindsDBSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        Map<String, String> taskProps = new HashMap<>(configProperties.originalsStrings());
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }

    @Override
    public void stop() {
        LOG.info("Stopping mindsdb sink connector...");
    }

    @Override
    public ConfigDef config() {
        return MindsDBSinkConnectorConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return ConnectorProperties.getVersion();
    }

    private void initMindsDBIntegration() {
        HttpClient.Builder clientBuilder = HttpClient.newBuilder();

        if (configProperties.getString(AUTH_USER) != null && configProperties.getString(AUTH_PASSWORD) != null) {
            clientBuilder.authenticator(
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    configProperties.getString(AUTH_USER),
                                    configProperties.getString(AUTH_PASSWORD).toCharArray()
                            );
                        }
                    }
            );
        }

        HttpClient httpClient = clientBuilder.build();
        postToMindsDb(
                httpClient,
                "/api/config/integrations/" + configProperties.getString("kafka.api.name"),
                getKafkaIntegrationBody()
        );

        postToMindsDb(
                httpClient,
                "/api/streams/" + configProperties.getString("topics") + "_" +
                        configProperties.getString("predictor.name") + "_" + configProperties.getString("output.forecast.topic"),
                getKafkaStreamData()
        );
    }

    private void postToMindsDb(HttpClient httpClient, String endpoint, Map<String, Object> request) {
        try {
            String jsonRequest = OBJECT_MAPPER.writeValueAsString(request);
            URI uri = new URI(configProperties.getString("mindsdb.url") + endpoint);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.of(5, ChronoUnit.SECONDS))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() > 300) {
                LOG.error("Failed to post info to mindsdb: url -> " + uri + ", body -> " + jsonRequest +
                        "\nResponse: statusCode -> " + response.statusCode() + ", body ->" + response.body());
            }
        } catch (Exception e) {
            LOG.error("Failed to post to mindsdb", e);
        }
    }

    private Map<String, Object> getKafkaIntegrationBody() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("kafka_host", configProperties.getString("kafka.api.host"));
        parameters.put("kafka_port", configProperties.getString("kafka.api.port"));
        parameters.put("kafka_key", configProperties.getString("kafka.api.key"));
        parameters.put("kafka_secret", configProperties.getString("kafka.api.secret"));
        parameters.put("type", "kafka");
        parameters.put("topic", null);
        parameters.put("enabled", true);

        return Collections.singletonMap("params", parameters);
    }

    private Map<String, Object> getKafkaStreamData() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("predictor_name", configProperties.getString("predictor.name"));
        parameters.put("input_topic", configProperties.getString("topics"));
        parameters.put("output_topic", configProperties.getString("output.forecast.topic"));

        return parameters;
    }
}
