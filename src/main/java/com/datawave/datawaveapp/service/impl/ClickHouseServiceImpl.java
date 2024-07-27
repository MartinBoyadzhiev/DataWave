package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.model.dto.MetricDataDTO;
import com.datawave.datawaveapp.model.entity.ColumnName;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.MetricMetadataRepository;
import com.datawave.datawaveapp.service.ClickHouseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClickHouseServiceImpl implements ClickHouseService {

    private final JdbcTemplate jdbcTemplate;
    private final MetricMetadataRepository metricMetadataRepository;

    public ClickHouseServiceImpl(@Qualifier("clickTemplate") JdbcTemplate jdbcTemplate, MetricMetadataRepository metricMetadataRepository, ModelMapper modelMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.metricMetadataRepository = metricMetadataRepository;
    }

    @Override
    public Set<MetricDataDTO> getMetricData(String metricName, Map<String, Object> filter) {
        Map<String, Object> sortedFilters = new TreeMap<>(filter);

        Optional<MetricMetadataEntity> optionalMetricMetadata = metricMetadataRepository.findByMetricName(metricName);
        if (optionalMetricMetadata.isEmpty()) {
            throw new IllegalArgumentException("Metric not found");
        }

        StringBuilder query = buildMetricDataPreparedStatement(metricName, sortedFilters);

        return executeMetricDataPreparedStatement(sortedFilters, query);
    }

    @Override
    public List<String> getColumns(String metricName) {

        MetricMetadataEntity metricMetadata = metricMetadataRepository.findByMetricName(metricName)
                .orElseThrow(() -> new IllegalArgumentException("Metric not found"));

        return metricMetadata.getColumnNames().stream().map(ColumnName::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> getFilteredValues(String metricName, String column, Map<String, Object> webFilter) {
        Map<String, Object> sortedFilters = new TreeMap<>(webFilter);
        validateMetricAndColumnNames(metricName, column, sortedFilters);
        StringBuilder query = buildColumnValuesPreparedStatement(metricName, column, sortedFilters);
        return executeColumnValuesPreparedStatement(sortedFilters, query);
    }

    private void validateMetricAndColumnNames(String metricName, String column, Map<String, Object> webFilter) {

        Optional<MetricMetadataEntity> optionalMetricMetadata = metricMetadataRepository.findByMetricName(metricName);

        if (optionalMetricMetadata.isEmpty()) {
            throw new IllegalArgumentException("Metric not found");
        }

        MetricMetadataEntity metricMetadata = optionalMetricMetadata.get();

        if (!metricMetadata.getColumnNames().contains(new ColumnName(column))) {
            throw new IllegalArgumentException("Column not found: " + column);
        }

        for (String filterColumn : webFilter.keySet()) {
            if (!metricMetadata.getColumnNames().contains(new ColumnName(filterColumn))) {
                throw new IllegalArgumentException("Column not found: " + filterColumn);
            }
        }

    }


    private StringBuilder buildMetricDataPreparedStatement(String metricName, Map<String, Object> sortedFilters) {
        StringBuilder query = new StringBuilder("SELECT * FROM ").append(metricName);
        if (!sortedFilters.isEmpty()) {
            String conditions = sortedFilters.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + " = ?")
                    .collect(Collectors.joining(" AND "));
            query.append(" WHERE ").append(conditions);
        }
        return query;
    }

    private StringBuilder buildColumnValuesPreparedStatement(String metricName, String column, Map<String, Object> sortedFilters) {

        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT ").append(column).append(" FROM ").append(metricName);

        if (!sortedFilters.isEmpty()) {
            String conditions = sortedFilters.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + " = ?")
                    .collect(Collectors.joining(" AND "));

            query.append(" WHERE ").append(conditions);
        }

        return query;
    }

    private Set<MetricDataDTO> executeMetricDataPreparedStatement(Map<String, Object> sortedFilters, StringBuilder query) {
        return new TreeSet<>(jdbcTemplate.query(query.toString(),
                ps -> {
                    int index = 1;
                    for (Map.Entry<String, Object> entry : sortedFilters.entrySet()) {
                        ps.setObject(index++, entry.getValue());
                    }
                },
                (rs, rowNum) -> new MetricDataDTO(
                        rs.getTimestamp("timestamp").toInstant(),
                        rs.getFloat("value")
                )
        )
        );
    }

    private List<String> executeColumnValuesPreparedStatement(Map<String, Object> sortedFilters, StringBuilder query) {
        return this.jdbcTemplate.query(query.toString(),
                ps -> {
                    int index = 1;
                    for (Map.Entry<String, Object> entry : sortedFilters.entrySet()) {
                        ps.setObject(index++, entry.getValue());
                    }
                },
                (rs, rowNum) -> rs.getString(1)
        );
    }
}
