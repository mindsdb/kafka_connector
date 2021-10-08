package com.mindsdb.kafka.connect.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindsdb.kafka.connect.client.models.Predictor;
import org.apache.kafka.connect.errors.ConnectException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MindsDBCloudClientImpl implements MindsDBClient {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String CONNECTION_ERROR = "Failed to send request to MindsDB server";

    private final HttpClient httpClient;
    private final MindsDBClientConfig clientConfig;

    public MindsDBCloudClientImpl(HttpClient httpClient, MindsDBClientConfig clientConfig) {
        this.httpClient = httpClient;
        this.clientConfig = clientConfig;

        try {
            postToMindsDb(
                    "/cloud/login",
                    this.clientConfig.cloudLoginBody()
            );
        } catch (Exception e) {
            throw new ConnectException("Failed to authenticate to cloud with the provided user/password", e);
        }
    }

    @Override
    public void createIntegration() { }

    @Override
    public void createStream() throws MindsDBApiException {
        postToMindsDb(
                clientConfig.streamCreationUri(),
                clientConfig.cloudStreamRequest()
        );
    }

    @Override
    public List<String> getPredictorColumns() throws MindsDBApiException {
        Predictor predictor = getFromMindsDB(clientConfig.predictorUri(), Predictor.class);
        return Optional
                .ofNullable(predictor)
                .map(Predictor::getInputColumns)
                .orElse(Collections.emptyList());
    }

    private <T> T getFromMindsDB(String endpoint, Class<T> clazz) throws MindsDBApiException {
        try {
            URI uri = new URI(clientConfig.baseUrl() + endpoint);

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
        } catch (IOException | URISyntaxException e) {
            throw new ConnectException(CONNECTION_ERROR, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectException(CONNECTION_ERROR, e);
        }
    }

    private void postToMindsDb(String endpoint, Map<String, Object> request) throws MindsDBApiException {
        try {
            String jsonRequest = OBJECT_MAPPER.writeValueAsString(request);
            URI uri = new URI(clientConfig.baseUrl() + endpoint);

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
        } catch (IOException | URISyntaxException e) {
            throw new ConnectException(CONNECTION_ERROR, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectException(CONNECTION_ERROR, e);
        }
    }
}
