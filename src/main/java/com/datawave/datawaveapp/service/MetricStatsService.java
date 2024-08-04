package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.StatisticDTO;

import java.util.Set;

public interface MetricStatsService {

    Set<StatisticDTO> getStatistics();

    void updateStatistics(String metricName, String token);
}
