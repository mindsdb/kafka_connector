package com.acme.mindsdb.connect.mindsdb;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;

import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;

public class MindsdbSinkConnector extends SinkConnector {

    private final Logger log = LoggerFactory.getLogger(MindsdbSinkConnector.class);

    private MindsdbSinkConnectorConfig config;

    @Override
    public String version() {
        return MindsdbUtil.getConnectorVersion();
    }

    @Override
    public ConfigDef config() {
        return MindsdbSinkConnectorConfig.CONFIG_DEF;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MindsdbSinkTask.class;
    }

    @Override
    public Config validate(Map<String, String> connectorConfigs) {
        Config config = super.validate(connectorConfigs);
        return config;
    }

    @Override
    public void start(Map<String, String> props) {
        config = new MindsdbSinkConnectorConfig(props);
        add_kafka_integration();
        add_kafka_stream();
    }


    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        ArrayList<Map<String, String>> configs = new ArrayList<>();
        for (int i = 0; i < maxTasks; i++) {
            Map<String, String> config = new HashMap<>();
            configs.add(config);
        }
        return configs;
    }

    // Implementation based on hieroglyphs from a pre stone-age cave wall: https://www.baeldung.com/java-http-request
    private void mindsdb_post_with_params(HashMap<String, Object> parameters, String endpoint) throws Exception {
        URL url = new URL(config.getString("mindsdb.url") + endpoint);
        log.error("\nSending request to: " + config.getString("mindsdb.url") + endpoint + "\n");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setConnectTimeout(10);

        JSONObject payload = new JSONObject(parameters)
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.write(payload.toString())
        out.flush();
        out.close();

        int status = con.getResponseCode();
        con.disconnect();
    }

    private void add_kafka_integration() {
        HashMap<String, Object> parameters = new HashMap<>();
        HashMap<String, Object> payload = new HashMap<>();
        parameters.put("kafka_host", config.getString("kafka.api.host"));
        parameters.put("kafka_port", config.getString("kafka.api.port"));
        parameters.put("kafka_key", config.getString("kafka.api.key"));
        parameters.put("kafka_secret", config.getString("kafka.api.secret"));
        parameters.put("type", "kafka");
        parameters.put("topic", null);
        parameters.put("enabled", true);
        payload.put("params", parameters)
        try {
            mindsdb_post_with_params(payload, "/api/config/integrations/" + config.getString("kafka.api.name"));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    private void add_kafka_stream() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("predictor_name", config.getString("predictor.name"));
        parameters.put("input_topic", config.getString("topics"));
        parameters.put("output_topic", config.getString("output.forecast.topic"));
        try {
            mindsdb_post_with_params(parameters, "/api/streams/" + config.getString("topics") + "_" + config.getString("predictor.name") + "_" + config.getString("output.forecast.topic"));
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    @Override
    public void stop() {

    }

}
