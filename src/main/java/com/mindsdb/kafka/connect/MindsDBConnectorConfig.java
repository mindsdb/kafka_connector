package com.mindsdb.kafka.connect;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Arrays;
import java.util.Map;
import static org.apache.kafka.common.config.ConfigDef.Importance;
import static org.apache.kafka.common.config.ConfigDef.Type;

public class MindsDBConnectorConfig extends AbstractConfig {
    private static final String MINDS_DB_URL = "mindsdb.url";
    private static final String MINDS_DB_USER = "mindsdb.user";
    private static final String MINDS_DB_PASSWORD = getMindsDBPasswordFieldName();
    private static final String KAFKA_HOST = "kafka.api.host";
    private static final String KAFKA_PORT = "kafka.api.port";
    private static final String KAFKA_AUTH_SECRET = "kafka.api.secret";
    private static final String API_NAME = "kafka.api.name";
    private static final String GROUP_ID = "kafka.group.id";
    private static final String PREDICTOR_NAME = "predictor.name";
    private static final String PREDICTOR_TYPE = "predictor.type";
    private static final String TOPICS = "topics";
    private static final String FORECAST_TOPIC = "output.forecast.topic";
    private static final String ANOMALY_TOPIC = "output.anomaly.topic";
    private static final String SECURITY_PROTOCOL = "security.protocol";
    private static final String SASL_MECHANISM = "sasl.mechanism";
    private static final String SASL_PLAIN_USERNAME = "sasl.plain.username";
    private static final String SASL_PLAIN_PASSWORD = getKafkaUserPasswordFieldName();


    private static String getMindsDBPasswordFieldName() {
        return "mindsdb.password";

    }

    private static String getKafkaUserPasswordFieldName() {
        return "sasl.plain.password";

    }

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(MINDS_DB_URL, Type.STRING, Importance.HIGH, "Root url for mindsdb's http interface")
            .define(MINDS_DB_USER, Type.STRING, null, Importance.LOW, "User for mindsdb (only required for cloud)")
            .define(MINDS_DB_PASSWORD, Type.STRING, null, Importance.LOW, "Password for mindsdb (only required for cloud)")
            .define(KAFKA_HOST, Type.STRING, Importance.HIGH, "The kafka_host on which kafka is running")
            .define(KAFKA_PORT, Type.STRING, Importance.HIGH, "The port on which kafka is running")
            .define(API_NAME, Type.STRING, Importance.HIGH, "Name of your kafka integration")
            .define(GROUP_ID, Type.STRING, null, Importance.HIGH, "The name of the consumer group to join for dynamic partition assignment (if enabled), and to use for fetching and committing offsets.")
            .define(PREDICTOR_NAME, Type.STRING, Importance.HIGH, "Name of the predictor you want to integrate with")
            .define(PREDICTOR_TYPE, Type.STRING, "default", ConfigDef.ValidString.in("default", "timeseries"), Importance.HIGH, "Type of the predictor, either default or timeseries")
            .define(TOPICS, Type.STRING, Importance.HIGH, "Topic the predictor should listen to")
            .define(FORECAST_TOPIC, Type.STRING, Importance.HIGH, "Topic the predictor should put predictions in")
            .define(ANOMALY_TOPIC, Type.STRING, null, Importance.LOW, "Topic the predictor should put anomaly detection warnings in")
            .define(SECURITY_PROTOCOL, Type.STRING, "PLAINTEXT", Importance.LOW, "Protocol used to communicate with brokers. Valid values are: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL. Default: PLAINTEXT.")
            .define(SASL_MECHANISM, Type.STRING, null, Importance.LOW, "Authentication mechanism when security_protocol is configured for SASL_PLAINTEXT or SASL_SSL. Valid values are: PLAIN, GSSAPI, OAUTHBEARER, SCRAM-SHA-256, SCRAM-SHA-512.")
            .define(KAFKA_AUTH_SECRET, Type.STRING, null, Importance.HIGH, "OAuthBearer token provider instance.")
            .define(SASL_PLAIN_USERNAME, Type.STRING, null, Importance.LOW, "username for sasl PLAIN and SCRAM authentication. Required if sasl_mechanism is PLAIN or one of the SCRAM mechanisms.")
            .define(SASL_PLAIN_PASSWORD, Type.STRING, null, Importance.LOW, "password for sasl PLAIN and SCRAM authentication. Required if sasl_mechanism is PLAIN or one of the SCRAM mechanisms.");

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

    public String getKafkaAuthSecret() {
        return getString(KAFKA_AUTH_SECRET);
    }

    public String getApiName() {
        return getString(API_NAME);
    }

    public String getGroupID() {
        return getString(GROUP_ID);
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

    public String getSecurityProtocol() {
        return getString(SECURITY_PROTOCOL);
    }

    public String getSaslMechanism() {
        return getString(SASL_MECHANISM);
    }

    public String getUsername() {
        return getString(SASL_PLAIN_USERNAME);
    }

    public String getPassword() {
        return getString(SASL_PLAIN_PASSWORD);
    }
}
