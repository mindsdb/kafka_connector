package com.mindsdb.kafka.connect;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;
import static org.apache.kafka.common.config.ConfigDef.Importance;
import static org.apache.kafka.common.config.ConfigDef.Type;

public class MindsDBConnectorConfig extends AbstractConfig {
    private static final String MINDS_DB_URL = "mindsdb.url";
    private static final String MINDS_DB_USER = "mindsdb.user";
    private static final String MINDS_DB_PASSWORD = getDefaultPassword();
    private static final String KAFKA_HOST = "kafka.api.host";
    private static final String KAFKA_PORT = "kafka.api.port";
    private static final String KAFKA_AUTH_KEY = "kafka.api.key";
    private static final String KAFKA_AUTH_SECRET = "kafka.api.secret";
    private static final String API_NAME = "kafka.api.name";
    private static final String PREDICTOR_NAME = "predictor.name";
    private static final String PREDICTOR_TYPE = "predictor.type";
    private static final String TOPICS = "topics";
    private static final String FORECAST_TOPIC = "output.forecast.topic";
    private static final String ANOMALY_TOPIC = "output.anomaly.topic";

    private static String getDefaultPassword() {
        return "mindsdb.password";
    }

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(MINDS_DB_URL, Type.STRING, Importance.HIGH, "Root url for mindsdb's http interface")
            .define(MINDS_DB_USER, Type.STRING, null, Importance.LOW, "User for mindsdb (only required for cloud)")
            .define(MINDS_DB_PASSWORD, Type.STRING, null, Importance.LOW, "Password for mindsdb (only required for cloud)")
            .define(KAFKA_HOST, Type.STRING, Importance.HIGH, "The kafka_host on which kafka is running")
            .define(KAFKA_PORT, Type.STRING, Importance.HIGH, "The port on which kafka is running")
            .define(KAFKA_AUTH_KEY, Type.STRING, null, Importance.HIGH, "The key for kafka")
            .define(KAFKA_AUTH_SECRET, Type.STRING, null, Importance.HIGH, "The secret for kafka")
            .define(API_NAME, Type.STRING, Importance.HIGH, "Name of your kafka integration")
            .define(PREDICTOR_NAME, Type.STRING, Importance.HIGH, "Name of the predictor you want to integrate with")
            .define(PREDICTOR_TYPE, Type.STRING, "default", ConfigDef.ValidString.in("default", "timeseries"), Importance.HIGH, "Type of the predictor, either default or timeseries")
            .define(TOPICS, Type.STRING, Importance.HIGH, "Topic the predictor should listen to")
            .define(FORECAST_TOPIC, Type.STRING, Importance.HIGH, "Topic the predictor should put predictions in")
            .define(ANOMALY_TOPIC, Type.STRING, null, Importance.LOW, "Topic the predictor should put anomaly detection warnings in");

    public MindsDBConnectorConfig(Map<String, String> props) {
        super(CONFIG_DEF, props);
    }

    public String getMindsDbUrl() {
        return getString(MINDS_DB_URL);
    }

    public String getMindsDbUser() {
        return getString(MINDS_DB_USER);
    }

    public String getMindsDbPassword() {
        return getString(MINDS_DB_PASSWORD);
    }

    public String getKafkaHost(){
        return getString(KAFKA_HOST);
    }

    public String getKafkaPort() {
        return getString(KAFKA_PORT);
    }

    public String getKafkaAuthKey() {
        return getString(KAFKA_AUTH_KEY);
    }

    public String getKafkaAuthSecret() {
        return getString(KAFKA_AUTH_SECRET);
    }

    public String getApiName() {
        return getString(API_NAME);
    }

    public String getPredictorName() {
        return getString(PREDICTOR_NAME);
    }

    public String getPredictorType() {
        return getString(PREDICTOR_TYPE);
    }

    public String getTopics() {
        return getString(TOPICS);
    }

    public String getForecastTopic() {
        return getString(FORECAST_TOPIC);
    }

    public String getAnomalyTopic() {
        return getString(ANOMALY_TOPIC);
    }
}
