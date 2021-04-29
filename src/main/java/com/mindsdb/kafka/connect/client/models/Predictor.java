package com.mindsdb.kafka.connect.client.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Predictor {

    private String name;

    private String status;

    @JsonProperty("usable_input_columns")
    private List<String> inputColumns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getInputColumns() {
        return inputColumns;
    }

    public void setInputColumns(List<String> inputColumns) {
        this.inputColumns = inputColumns;
    }
}
