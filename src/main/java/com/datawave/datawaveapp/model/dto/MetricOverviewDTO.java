package com.datawave.datawaveapp.model.dto;

import java.util.Set;

public class MetricOverviewDTO {
    private String metricName;
    private Set<String> columns;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }
}
