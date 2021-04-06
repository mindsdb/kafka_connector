package com.mindsdb.kafka.connect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;

import static com.mindsdb.kafka.connect.MindsdbConnectorConfig.CONFIG_DEF;

public class MindsdbConnector extends SourceConnector {

    private final Logger log = LoggerFactory.getLogger(MindsdbConnector.class);

    private Map<String, String> originalProps;
    private MindsdbConnectorConfig config;
    private SourceMonitorThread sourceMonitorThread;

    @Override
    public String version() {
        return MindsdbUtil.getConnectorVersion();
    }

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MindsdbTask.class;
    }

    @Override
    public Config validate(Map<String, String> connectorConfigs) {
        Config config = super.validate(connectorConfigs);
        return config;
    }

    @Override
    public void start(Map<String, String> originalProps) {
        this.originalProps = originalProps;
        config = new MindsdbConnectorConfig(originalProps);
        String firstParam = config.getString(FIRST_NONREQUIRED_PARAM_CONFIG);
        String secondParam = config.getString(SECOND_NONREQUIRED_PARAM_CONFIG);
        int monitorThreadTimeout = config.getInt(MONITOR_THREAD_TIMEOUT_CONFIG);
        sourceMonitorThread = new SourceMonitorThread(
            context, firstParam, secondParam, monitorThreadTimeout);
        sourceMonitorThread.start();
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        // The partitions below represent the source's part that
        // would likely to be broken down into tasks... such as
        // tables in a database.
        List<String> partitions = sourceMonitorThread.getCurrentSources();
        if (partitions.isEmpty()) {
            taskConfigs = Collections.emptyList();
            log.warn("No tasks created because there is zero to work on");
        } else {
            int numTasks = Math.min(partitions.size(), maxTasks);
            List<List<String>> partitionSources = ConnectorUtils.groupPartitions(partitions, numTasks);
            for (List<String> source : partitionSources) {
                Map<String, String> taskConfig = new HashMap<>(originalProps);
                taskConfig.put("sources", String.join(",", source));
                taskConfigs.add(taskConfig);
            }
        }
        return taskConfigs;
    }

    private void add_kafka_integration() {
        mindsdb_url = parsedConfig.getString("mindsdb_url");

        // Add kafka integrations
        kafka_host = parsedConfig.getString("kafka_host");
        port = parsedConfig.getInt("port");


        HttpRequest add_integration_request = HttpRequest.newBuilder()
                .POST(ofFormData(new HashMap<>(String,Object) {{
                    put("kafka_host", kafka_host);
                    put("kafka_port", kafka_port);
                    put("type", "kafka");
                    put("topic", "control_stream");
                    put("enabled", true);
                }}))
                .uri(URI.create(mindsdb_url + "/api/config/integrations/" + integration_name))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> add_integration_response = httpClient.send(add_integration_request, HttpResponse.BodyHandlers.ofString());
        /*
        // Tell mindsdb to read inputs from a topic and put predictions in another
        predictor_name = parsedConfig.getString("predictor_name");
        input_topic = parsedConfig.getString("input_topic");
        output_topic = parsedConfig.getString("output_topic");

        HttpRequest add_stream_request = HttpRequest.newBuilder()
                .POST(ofFormData(new HashMap<>(String,Object) {{
                    put("predictor_name", predictor_name);
                    put("input_topic", input_topic);
                    put("output_topic", output_topic);
                    put("integration_name", integration_name);
                }}))
                .uri(URI.create(mindsdb_url + "/api/streams/stream_http_api/" + integration_name))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> add_stream_response = httpClient.send(add_stream_request, HttpResponse.BodyHandlers.ofString());
        */
    }

    @Override
    public void stop() {
        sourceMonitorThread.shutdown();
    }

}
