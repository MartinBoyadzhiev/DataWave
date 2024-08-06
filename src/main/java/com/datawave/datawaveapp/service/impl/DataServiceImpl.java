package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.service.exceptions.IllegalCSVDataFormatException;
import com.datawave.datawaveapp.service.exceptions.MetricNotFoundException;
import com.datawave.datawaveapp.model.dto.InsertDataDTO;
import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.model.entity.ValueTypeEnum;
import com.datawave.datawaveapp.service.DataService;
import com.datawave.datawaveapp.service.MetricMetadataService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataServiceImpl implements DataService {

    private final JdbcTemplate jdbcTemplate;
    private final MetricMetadataService metricMetadataService;

    public DataServiceImpl(JdbcTemplate jdbcTemplate, MetricMetadataService metricMetadataService) {
        this.jdbcTemplate = jdbcTemplate;
        this.metricMetadataService = metricMetadataService;
    }

    @Override
    public void insertData(InsertDataDTO insertDataDTO) {

        try {
            String metricName = insertDataDTO.getMetricName();
            String clikhouseMetricName = "metric_" + metricName;
            String metricData = insertDataDTO.getCsvData();

            Optional<MetricMetadataEntity> optionalMetricMetadataEntity = this.metricMetadataService.getByMetricName(metricName);

            if (optionalMetricMetadataEntity.isEmpty()) {
                throw new MetricNotFoundException("Metric not found");
            }

            MetricMetadataEntity metricMetadata = optionalMetricMetadataEntity.get();

            StringReader reader = new StringReader(metricData);
            String headerLine = metricData.substring(0, metricData.indexOf("\n"));
            String[] headersArr = headerLine.split(",");

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(headersArr)
                    .setIgnoreHeaderCase(false)
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);

            Set<ColumnMetadataEntity> columnTypes = metricMetadata.getColumns();

            List<String> headersList = Arrays.asList(headersArr);
            validateHeaders(columnTypes, headersList);

            StringBuilder query = buildInsertDataPreparedStatementQuery(clikhouseMetricName, headersList);

            Map<String, ValueTypeEnum> columnTypesMap = columnTypes.stream()
                    .collect(Collectors.toMap(ColumnMetadataEntity::getName, ColumnMetadataEntity::getType));

            for (CSVRecord record : records) {

                jdbcTemplate.update(query.toString(), stmt -> {
                    int index = 1;
                    for (String headerName : headersList) {
                        ValueTypeEnum valueTypeEnum = columnTypesMap.get(headerName);
                        String recordColumnData = record.get(headerName);
                        if (recordColumnData == null) {
                            throw new IllegalCSVDataFormatException("CSV contains null data\nPlease check the instructions and try again");
                        }
                        switch (valueTypeEnum) {
                            case STRING:
                                stmt.setString(index, recordColumnData);
                                break;
                            case INTEGER:
                                stmt.setInt(index, Integer.parseInt(recordColumnData));
                                break;
                            case FLOAT:
                                stmt.setDouble(index, Double.parseDouble(recordColumnData));
                                break;
                            case TIMESTAMP:
                                stmt.setTimestamp(index, Timestamp.from(Instant.parse(recordColumnData)));
                                break;
                        }
                        index++;
                    }
                });
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error inserting data\nPlease check the instructions and try again");
        }
    }

    StringBuilder buildInsertDataPreparedStatementQuery(String clikhouseMetricName, List<String> headersList) {
        StringBuilder query = new StringBuilder("INSERT INTO " + clikhouseMetricName + " (");


        query.append(headersList.stream().collect(Collectors.joining(", ")));
        query.append(") VALUES (");
        query.append(headersList.stream().map(column -> "?").collect(Collectors.joining(", ")));
        query.append(")");
        return query;
    }

    void validateHeaders(Set<ColumnMetadataEntity> columnTypes, List<String> header) {
        Set<String> headers = new HashSet<>(header);
        Set<String> columnNames = columnTypes.stream().map(ColumnMetadataEntity::getName).collect(Collectors.toSet());
        if (!columnNames.containsAll(headers)) {
            throw new IllegalCSVDataFormatException("Headers do not match column names\nPlease check the instructions and try again");
        }
    }

}
