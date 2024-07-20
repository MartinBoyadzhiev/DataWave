package com.datawave.datawaveapp.repository.mysqlRepositories;

import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetricMetadataRepository extends JpaRepository<MetricMetadataEntity, Long> {

    Optional<MetricMetadataEntity> findByMetricName(String metricName);
}
