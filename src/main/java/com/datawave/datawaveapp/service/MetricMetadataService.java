package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;

import java.util.Optional;

public interface MetricMetadataService {
    Optional<MetricMetadataEntity> getByMetricName(String metricName);

    void save(MetricMetadataEntity metricMetadataEntity);

    boolean deleteByMetricName(String metricName);
}
