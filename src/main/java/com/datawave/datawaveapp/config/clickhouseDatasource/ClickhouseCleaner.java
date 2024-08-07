package com.datawave.datawaveapp.config.clickhouseDatasource;

import com.datawave.datawaveapp.repository.MetricMetadataRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
public class ClickhouseCleaner {

    private final JdbcTemplate jdbcTemplate;
    private final MetricMetadataRepository metricMetadataRepository;

    public ClickhouseCleaner(JdbcTemplate jdbcTemplate, MetricMetadataRepository metricMetadataRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.metricMetadataRepository = metricMetadataRepository;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 10000)
    public void cleanOldData() {

        this.metricMetadataRepository.findAll().forEach(metricMetadataEntity -> {
            String metricName = metricMetadataEntity.getMetricName();
            String clikhouseMetricName = "metric_" + metricName;
            try {
                jdbcTemplate.execute("DELETE FROM default." + clikhouseMetricName +
                        " WHERE timestamp < now() - INTERVAL 1 MINUTE ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
