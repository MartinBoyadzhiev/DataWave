package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.model.dto.BasicResponseDTO;
import com.datawave.datawaveapp.model.dto.CreateTableDTO;
import com.datawave.datawaveapp.model.dto.MetricDataDTO;
import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.ColumnMetadataRepository;
import com.datawave.datawaveapp.repository.mysqlRepositories.MetricMetadataRepository;
import com.datawave.datawaveapp.service.ClickHouseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClickHouseServiceImpl implements ClickHouseService {

    private final JdbcTemplate jdbcTemplate;
    private final MetricMetadataRepository metricMetadataRepository;
    private final ColumnMetadataRepository columnMetadataRepository;

    public ClickHouseServiceImpl(@Qualifier("clickTemplate") JdbcTemplate jdbcTemplate, MetricMetadataRepository metricMetadataRepository, ColumnMetadataRepository columnMetadataRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.metricMetadataRepository = metricMetadataRepository;
        this.columnMetadataRepository = columnMetadataRepository;
    }

    @Override
    public Set<MetricDataDTO> getMetricData(String metricName, Map<String, Object> filter) {
        Map<String, Object> sortedFilters = new TreeMap<>(filter);

        Optional<MetricMetadataEntity> optionalMetricMetadata = this.metricMetadataRepository.findByMetricName(metricName);
        if (optionalMetricMetadata.isEmpty()) {
            throw new IllegalArgumentException("Metric not found");
        }

        StringBuilder query = buildMetricDataPreparedStatement(metricName, sortedFilters);

        return executeMetricDataPreparedStatement(sortedFilters, query);
    }

    @Override
    public List<String> getColumns(String metricName) {

        MetricMetadataEntity metricMetadata = this.metricMetadataRepository.findByMetricName(metricName)
                .orElseThrow(() -> new IllegalArgumentException("Metric not found"));

        return metricMetadata.getColumnNames().stream().map(ColumnMetadataEntity::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> getFilteredValues(String metricName, String column, Map<String, Object> webFilter) {
        Map<String, Object> sortedFilters = new TreeMap<>(webFilter);
        validateMetricAndColumnNames(metricName, column, sortedFilters);
        StringBuilder query = buildColumnValuesPreparedStatement(metricName, column, sortedFilters);
        return executeColumnValuesPreparedStatement(sortedFilters, query);
    }

    @Override
    public ResponseEntity<BasicResponseDTO> createTable(CreateTableDTO createTableData) {

        String metricName = createTableData.getMetricName();
        String valueType = createTableData.getValueType();
        Map<String, String> columns = createTableData.getColumns();
        List<String> primaryKeys = createTableData.getPrimaryKeys();

        if (this.metricMetadataRepository.findByMetricName(metricName).isPresent()) {
            return new ResponseEntity<>(new BasicResponseDTO("Table already exists", false),
                    HttpStatus.CONFLICT);
        }

        MetricMetadataEntity metricMetadata = new MetricMetadataEntity();
        metricMetadata.setMetricName(metricName);
        metricMetadata.setColumnNames(columns.keySet().stream().map(k -> {
            ColumnMetadataEntity columnName = new ColumnMetadataEntity();
            columnName.setName(k);
            return columnName;
        }).collect(Collectors.toSet()));

        ColumnMetadataEntity timestampMetadata = new ColumnMetadataEntity("timestamp");
        timestampMetadata.setType("DateTime");
        metricMetadata.getColumnNames().add(timestampMetadata);
        ColumnMetadataEntity valueMetadata = new ColumnMetadataEntity("value");
        valueMetadata.setType(valueType);
        metricMetadata.getColumnNames().add(valueMetadata);

        this.columnMetadataRepository.saveAll(metricMetadata.getColumnNames());

//      FIXME: SQL Injection vulnerability
        StringBuilder query = new StringBuilder(buildCreateTableQuery(metricName, valueType, columns, primaryKeys));

        this.jdbcTemplate.execute(query.toString());

        this.metricMetadataRepository.save(metricMetadata);

        return new ResponseEntity<>(new BasicResponseDTO("Table created successfully", true), HttpStatus.CREATED);
    }

    private String buildCreateTableQuery(String metricName, String valueType, Map<String, String> columns, List<String> primaryKeys) {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE default.").append(metricName)
                .append(" (timestamp DateTime, value ").append(valueType)
                .append(", ");
        String fields = columns.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .collect(Collectors.joining(", "));
        String keys = primaryKeys.stream().collect(Collectors.joining(", "));
        query.append(fields).append(") ENGINE = MergeTree() PRIMARY KEY (").append(keys).append(");");
        return query.toString();
    }

    private void validateMetricAndColumnNames(String metricName, String column, Map<String, Object> webFilter) {

        Optional<MetricMetadataEntity> optionalMetricMetadata = this.metricMetadataRepository.findByMetricName(metricName);

        if (optionalMetricMetadata.isEmpty()) {
            throw new IllegalArgumentException("Metric not found");
        }

        MetricMetadataEntity metricMetadata = optionalMetricMetadata.get();

        if (!metricMetadata.getColumnNames().contains(new ColumnMetadataEntity(column))) {
            throw new IllegalArgumentException("Column not found: " + column);
        }

        for (String filterColumn : webFilter.keySet()) {
            if (!metricMetadata.getColumnNames().contains(new ColumnMetadataEntity(filterColumn))) {
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
        return new TreeSet<>(this.jdbcTemplate.query(query.toString(),
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
