package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.model.dto.MetricOverviewDTO;
import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.MetricMetadataRepository;
import com.datawave.datawaveapp.service.MetricMetadataService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MetricMetadataServiceImpl implements MetricMetadataService {
    private final MetricMetadataRepository metricMetadataRepository;
    private final ModelMapper modelMapper;

    public MetricMetadataServiceImpl(MetricMetadataRepository metricMetadataRepository, ModelMapper modelMapper) {
        this.metricMetadataRepository = metricMetadataRepository;
        this.modelMapper = modelMapper;
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

    @Override
    public Set<MetricOverviewDTO> getAllMetricOverview() {

        List<MetricMetadataEntity> allMetrics = this.metricMetadataRepository.findAll();

        Set<MetricOverviewDTO> result = allMetrics.stream()
                .map(m -> {
                    MetricOverviewDTO metricOverviewDTO = new MetricOverviewDTO();
                    metricOverviewDTO.setMetricName(m.getMetricName());
                    metricOverviewDTO.setColumns(m.getColumns()
                            .stream()
                            .map(ColumnMetadataEntity::getName)
                            .collect(Collectors.toSet()));
                    return metricOverviewDTO;
                })
                .collect(Collectors.toSet());

        return result;
    }
}