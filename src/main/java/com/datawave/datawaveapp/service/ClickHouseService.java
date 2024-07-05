package com.datawave.datawaveapp.service;

import com.clickhouse.client.ClickHouseException;
import com.datawave.datawaveapp.model.dto.PriceMetricRecord;

import java.util.List;

public interface ClickHouseService {

    List<PriceMetricRecord> getMetric(String metricName) throws ClickHouseException;

}
