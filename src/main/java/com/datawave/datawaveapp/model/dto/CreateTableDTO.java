package com.datawave.datawaveapp.model.dto;

import java.util.List;
import java.util.Map;

public class CreateTableDTO {

    private String metricName;
//    TODO: Change valueType to enum
    private String valueType;
    private Map<String, String> columns;
    private List<String> primaryKeys;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }
}
