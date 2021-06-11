package com.mindsdb.kafka.connect;

import com.mindsdb.kafka.connect.client.MindsDBClient;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MindsDBConnector extends SinkConnector {
    private static final Logger LOG = LoggerFactory.getLogger(MindsDBConnector.class);

    private MindsDBConnectorConfig configProperties;

    @Override
    public void start(Map<String, String> map) {
        try {
            configProperties = new MindsDBConnectorConfig(map);
            MindsDBClient mindsDBClient = MindsDBClient.getInstance(configProperties);
            mindsDBClient.createIntegration();
            mindsDBClient.createStream();
        } catch (Exception e) {
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
        return MindsDBConnectorConfig.CONFIG_DEF;
    }

    @Override
    public String version() {
        return ConnectorProperties.getVersion();
    }
}
