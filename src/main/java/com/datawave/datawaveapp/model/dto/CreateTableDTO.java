package com.datawave.datawaveapp.model.dto;

import com.datawave.datawaveapp.model.entity.ValueTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Map;

public class CreateTableDTO {
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Metric name must be alphanumeric.")
    private String metricName;
//    TODO: Change valueType to enum
    private ValueTypeEnum valueType;
    private Map<String, ValueTypeEnum> columns;
    private List<String> primaryKeys;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public ValueTypeEnum getValueType() {
        return valueType;
    }

    public void setValueType(ValueTypeEnum valueType) {
        this.valueType = valueType;
    }

    public Map<String, ValueTypeEnum> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, ValueTypeEnum> columns) {
        this.columns = columns;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }
}
