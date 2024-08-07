package com.datawave.datawaveapp.repository;

import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MetricMetadataRepositoryIntegrationTest {

    @Autowired
    private MetricMetadataRepository repository;

    @Test
    @Transactional
    @Rollback
    void findByMetricName_ReturnsEntity_WhenMetricNameExists() {
        MetricMetadataEntity entity = new MetricMetadataEntity();
        entity.setMetricName("existingMetric");
        repository.save(entity);

        Optional<MetricMetadataEntity> foundEntity = repository.findByMetricName("existingMetric");

        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getMetricName()).isEqualTo("existingMetric");
    }

    @Test
    void findByMetricName_ReturnsEmpty_WhenMetricNameDoesNotExist() {
        Optional<MetricMetadataEntity> foundEntity = repository.findByMetricName("nonExistingMetric");

        assertThat(foundEntity).isNotPresent();
    }

    @Test
    @Transactional
    void deleteByMetricName_DeletesEntity_WhenMetricNameExists() {
        MetricMetadataEntity entity = new MetricMetadataEntity();
        entity.setMetricName("metricToDelete");
        repository.save(entity);

        int deletedCount = repository.deleteByMetricName("metricToDelete");

        assertThat(deletedCount).isEqualTo(1);
        assertThat(repository.findByMetricName("metricToDelete")).isNotPresent();
    }

    @Test
    void deleteByMetricName_ReturnsZero_WhenMetricNameDoesNotExist() {
        int deletedCount = repository.deleteByMetricName("nonExistingMetric");

        assertThat(deletedCount).isEqualTo(0);
    }
}
