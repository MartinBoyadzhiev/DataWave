package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.MetricMetadataRepository;
import com.datawave.datawaveapp.service.MetricMetadataService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MetricMetadataServiceImpl implements MetricMetadataService {
    private final MetricMetadataRepository metricMetadataRepository;

    public MetricMetadataServiceImpl(MetricMetadataRepository metricMetadataRepository) {
        this.metricMetadataRepository = metricMetadataRepository;
    }

    @Override
    public Optional<MetricMetadataEntity> getByMetricName(String metricName) {
        return this.metricMetadataRepository.findByMetricName(metricName);
    }

    @Override
    public void save(MetricMetadataEntity metricMetadataEntity) {
        this.metricMetadataRepository.save(metricMetadataEntity);
    }

    @Override
    public boolean deleteByMetricName(String metricName) {
        return this.metricMetadataRepository.deleteByMetricName(metricName) > 0;
    }
}
