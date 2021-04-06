package com.mindsdb.kafka.connect;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SampleSourceConnectorTest {

    @Test
    public void connectorVersionShouldMatch() {
        String version = MindsdbUtil.getConnectorVersion();
        assertEquals(version, new MindsdbConnector().version());
    }

    @Test
    public void checkClassTask() {
        Class<? extends Task> taskClass = new MindsdbConnector().taskClass();
        assertEquals(SampleSourceTask.class, taskClass);
    }

    @Test
    public void checkMissingRequiredParams() {
        assertThrows(ConnectException.class, () -> {
            Map<String, String> props = new HashMap<>();
            new MindsdbConnector().validate(props);
        });
    }

}
