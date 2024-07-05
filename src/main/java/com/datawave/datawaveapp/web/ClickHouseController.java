package com.datawave.datawaveapp.web;

import com.clickhouse.client.ClickHouseException;
import com.datawave.datawaveapp.model.dto.PriceMetricRecord;
import com.datawave.datawaveapp.service.ClickHouseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClickHouseController {

    private final ClickHouseService clickHouseService;

    public ClickHouseController(ClickHouseService clickHouseService) {
        this.clickHouseService = clickHouseService;
    }

    @GetMapping("/metric/{metricName}/data")
    public List<PriceMetricRecord> getMetric(@PathVariable("metricName") String metricName) throws ClickHouseException {
        return clickHouseService.getMetric(metricName);
    }
}
