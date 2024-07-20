package com.datawave.datawaveapp;

import com.datawave.datawaveapp.model.entity.ColumnName;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.MetricMetadataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@SpringBootApplication
public class DataWaveApplication implements CommandLineRunner {

    private final MetricMetadataRepository metricMetadataRepository;

    public DataWaveApplication(MetricMetadataRepository metricMetadataRepository) {
        this.metricMetadataRepository = metricMetadataRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(DataWaveApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        if (metricMetadataRepository.findByMetricName("metric_price").isPresent()) {
            return;
        }

        MetricMetadataEntity metricMetadata = new MetricMetadataEntity();
        metricMetadata.setMetricName("metric_price");
        metricMetadata.setColumnNames(Set.of(new ColumnName("timestamp"), new ColumnName("value"),
                new ColumnName("asset"), new ColumnName("exchange"))
        );
        metricMetadataRepository.save(metricMetadata);
    }
}
