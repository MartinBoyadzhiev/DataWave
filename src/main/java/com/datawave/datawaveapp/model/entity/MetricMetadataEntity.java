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
    private Set<ColumnMetadataEntity> columns;

    @OneToMany(mappedBy = "metricMetadataEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<MetricStatsEntity> metricStats;

    public void setColumns(Set<ColumnMetadataEntity> columns) {
        this.columns = columns;
    }

    public Set<ColumnMetadataEntity> getColumns() {
        return columns;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }
}
