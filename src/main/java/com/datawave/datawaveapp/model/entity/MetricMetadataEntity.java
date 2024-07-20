package com.datawave.datawaveapp.model.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "metric_metadata")
public class MetricMetadataEntity extends BaseEntity {
    @Column(name = "metric_name", nullable = false, unique = true)
    private String metricName;

    @JoinColumn(name = "metric_id", referencedColumnName = "id", nullable = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ColumnName> columnNames;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Set<ColumnName> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(Set<ColumnName> columnNames) {
        this.columnNames = columnNames;
    }
}
