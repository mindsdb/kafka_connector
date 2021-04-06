package com.mindsdb.kafka.connect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

public class MindsdbTask extends SourceTask {

    private static Logger log = LoggerFactory.getLogger(MindsdbTask.class);

    private MindsdbConnectorConfig config;
    private List<String> sources;

    @Override
    public String version() {
        return MindsdbUtil.getConnectorVersion();
    }

    @Override
    public void start(Map<String, String> properties) {
        config = new MindsdbConnectorConfig(properties);
        String sourcesStr = properties.get("sources");
        sources = Arrays.asList(sourcesStr.split(","));
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        Thread.sleep(2);
        List<SourceRecord> records = new ArrayList<>();
        for (String source : sources) {
            log.info("Polling data from the source '" + source + "'");
            records.add(new SourceRecord(
                Collections.singletonMap("source", source),
                Collections.singletonMap("offset", 0),
                source, null, null, null, Schema.BYTES_SCHEMA,
                String.format("Data from %s", source).getBytes()));
        }
        return records;
    }

    @Override
    public void stop() {
    }

}
