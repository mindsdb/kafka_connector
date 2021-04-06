package com.mindsdb.kafka.connect;

import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static com.mindsdb.kafka.connect.MindsdbTask.*;

public class MindsdbTaskTest {

    @Test
    public void taskVersionShouldMatch() {
        String version = MindsdbUtil.getConnectorVersion();
        assertEquals(version, new MindsdbTask().version());
    }

    @Test
    public void checkNumberOfRecords() {

    }
}
