package com.mindsdb.kafka.connect;

import com.mindsdb.kafka.connect.client.MindsDBApiException;
import com.mindsdb.kafka.connect.client.MindsDBClient;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.ErrantRecordReporter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MindsDBSinkTask extends SinkTask {
    private static final Logger LOG = LoggerFactory.getLogger(MindsDBSinkTask.class);

    private ErrantRecordReporter reporter;
    private MindsDBSinkConnectorConfig config;
    private List<String> expectedProperties = Collections.emptyList();

    @Override
    public String version() {
        return ConnectorProperties.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        LOG.debug("Starting sink task...");
        reporter = null;

        try {
            if (context.errantRecordReporter() == null) {
                LOG.info("Errant record reporter not configured.");
            }

            reporter = context.errantRecordReporter();

            this.config = new MindsDBSinkConnectorConfig(map);
            initExpectedProperties();
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
            LOG.warn("Kafka versions prior to 2.6 do not support the errant record reporter.");
        }
    }

    @Override
    public void put(Collection<SinkRecord> collection) {
        initExpectedProperties();

        collection.forEach(record -> {
            if (!validSchema(record.valueSchema())) {
                String errorMessage = "Topic contains data with invalid schema. Expected fields -> [" +
                        String.join(", ", expectedProperties) + "]";
                reportError(record, new ConnectException(errorMessage));
            }
        });
    }

    @Override
    public void stop() {
        LOG.debug("Stopping sink task...");
    }

    private void reportError(SinkRecord record, Throwable t) {
        if (reporter != null) {
            reporter.report(record, t);
        }
    }

    private boolean validSchema(Schema schema) {
        if (expectedProperties.isEmpty())
            return false;

        if (schema != null && schema.fields() != null && !schema.fields().isEmpty()) {
            return schema.fields()
                    .stream()
                    .map(Field::name)
                    .allMatch(expectedProperties::contains);
        }

        return false;
    }

    private void initExpectedProperties() {
        if (this.expectedProperties.isEmpty()) {
            MindsDBClient mindsDBClient = MindsDBClient.getInstance(config);
            try {
                this.expectedProperties = mindsDBClient.getPredictorColumns();
            } catch (MindsDBApiException e) {
                LOG.error("Error fetching data from MindsDB API", e);
            }
        }
    }
}
