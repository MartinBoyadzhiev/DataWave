package com.datawave.datawaveapp.web;

import com.datawave.datawaveapp.model.dto.MetricOverviewDTO;
import com.datawave.datawaveapp.service.MetricMetadataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class OverviewController {

    private final MetricMetadataService metricMetadataService;

    public OverviewController(MetricMetadataService metricMetadataService) {
        this.metricMetadataService = metricMetadataService;
    }

    @GetMapping("/metric/overview")
    public Set<MetricOverviewDTO> getMetricOverview() {
        return this.metricMetadataService.getAllMetricOverview();
    }
}
