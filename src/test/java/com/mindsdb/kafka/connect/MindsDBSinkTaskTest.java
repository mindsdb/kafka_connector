package com.mindsdb.kafka.connect;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;

class MindsDBSinkTaskTest {

    private static final Map<String, String> FAKE_VALID_CONFIG = Map.of(
            "mindsdb.url", "fakeUrl",
            "kafka.api.host", "fakeKafkaHost",
            "kafka.api.port", "0000",
            "kafka.api.name", "fakeApi",
            "predictor.name", "fakePredictor",
            "topics", "fakeTopic",
            "output.forecast.topic", "fakeOutTopic"
    );

    private MindsDBSinkTask task;

    @BeforeEach
    void setup() {
        task = spy(MindsDBSinkTask.class);

        doNothing().when(task).initExpectedProperties();
    }

    @Test
    void start() {
        task.start(FAKE_VALID_CONFIG);
        verify(task).initExpectedProperties();
    }

    @Test
    void putValidTask() {
        when(task.validSchema(any())).thenReturn(true);

        task.put(Collections.singletonList(new FakeRecord()));
        verify(task, never()).reportError(any(), any());
    }

    @Test
    void putInvalidTask() {
        when(task.validSchema(any())).thenReturn(false);

        task.put(Collections.singletonList(new FakeRecord()));
        verify(task).reportError(any(), any());
    }

    private static class FakeRecord extends SinkRecord {
        public FakeRecord() {
            super("topic", 1, Schema.STRING_SCHEMA, "key", Schema.STRING_SCHEMA, "value", 0);
        }
    }
}