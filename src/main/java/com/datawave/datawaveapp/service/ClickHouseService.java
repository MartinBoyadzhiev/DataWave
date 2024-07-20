package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.PriceMetricRecord;
import java.util.List;
import java.util.Map;

public interface ClickHouseService {

    List<PriceMetricRecord> getMetricData(String metricName);

    List<String> getColumns(String metricName);

    List<String> getFilteredValues(String metricName, String column, Map<String, Object> filter);
}
