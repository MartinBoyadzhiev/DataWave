package com.datawave.datawaveapp.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "metric_stats")
public class MetricStatsEntity extends BaseEntity {
    @Column
    private long count;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "metric_metadata_id", referencedColumnName = "id", nullable = false)
    private MetricMetadataEntity metricMetadataEntity;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity userEntity;

    public MetricStatsEntity() {
    }


    public void setMetricMetadataEntity(MetricMetadataEntity metricMetadataEntity) {
        this.metricMetadataEntity = metricMetadataEntity;
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
