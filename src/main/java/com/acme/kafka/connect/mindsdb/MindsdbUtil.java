package com.acme.kafka.connect.mindsdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public final class MindsdbUtil {

    private static final String CONNECTOR_VERSION = "connector.version";

    private static Logger log = LoggerFactory.getLogger(MindsdbUtil.class);
    private static String propertiesFile = "/mindsdb-kafka-connect.properties";
    private static Properties properties;

    static {
        try (InputStream stream = MindsdbUtil.class.getResourceAsStream(propertiesFile)) {
            properties = new Properties();
            properties.load(stream);
        } catch (Exception ex) {
            log.warn("Error while loading properties: ", ex);
        }
    }

    public static String getConnectorVersion() {
        return properties.getProperty(CONNECTOR_VERSION);
    }

    private MindsdbUtil() {
    }
}

