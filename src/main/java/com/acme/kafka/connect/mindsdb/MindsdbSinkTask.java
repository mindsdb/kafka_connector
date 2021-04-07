package com.acme.kafka.connect.mindsdb;

import java.util.Collection;
import java.util.Map;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MindsdbSinkTask extends SinkTask {

    private static Logger log = LoggerFactory.getLogger(MindsdbSinkTask.class);

    @Override
    public String version() {
        return MindsdbUtil.getConnectorVersion();
    }

    @Override
    public void start(Map<String, String> properties) {
    }

    @Override
    public void put(Collection<SinkRecord> sinkRecords) {
        for (SinkRecord record : sinkRecords) {
            log.debug("Got new record, ignoring");
        }
    }

    @Override
    public void stop() {
    }

}

