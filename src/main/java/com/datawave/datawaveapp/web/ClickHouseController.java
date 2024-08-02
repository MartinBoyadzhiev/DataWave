package com.datawave.datawaveapp.web;

import com.datawave.datawaveapp.model.dto.BasicResponseDTO;
import com.datawave.datawaveapp.model.dto.CreateTableDTO;
import com.datawave.datawaveapp.model.dto.MetricDataDTO;
import com.datawave.datawaveapp.model.dto.MetricOverviewDTO;
import com.datawave.datawaveapp.service.ClickHouseService;
import com.datawave.datawaveapp.service.MetricMetadataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class ClickHouseController {

    private final ClickHouseService clickHouseService;
    private final MetricMetadataService metricMetadataService;

    public ClickHouseController(ClickHouseService clickHouseService, MetricMetadataService metricMetadataService) {
        this.clickHouseService = clickHouseService;
        this.metricMetadataService = metricMetadataService;
    }

    @GetMapping("/metric/overview")
    public Set<MetricOverviewDTO> getMetricOverview() {
        return this.metricMetadataService.getAllMetricOverview();
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

    @PostMapping("/metric/create")
    public ResponseEntity<BasicResponseDTO> createTable(@Valid @RequestBody CreateTableDTO createTableData) {
        return this.clickHouseService.createTable(createTableData);
    }

    @DeleteMapping("/metric/delete")
    public ResponseEntity<BasicResponseDTO> deleteTable(@Valid
                                                        @Pattern (regexp = "^[a-zA-Z0-9]*$")
                                                        @RequestParam String metricName) {
        return this.clickHouseService.deleteTable(metricName);
    }
}
