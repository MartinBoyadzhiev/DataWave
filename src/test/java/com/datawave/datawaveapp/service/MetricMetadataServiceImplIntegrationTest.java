package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.MetricOverviewDTO;
import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.model.entity.ValueTypeEnum;
import com.datawave.datawaveapp.repository.MetricMetadataRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MetricMetadataServiceImplIntegrationTest {

    @Autowired
    private MetricMetadataRepository metricMetadataRepository;

    @Autowired
    private MetricMetadataService metricMetadataService;


    @Test
    @Transactional
    @Rollback(true)
    void deleteByMetricName_ReturnsTrue_WhenMetricIsDeleted() {
        MetricMetadataEntity mtd = createMetricMetadata();
        boolean result = metricMetadataService.deleteByMetricName(mtd.getMetricName());
        assertTrue(result);
    }

    @Test
    @Transactional
    @Rollback(true)
    void deleteByMetricName_ReturnsFalse_WhenMetricIsNotDeleted() {
        createMetricMetadata();
        boolean result = metricMetadataService.deleteByMetricName("nonExistentMetric");
        assertFalse(result);
    }

    @Test
    @Transactional
    @Rollback(true)
    void getByMetricName_ReturnsEmptyOptional_WhenMetricNameDoesNotExist() {

        Optional<MetricMetadataEntity> optionalMetricMetadataEntity = metricMetadataRepository
                .findByMetricName("nonExistentMetric");

        assertTrue(optionalMetricMetadataEntity.isEmpty());
    }

    @Test
    @Transactional
    @Rollback(true)
    void getByMetricName_ReturnsMetricMetadataEntity_WhenMetricNameExists() {
        MetricMetadataEntity metricMetadata = createMetricMetadata();
        Optional<MetricMetadataEntity> optionalMetricMetadataEntity = metricMetadataRepository
                .findByMetricName(metricMetadata.getMetricName());

        assertTrue(optionalMetricMetadataEntity.isPresent());
    }

    @Test
    void getMetricMetadataByName_ReturnsNull_WhenMetricNameDoesNotExist() {
        MetricMetadataEntity result = metricMetadataService.getMetricMetadataByName("nonExistentMetric");
        assertNull(result);
    }

    @Test
    @Transactional
    @Rollback(true)
    void getMetricMetadataByName_ReturnsMetricMetadataEntity_WhenMetricNameExists() {
        MetricMetadataEntity metricMetadata = createMetricMetadata();
        MetricMetadataEntity result = metricMetadataService.getMetricMetadataByName(metricMetadata.getMetricName());
        assertNotNull(result);
    }

    @Test
    @Transactional
    @Rollback(true)
    void getAllMetricOverview_ReturnsListOfMetricMetadataEntity() {
        List<MetricMetadataEntity> metricMetadataEntities = List.of(createMetricMetadata());
        Set<MetricOverviewDTO> result = metricMetadataService.getAllMetricOverview();
        assertFalse(result.isEmpty());
    }


    private MetricMetadataEntity createMetricMetadata() {
        MetricMetadataEntity mtd = new MetricMetadataEntity();
        ColumnMetadataEntity clm = new ColumnMetadataEntity();
        clm.setName("column");
        clm.setType(ValueTypeEnum.STRING);
        mtd.setColumns(Set.of(clm));
        mtd.setMetricName(UUID.randomUUID().toString());

        return metricMetadataRepository.save(mtd);
    }
}