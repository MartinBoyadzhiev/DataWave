package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.config.securityConfig.JwtProvider;
import com.datawave.datawaveapp.model.dto.CreateTableDTO;
import com.datawave.datawaveapp.model.dto.MetricDataDTO;
import com.datawave.datawaveapp.model.entity.*;
import com.datawave.datawaveapp.repository.ColumnMetadataRepository;
import com.datawave.datawaveapp.service.ClickHouseService;
import com.datawave.datawaveapp.service.MetricMetadataService;
import com.datawave.datawaveapp.service.UserEntityService;
import com.datawave.datawaveapp.service.exceptions.ColumnNotFoundException;
import com.datawave.datawaveapp.service.exceptions.IllegalColumnNameException;
import com.datawave.datawaveapp.service.exceptions.MetricAlreadyExistsException;
import com.datawave.datawaveapp.service.exceptions.MetricNotFoundException;
import com.datawave.datawaveapp.util.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final UserEntityService userService;
    private final JwtProvider jwtProvider;

    public ClickHouseServiceImpl(@Qualifier("clickTemplate") JdbcTemplate jdbcTemplate, ColumnMetadataRepository columnMetadataRepository, MetricMetadataService metricMetadataService, UserEntityService userService, JwtProvider jwtProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.columnMetadataRepository = columnMetadataRepository;
        this.metricMetadataService = metricMetadataService;
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Set<MetricDataDTO> getMetricData(String metricName, Map<String, Object> filter) {
        String mappedMetricName = "metric_" + metricName;
        Map<String, Object> sortedFilters = new TreeMap<>(filter);

        Optional<MetricMetadataEntity> optionalMetricMetadata = this.metricMetadataService.getByMetricName(metricName);
        if (optionalMetricMetadata.isEmpty()) {
            throw new MetricNotFoundException("Metric not found");
        }

        StringBuilder query = buildMetricDataPreparedStatement(mappedMetricName, sortedFilters);

        return executeMetricDataPreparedStatement(sortedFilters, query);
    }

    @Override
    public List<String> getColumns(String metricName) {

        Optional<MetricMetadataEntity> optionalMetricMetadata = this.metricMetadataService.getByMetricName(metricName);
        if (optionalMetricMetadata.isEmpty()) {
            throw new MetricNotFoundException("Metric not found");
        }

        MetricMetadataEntity metricMetadata = optionalMetricMetadata.get();

        return metricMetadata.getColumns().stream()
                .map(ColumnMetadataEntity::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getFilteredValues(String metricName, String column, Map<String, Object> webFilter) {
        String mappedMetricName = "metric_" + metricName;

        Map<String, Object> sortedFilters = new TreeMap<>(webFilter);
        validateMetricAndColumnNames(mappedMetricName, column, sortedFilters);
        StringBuilder query = buildColumnValuesPreparedStatement(mappedMetricName, column, sortedFilters);

        return executeColumnValuesPreparedStatement(sortedFilters, query);
    }

    @Override
    public void createTable(CreateTableDTO createTableData) {

        String metricName = createTableData.getMetricName();
        String mappedMetricName = "metric_" + metricName;

        ValueTypeEnum valueType = createTableData.getValueType();
        Map<String, ValueTypeEnum> columns = createTableData.getColumns();
        List<String> primaryKeys = createTableData.getPrimaryKeys();

        if (this.metricMetadataService.getByMetricName(metricName).isPresent()) {
            throw new MetricAlreadyExistsException("Metric with name \"" + metricName+  "\" already exists");
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

        metricMetadata.setColumns(columnMetadataEntitySet);

        ColumnMetadataEntity timestampMetadata = new ColumnMetadataEntity("timestamp");
        timestampMetadata.setType(ValueTypeEnum.TIMESTAMP);

        ColumnMetadataEntity valueMetadata = new ColumnMetadataEntity("value");
        valueMetadata.setType(valueType);

        metricMetadata.getColumns().add(timestampMetadata);
        metricMetadata.getColumns().add(valueMetadata);

        this.metricMetadataService.save(metricMetadata);
        this.columnMetadataRepository.saveAll(metricMetadata.getColumns());

        StringBuilder query = new StringBuilder(buildCreateTableQuery(mappedMetricName, valueType, columns, primaryKeys));

        this.jdbcTemplate.execute(query.toString());
    }

    @Override
    @Transactional
    public void deleteTable(String metricName) {
        String mappedMetricName = "metric_" + metricName;
        Optional<MetricMetadataEntity> optionalMetricMetadata = this.metricMetadataService.getByMetricName(metricName);

        if (optionalMetricMetadata.isEmpty()) {
            throw new MetricNotFoundException("Metric not found");
        }

        this.metricMetadataService.deleteByMetricName(metricName);

        this.jdbcTemplate.execute("DROP TABLE default." + mappedMetricName);

    }

    private String buildCreateTableQuery(String metricName, ValueTypeEnum valueType, Map<String, ValueTypeEnum> columns, List<String> primaryKeys) {

        columns.keySet().stream().filter(c -> !StringUtils.isAlphanumeric(c)).forEach(column -> {
            throw new IllegalColumnNameException("Column name must be alphanumeric");
        });

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE default.").append(metricName)
                .append(" (timestamp DateTime, value ").append(valueType.getValue())
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
            throw new MetricNotFoundException("Metric not found");
        }

        MetricMetadataEntity metricMetadata = optionalMetricMetadata.get();

        if (!metricMetadata.getColumns().contains(new ColumnMetadataEntity(column))) {
            throw new ColumnNotFoundException("Column not found: " + column);
        }

        for (String filterColumn : webFilter.keySet()) {
            if (!metricMetadata.getColumns().contains(new ColumnMetadataEntity(filterColumn))) {
                throw new ColumnNotFoundException("Column not found: " + filterColumn);
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
