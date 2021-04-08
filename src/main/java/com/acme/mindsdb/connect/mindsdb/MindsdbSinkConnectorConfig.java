package com.acme.mindsdb.connect.mindsdb;

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

public class MindsdbSinkConnectorConfig extends AbstractConfig {

    public MindsdbSinkConnectorConfig(final Map<?, ?> originalProps) {
        super(CONFIG_DEF, originalProps);
    }

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define("mindsdb.url", Type.STRING, null, Importance.HIGH, "Root url for mindsdb's http interface")
            .define("mindsdb.user", Type.STRING, null, Importance.HIGH, "User for mindsdb (only required for cloud)")
            .define("mindsdb.password", Type.STRING, null, Importance.HIGH, "Password for mindsdb (only required for cloud)")
            .define("kafka.api.host", Type.STRING, null, Importance.HIGH, "The kafka_host on which kafka is running")
            .define("kafka.api.port", Type.STRING, null, Importance.HIGH, "The port on which kafka is running")
            .define("kafka.api.key", Type.STRING, null, Importance.HIGH, "The key for kafka")
            .define("kafka.api.secret", Type.STRING, null, Importance.HIGH, "The secret for kafka")
            .define("kafka.api.name", Type.STRING, null, Importance.HIGH, "Name of your kafka integration")
            .define("predictor.name", Type.STRING, null, Importance.HIGH, "Name of the predictor you want to integrate with")
            .define("topics", Type.STRING, null, Importance.HIGH, "Topic the predictor should listen to")
            .define("output.forecast.topic", Type.STRING, null, Importance.HIGH, "Topic the predictor should put predictions in")
            .define("output.anomaly.topic", Type.STRING, null, Importance.HIGH, "Topic the predictor should put anomaly detection warnings in");

}
