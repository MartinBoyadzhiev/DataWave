package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.MetricOverviewDTO;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;

import java.util.Optional;
import java.util.Set;

public interface MetricMetadataService {
    Optional<MetricMetadataEntity> getByMetricName(String metricName);

    void save(MetricMetadataEntity metricMetadataEntity);

    boolean deleteByMetricName(String metricName);

    Set<MetricOverviewDTO> getAllMetricOverview();
}
