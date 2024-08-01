package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.model.dto.BasicResponseDTO;
import com.datawave.datawaveapp.model.dto.CreateTableDTO;
import com.datawave.datawaveapp.model.dto.MetricDataDTO;
import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.ColumnMetadataRepository;
import com.datawave.datawaveapp.repository.mysqlRepositories.MetricMetadataRepository;
import com.datawave.datawaveapp.service.ClickHouseService;
import com.datawave.datawaveapp.service.MetricMetadataService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClickHouseServiceImpl implements ClickHouseService {
    private final JdbcTemplate jdbcTemplate;
    private final ColumnMetadataRepository columnMetadataRepository;
    private final MetricMetadataService metricMetadataService;

    public ClickHouseServiceImpl(@Qualifier("clickTemplate") JdbcTemplate jdbcTemplate, ColumnMetadataRepository columnMetadataRepository, MetricMetadataService metricMetadataService) {
        this.jdbcTemplate = jdbcTemplate;
        this.columnMetadataRepository = columnMetadataRepository;
        this.metricMetadataService = metricMetadataService;
    }

    @Override
    public Set<MetricDataDTO> getMetricData(String metricName, Map<String, Object> filter) {
        Map<String, Object> sortedFilters = new TreeMap<>(filter);

        Optional<MetricMetadataEntity> optionalMetricMetadata = this.metricMetadataService.getByMetricName(metricName);
        if (optionalMetricMetadata.isEmpty()) {
            throw new IllegalArgumentException("Metric not found");
        }

        StringBuilder query = buildMetricDataPreparedStatement(metricName, sortedFilters);

        return executeMetricDataPreparedStatement(sortedFilters, query);
    }

    @Override
    public List<String> getColumns(String metricName) {

        MetricMetadataEntity metricMetadata = this.metricMetadataService.getByMetricName(metricName)
                .orElseThrow(() -> new IllegalArgumentException("Metric not found"));

        return metricMetadata.getColumns().stream()
                .map(ColumnMetadataEntity::getName)
                .collect(Collectors.toList());
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

        if (this.metricMetadataService.getByMetricName(metricName).isPresent()) {
            return new ResponseEntity<>(new BasicResponseDTO("Table already exists", false),
                    HttpStatus.CONFLICT);
        }

        MetricMetadataEntity metricMetadata = new MetricMetadataEntity();
        metricMetadata.setMetricName(metricName);

        Set<ColumnMetadataEntity> columnMetadataEntitySet = new HashSet<>();
        columns.forEach((k, v) -> {
            ColumnMetadataEntity columnName = new ColumnMetadataEntity();
            columnName.setName(k);
            columnName.setType(v);
            columnMetadataEntitySet.add(columnName);
        });
        metricMetadata.setColumnNames(columnMetadataEntitySet);

        ColumnMetadataEntity timestampMetadata = new ColumnMetadataEntity("timestamp");
        timestampMetadata.setType("DateTime");

        ColumnMetadataEntity valueMetadata = new ColumnMetadataEntity("value");
        valueMetadata.setType(valueType);

        metricMetadata.getColumns().add(timestampMetadata);
        metricMetadata.getColumns().add(valueMetadata);

        this.metricMetadataService.save(metricMetadata);
        this.columnMetadataRepository.saveAll(metricMetadata.getColumns());

        StringBuilder query = new StringBuilder(buildCreateTableQuery(metricName, valueType, columns, primaryKeys));

        this.jdbcTemplate.execute(query.toString());

        return new ResponseEntity<>(new BasicResponseDTO("Table created successfully", true), HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public ResponseEntity<BasicResponseDTO> deleteTable(String metricName) {
        Optional<MetricMetadataEntity> optionalMetricMetadata = this.metricMetadataService.getByMetricName(metricName);
        if (optionalMetricMetadata.isEmpty()) {
            return new ResponseEntity<>(new BasicResponseDTO("Table not found", false),
                    HttpStatus.NOT_FOUND);
        };

        this.metricMetadataService.deleteByMetricName(metricName);

        this.jdbcTemplate.execute("DROP TABLE default." + metricName);

        return new ResponseEntity<>(new BasicResponseDTO("Table deleted successfully", true), HttpStatus.OK);
    }

    private String buildCreateTableQuery(String metricName, String valueType, Map<String, String> columns, List<String> primaryKeys) {
//        FIXME: This is a SQL injection vulnerability
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE default.").append(metricName)
                .append(" (timestamp DateTime, value ").append(valueType)
                .append(", ");
        String fields = columns.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue())
                .collect(Collectors.joining(", "));
        String keys = primaryKeys.stream().collect(Collectors.joining(", "));
        query.append(fields).append(") ENGINE = MergeTree() PRIMARY KEY (timestamp, ").append(keys).append(");");
        return query.toString();
    }

    private void validateMetricAndColumnNames(String metricName, String column, Map<String, Object> webFilter) {

        Optional<MetricMetadataEntity> optionalMetricMetadata = this.metricMetadataService.getByMetricName(metricName);

        if (optionalMetricMetadata.isEmpty()) {
            throw new IllegalArgumentException("Metric not found");
        }

        MetricMetadataEntity metricMetadata = optionalMetricMetadata.get();

        if (!metricMetadata.getColumns().contains(new ColumnMetadataEntity(column))) {
            throw new IllegalArgumentException("Column not found: " + column);
        }

        for (String filterColumn : webFilter.keySet()) {
            if (!metricMetadata.getColumns().contains(new ColumnMetadataEntity(filterColumn))) {
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
