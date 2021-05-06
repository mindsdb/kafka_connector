package com.mindsdb.kafka.connect.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindsdb.kafka.connect.MindsDBSinkConnectorConfig;
import com.mindsdb.kafka.connect.client.models.Predictor;
import org.apache.kafka.connect.errors.ConnectException;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MindsDBClientImpl implements MindsDBClient {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final MindsDBSinkConnectorConfig config;
    private final HttpClient httpClient;

    MindsDBClientImpl(MindsDBSinkConnectorConfig config) {
        this.config = config;
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

        this.httpClient = clientBuilder.build();
    }

    @Override
    public void createIntegration() throws MindsDBApiException {
        HashMap<String, Object> parameters = new HashMap<>();
        HashMap<String, Object> connection = new HashMap<>();
        connection.put("bootstrap_servers", config.getKafkaHost() + ":" + config.getKafkaPort());

        // Need also add checks for 'sasl_mechanism' and 'security_protocol' because
        // next two params depend on them
        connection.put("sasl_plain_username", config.getKafkaAuthKey());
        connection.put("sasl_plain_password", config.getKafkaAuthSecret());
        parameters.put("connection", connection);
        parameters.put("type", "kafka");
        parameters.put("enabled", true);

        postToMindsDb(
                "/api/config/integrations/" + config.getApiName(),
                Collections.singletonMap("params", parameters)
        );
    }

    @Override
    public void createStream() throws MindsDBApiException {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("predictor", config.getPredictorName());
        parameters.put("stream_in", config.getTopics());
        parameters.put("stream_out", config.getForecastTopic());
        parameters.put("stream_anomaly", config.getAnomalyTopic());
        parameters.put("integration_name", config.getApiName());

        postToMindsDb(
                "/api/streams/" + config.getTopics() + "_" +
                        config.getPredictorName() + "_" + config.getForecastTopic(),
                Collections.singletonMap("params", parameters)
        );
    }

    @Override
    public List<String> getPredictorColumns() throws MindsDBApiException {
        return Optional
                .ofNullable(
                        getFromMindsDB("/api/predictors/" + config.getPredictorName(), Predictor.class)
                )
                .map(Predictor::getInputColumns)
                .orElse(Collections.emptyList());
    }

    private <T> T getFromMindsDB(String endpoint, Class<T> clazz) throws MindsDBApiException {
        try {
            URI uri = new URI(config.getMindsDbUrl() + endpoint);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.of(5, ChronoUnit.SECONDS))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new MindsDBApiException(httpRequest, response);
            }

            return OBJECT_MAPPER.readValue(response.body(), clazz);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new ConnectException("Failed to send request to MindsDB server", e);
        }
    }

    private void postToMindsDb(String endpoint, Map<String, Object> request) throws MindsDBApiException {
        try {
            String jsonRequest = OBJECT_MAPPER.writeValueAsString(request);
            URI uri = new URI(config.getMindsDbUrl() + endpoint);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.of(5, ChronoUnit.SECONDS))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() > 300) {
                throw new MindsDBApiException(httpRequest, response);
            }
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new ConnectException("Failed to send request to MindsDB server", e);
        }
    }

    private static boolean isValidString(String s) {
        return s != null && !s.isBlank();
    }
}
