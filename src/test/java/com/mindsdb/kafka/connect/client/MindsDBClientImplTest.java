package com.mindsdb.kafka.connect.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MindsDBClientImplTest {
    @SuppressWarnings("unchecked")
    private static final HttpResponse<Object> FAKE_OK_RESPONSE = mock(HttpResponse.class);

    @SuppressWarnings("unchecked")
    private static final HttpResponse<Object> FAKE_ERROR_RESPONSE = mock(HttpResponse.class);

    static {
        when(FAKE_OK_RESPONSE.statusCode()).thenReturn(200);
        when(FAKE_ERROR_RESPONSE.statusCode()).thenReturn(500);
    }

    private MindsDBClientConfig fakeConfig;

    private HttpClient fakeClient;

    private MindsDBClientImpl client;

    @BeforeEach
    void setup() {
        fakeConfig = mock(MindsDBClientConfig.class);
        fakeClient = mock(HttpClient.class);

        when(fakeConfig.baseUrl()).thenReturn("http://localhost:8080");
        when(fakeConfig.integrationCreationUri()).thenReturn("/test");
        when(fakeConfig.streamCreationUri()).thenReturn("/test");
        when(fakeConfig.predictorUri()).thenReturn("/test");

        client = new MindsDBClientImpl(fakeClient, fakeConfig);
    }

    @Test
    void createIntegrationOk() throws Exception {
        when(fakeClient.send(any(), any())).thenReturn(FAKE_OK_RESPONSE);

        assertDoesNotThrow(() -> client.createIntegration());
        verify(fakeConfig).integrationCreationUri();
        verify(fakeConfig).integrationCreationRequest();
    }

    @Test
    void createIntegrationErr() throws Exception {
        when(fakeClient.send(any(), any())).thenReturn(FAKE_ERROR_RESPONSE);

        assertThrows(MindsDBApiException.class, () -> client.createIntegration());

        verify(fakeConfig).integrationCreationUri();
        verify(fakeConfig).integrationCreationRequest();
    }

    @Test
    void createStreamOk() throws Exception {
        when(fakeClient.send(any(), any())).thenReturn(FAKE_OK_RESPONSE);

        assertDoesNotThrow(() -> client.createStream());

        verify(fakeConfig).streamCreationUri();
        verify(fakeConfig).streamCreationRequest();
    }

    @Test
    void createStreamError() throws Exception {
        when(fakeClient.send(any(), any())).thenReturn(FAKE_ERROR_RESPONSE);

        assertThrows(MindsDBApiException.class, () -> client.createStream());

        verify(fakeConfig).streamCreationUri();
        verify(fakeConfig).streamCreationRequest();
    }

    @Test
    void getPredictorWithColumns() throws Exception {
        @SuppressWarnings("unchecked")
        HttpResponse<Object> predictorResponse = mock(HttpResponse.class);
        when(predictorResponse.statusCode()).thenReturn(200);
        when(predictorResponse.body()).thenReturn("{\"usable_input_columns\": [\"fakeColumn\"]}");

        when(fakeClient.send(any(), any())).thenReturn(predictorResponse);

        List<String> result = client.getPredictorColumns();

        assertEquals(1, result.size());
        assertEquals("fakeColumn", result.get(0));
        verify(fakeConfig).predictorUri();
    }

    @Test
    void getPredictorWithoutColumns() throws Exception {
        @SuppressWarnings("unchecked")
        HttpResponse<Object> predictorResponse = mock(HttpResponse.class);
        when(predictorResponse.statusCode()).thenReturn(200);
        when(predictorResponse.body()).thenReturn("{}");

        when(fakeClient.send(any(), any())).thenReturn(predictorResponse);

        List<String> result = client.getPredictorColumns();

        assertTrue(result.isEmpty());
        verify(fakeConfig).predictorUri();
    }

    @Test
    void getPredictorColumnsError() throws Exception {
        when(fakeClient.send(any(), any())).thenReturn(FAKE_ERROR_RESPONSE);

        assertThrows(MindsDBApiException.class, () -> client.getPredictorColumns());
        verify(fakeConfig).predictorUri();
    }
}