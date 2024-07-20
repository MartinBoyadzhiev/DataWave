package com.datawave.datawaveapp.service;

import com.clickhouse.client.ClickHouseException;
import com.datawave.datawaveapp.model.dto.PriceMetricRecord;

import java.util.List;
import java.util.Map;

public interface ClickHouseService {

//    List<PriceMetricRecord> getMetric(String metricName) throws ClickHouseException;
//    List<String> getColumns(String metricName) throws ClickHouseException;
//
//    List<String> getDistinctValues(String metricName, String column, Map<String, Object> filter) throws ClickHouseException;
    List<PriceMetricRecord> getMetricByJdbc();

    List<PriceMetricRecord> getMetric(String metricName);

    List<String> getColumns(String metricName);

    List<String> getFilteredValues(String metricName, String column, Map<String, Object> filter);
}
