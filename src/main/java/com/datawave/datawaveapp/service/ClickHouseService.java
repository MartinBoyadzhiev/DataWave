package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.CreateTableDTO;
import com.datawave.datawaveapp.model.dto.MetricDataDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ClickHouseService {

    Set<MetricDataDTO> getMetricData(String metricName, Map<String, Object> filter);

    List<String> getColumns(String metricName);

    List<String> getFilteredValues(String metricName, String column, Map<String, Object> filter);

    void createTable(CreateTableDTO createTableData);

    void deleteTable(String metricName);
}
