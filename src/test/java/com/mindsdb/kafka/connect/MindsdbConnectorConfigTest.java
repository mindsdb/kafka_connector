package com.mindsdb.kafka.connect;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MindsdbConnectorConfigTest {

    @Test
    public void basicParamsAreMandatory() {
        assertThrows(ConfigException.class, () -> {
            Map<String,Object> props = new HashMap<>();
            props.put("kafka.api.port", 5000);
            props.put("kafka.api.host", "unlocalhost");
            new MindsdbConnectorConfig(props);
        });
    }

    public void checkingNonRequiredDefaults() {
        Map<String, String> props = new HashMap<>();
        MindsdbConnectorConfig config = new MindsdbConnectorConfig(props);
        assertEquals(5000, config.getInt("kafka.api.port"));
        assertEquals("unlocalhost", config.getString("kafka.api.host"));
    }

}
