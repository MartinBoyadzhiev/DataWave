package com.datawave.datawaveapp.repository;

import com.datawave.datawaveapp.model.entity.MetricStatsEntity;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetricStatsRepository {

    private com.datawave.datawaveapp.repository.mysqlRepositories.MetricStatsRepository metricStatsRepository = mock(com.datawave.datawaveapp.repository.mysqlRepositories.MetricStatsRepository.class);

    @Test
    void findByMetricMetadataEntityIdAndUserEntityId_ReturnsMetricStatsEntity_WhenIdsExist() {
        MetricStatsEntity metricStats = new MetricStatsEntity();
        when(metricStatsRepository.findByMetricMetadataEntityIdAndUserEntityId(1L, 1L)).thenReturn(Optional.of(metricStats));
        Optional<MetricStatsEntity> result = metricStatsRepository.findByMetricMetadataEntityIdAndUserEntityId(1L, 1L);
        assertTrue(result.isPresent());
        assertEquals(metricStats, result.get());
    }

    @Test
    void findByMetricMetadataEntityIdAndUserEntityId_ReturnsEmptyOptional_WhenIdsDoNotExist() {
        when(metricStatsRepository.findByMetricMetadataEntityIdAndUserEntityId(999L, 999L)).thenReturn(Optional.empty());
        Optional<MetricStatsEntity> result = metricStatsRepository.findByMetricMetadataEntityIdAndUserEntityId(999L, 999L);
        assertFalse(result.isPresent());
    }

    @Test
    void findByMetricMetadataEntityIdAndUserEntityId_ReturnsEmptyOptional_WhenOneIdDoesNotExist() {
        when(metricStatsRepository.findByMetricMetadataEntityIdAndUserEntityId(1L, 999L)).thenReturn(Optional.empty());
        Optional<MetricStatsEntity> result = metricStatsRepository.findByMetricMetadataEntityIdAndUserEntityId(1L, 999L);
        assertFalse(result.isPresent());
    }
}
