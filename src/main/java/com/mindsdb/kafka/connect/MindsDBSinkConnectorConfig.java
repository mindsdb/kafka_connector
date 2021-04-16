package com.mindsdb.kafka.connect;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;
import static org.apache.kafka.common.config.ConfigDef.Importance;
import static org.apache.kafka.common.config.ConfigDef.Type;

public class MindsDBSinkConnectorConfig extends AbstractConfig {
    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define("mindsdb.url", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "Root url for mindsdb's http interface")
            .define("mindsdb.user", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.MEDIUM, "User for mindsdb (only required for cloud)")
            .define("mindsdb.password", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.MEDIUM, "Password for mindsdb (only required for cloud)")
            .define("kafka.api.host", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "The kafka_host on which kafka is running")
            .define("kafka.api.port", Type.INT, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "The port on which kafka is running")
            .define("kafka.api.key", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "The key for kafka")
            .define("kafka.api.secret", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "The secret for kafka")
            .define("kafka.api.name", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "Name of your kafka integration")
            .define("predictor.name", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "Name of the predictor you want to integrate with")
            .define("topics", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "Topic the predictor should listen to")
            .define("output.forecast.topic", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "Topic the predictor should put predictions in")
            .define("output.anomaly.topic", Type.STRING, ConfigDef.NO_DEFAULT_VALUE, Importance.HIGH, "Topic the predictor should put anomaly detection warnings in");

    public MindsDBSinkConnectorConfig(Map<String, String> props) {
        super(CONFIG_DEF, props);
    }
}
