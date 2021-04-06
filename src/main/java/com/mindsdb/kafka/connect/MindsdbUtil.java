package com.mindsdb.kafka.connect;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MindsdbUtil {

    private static final String CONNECTOR_VERSION = "connector.version";

    private static Logger log = LoggerFactory.getLogger(MindsdbUtil.class);
    private static String propertiesFile = "/mindsdb-connect.properties";
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
