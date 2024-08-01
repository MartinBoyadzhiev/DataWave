package com.datawave.datawaveapp.model.dto;

import java.util.Objects;
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
