package com.mindsdb.kafka.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class MindsDBSinkTask extends SinkTask {
    private static final Logger LOG = LoggerFactory.getLogger(MindsDBSinkTask.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String version() {
        return ConnectorProperties.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        LOG.debug("Starting sink task...");
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        LOG.info("Received data from stream");
        try {
            LOG.info(objectMapper.writeValueAsString(collection));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to parse  json data", e);
        }
    }

    @Override
    public void stop() {
        LOG.debug("Stopping sink task...");
    }
}
