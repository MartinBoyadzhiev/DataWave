package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.model.dto.PriceMetricRecord;
import com.datawave.datawaveapp.model.entity.ColumnName;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.MetricMetadataRepository;
import com.datawave.datawaveapp.service.ClickHouseService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClickHouseServiceImpl implements ClickHouseService {

    private final JdbcTemplate jdbcTemplate;
    private final MetricMetadataRepository metricMetadataRepository;

    public ClickHouseServiceImpl(@Qualifier("clickTemplate") JdbcTemplate jdbcTemplate, MetricMetadataRepository metricMetadataRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.metricMetadataRepository = metricMetadataRepository;
    }

    @Override
    public List<PriceMetricRecord> getMetricData(String metricName) {

        return this.jdbcTemplate.query("SELECT * FROM ?",
                ps -> ps.setString(1, metricName),
                (rs, rowNum) -> new PriceMetricRecord(
                        rs.getTimestamp("timestamp").toInstant(),
                        rs.getFloat("value"),
                        rs.getString("asset"),
                        rs.getString("exchange")
                ));
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

        Optional<MetricMetadataEntity> optionalMetricMetadata = metricMetadataRepository.findByMetricName(metricName);
        if(optionalMetricMetadata.isEmpty()) {
            throw new IllegalArgumentException("Metric not found");
        }
        MetricMetadataEntity metricMetadata = optionalMetricMetadata.get();

        if(!metricMetadata.getColumnNames().contains(new ColumnName(column))) {
            throw new IllegalArgumentException("Column not found: " + column);
        }

        for (String filterColumn : webFilter.keySet()) {
            if(!metricMetadata.getColumnNames().contains(new ColumnName(filterColumn))) {
                throw new IllegalArgumentException("Column not found: " + filterColumn);
            }
        }

        StringBuilder preparedStatement = new StringBuilder();
        preparedStatement.append("SELECT DISTINCT "+ column +" FROM ?");
        if (!sortedFilters.isEmpty()) {
            String collected = sortedFilters.entrySet()
                    .stream()
                    .map(kv -> kv.getKey() + " = ?")
                    .collect(Collectors.joining(" and "));
            preparedStatement.append(" WHERE ").append(collected);
        }

        return this.jdbcTemplate.query(preparedStatement.toString(),
                ps -> {
                    int index = 1;
                    ps.setString(index++, metricName);
                    for (Map.Entry<String, Object> entry : sortedFilters.entrySet()) {
                        ps.setString(index++, entry.getValue().toString());
                    }
                },
                (rs, rowNum) -> rs.getString(1)
        );
    }
}
