package com.datawave.datawaveapp.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "metric_stats")
public class MetricStatsEntity extends BaseEntity {
    @Column
    private long count;
    @ManyToOne
    @JoinColumn(name = "metric_metadata_id", referencedColumnName = "id", nullable = false)
    private MetricMetadataEntity metricMetadataEntity;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity userEntity;

    public MetricStatsEntity() {
    }

    public MetricMetadataEntity getMetricMetadataEntity() {
        return metricMetadataEntity;
    }

    public void setMetricMetadataEntity(MetricMetadataEntity metricMetadataEntity) {
        this.metricMetadataEntity = metricMetadataEntity;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
