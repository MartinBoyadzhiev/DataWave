package com.datawave.datawaveapp.repository;

import com.datawave.datawaveapp.model.entity.MetricStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface MetricStatsRepository extends JpaRepository<MetricStatsEntity, Long> {

    Optional<MetricStatsEntity> findByMetricMetadataEntityIdAndUserEntityId(Long metricMetadataEntity, Long userEntity);
}
