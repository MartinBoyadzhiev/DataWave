package com.datawave.datawaveapp.web;

import com.datawave.datawaveapp.model.dto.MetricDataDTO;
import com.datawave.datawaveapp.service.ClickHouseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class ClickHouseController {

    private final ClickHouseService clickHouseService;

    public ClickHouseController(ClickHouseService clickHouseService) {
        this.clickHouseService = clickHouseService;
    }

    @GetMapping("/metric/{metricName}/data")
    public Set<MetricDataDTO> getMetricData(@PathVariable("metricName") String metricName,
                                            @RequestParam Map<String, Object> filter) {
        return this.clickHouseService.getMetricData(metricName, filter);
    }

    @GetMapping("/metric/{metricName}/columns")
    public List<String> getColumnNames(@PathVariable("metricName") String metricName) {
        return this.clickHouseService.getColumns(metricName);
    }

    @GetMapping("/metric/{metricName}/values/{column}")
    public List<String> getFilteredValues(@PathVariable("metricName") String metricName,
                                                        @PathVariable("column") String column,
                                                        @RequestParam Map<String, Object> filter) {
        return this.clickHouseService.getFilteredValues(metricName, column, filter);
    }
}
