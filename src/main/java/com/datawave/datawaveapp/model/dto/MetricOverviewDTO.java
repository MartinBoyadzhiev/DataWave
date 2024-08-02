package com.datawave.datawaveapp.model.dto;

import com.datawave.datawaveapp.model.entity.ValueTypeEnum;

import java.util.Map;
import java.util.Objects;


public class MetricOverviewDTO {
    private String metricName;
    private Map<String, ValueTypeEnum> columns;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Map<String, ValueTypeEnum> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, ValueTypeEnum> columns) {
        this.columns = columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricOverviewDTO that = (MetricOverviewDTO) o;
        return Objects.equals(metricName, that.metricName) && Objects.equals(columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metricName, columns);
    }
}
