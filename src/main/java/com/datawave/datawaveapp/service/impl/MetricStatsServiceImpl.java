package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.config.securityConfig.JwtProvider;
import com.datawave.datawaveapp.model.dto.StatisticDTO;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.model.entity.MetricStatsEntity;
import com.datawave.datawaveapp.model.entity.UserEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.MetricStatsRepository;
import com.datawave.datawaveapp.service.MetricMetadataService;
import com.datawave.datawaveapp.service.MetricStatsService;
import com.datawave.datawaveapp.service.UserEntityService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class MetricStatsServiceImpl implements MetricStatsService {

    private final JwtProvider jwtProvider;
    private final UserEntityService userEntityService;
    private final MetricMetadataService metricMetadataService;
    private final MetricStatsRepository metricStatsRepository;


    public MetricStatsServiceImpl(JwtProvider jwtProvider, UserEntityService userEntityService, MetricMetadataService metricMetadataService, MetricStatsRepository metricStatsRepository) {
        this.jwtProvider = jwtProvider;
        this.userEntityService = userEntityService;
        this.metricMetadataService = metricMetadataService;
        this.metricStatsRepository = metricStatsRepository;
    }

    @Override
    public Set<StatisticDTO> getStatistics() {
        return null;
    }

    @Override
    public void updateStatistics(String metricName, String token) {
        String email = jwtProvider.getEmailFromJwtToken(token);
        UserEntity user = userEntityService.getUserByEmail(email);

        MetricMetadataEntity metricMetadata = metricMetadataService.getMetricMetadataByName(metricName);

        if (metricMetadata == null) {
            throw new IllegalArgumentException("Metric with name " + metricName + " does not exist");
        }
        Optional<MetricStatsEntity> optionalMetricStats = metricStatsRepository
                .findByMetricMetadataEntityIdAndUserEntityId(metricMetadata.getId(), user.getId());

        if (optionalMetricStats.isEmpty()) {
            MetricStatsEntity metricStats = new MetricStatsEntity();
            metricStats.setMetricMetadataEntity(metricMetadata);
            metricStats.setUserEntity(user);
            metricStats.setCount(1);
            metricStatsRepository.save(metricStats);
            return;
        }

        MetricStatsEntity metricStats = optionalMetricStats.get();
        metricStats.setCount(metricStats.getCount() + 1);
        metricStatsRepository.save(metricStats);
    }
}
