package com.datawave.datawaveapp.web;

import com.datawave.datawaveapp.model.dto.StatisticDTO;
import com.datawave.datawaveapp.service.MetricStatsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class StatisticsController {

    private final MetricStatsService metricStatsService;

    public StatisticsController(MetricStatsService metricStatsService) {
        this.metricStatsService = metricStatsService;
    }

    @GetMapping("/metric/statistics")
    public Set<StatisticDTO> getStatistics() {
        return this.metricStatsService.getStatistics();
    }

    @PutMapping("/metric/statistics")
    public void updateStatistics(@Valid @Pattern(regexp = "^[a-zA-Z0-9]*$") @RequestParam String metricName,
                                 @RequestHeader("Authorization") String token) {
        System.out.println("Received token: " + token);
        this.metricStatsService.updateStatistics(metricName, token);
    }
}
