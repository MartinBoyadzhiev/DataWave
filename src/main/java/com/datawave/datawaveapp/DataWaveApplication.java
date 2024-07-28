package com.datawave.datawaveapp;

import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.repository.mysqlRepositories.MetricMetadataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Set;

@SpringBootApplication
public class DataWaveApplication implements CommandLineRunner {

    private final MetricMetadataRepository metricMetadataRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataWaveApplication(MetricMetadataRepository metricMetadataRepository, JdbcTemplate jdbcTemplate) {
        this.metricMetadataRepository = metricMetadataRepository;
        this.jdbcTemplate = jdbcTemplate;
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
        metricMetadata.setColumnNames(Set.of(new ColumnMetadataEntity("timestamp"), new ColumnMetadataEntity("value"),
                new ColumnMetadataEntity("asset"), new ColumnMetadataEntity("exchange"))
        );
        metricMetadataRepository.save(metricMetadata);

        try {
            jdbcTemplate.execute("""
                CREATE TABLE default."metric_price"
                (
                    timestamp DateTime,
                    value Float32,
                    asset String,
                    exchange String
                )
                ENGINE = MergeTree()
                PRIMARY KEY (timestamp, asset, exchange);


                INSERT INTO default."metric_price" (timestamp, value, asset, exchange) VALUES
                ('2024-07-01 12:00:00', 102.5, 'BTC', 'Binance'),
                ('2024-07-01 12:01:00', 103.2, 'BTC', 'Binance'),
                ('2024-07-01 12:02:00', 101.9, 'BTC', 'Binance'),
                ('2024-07-01 12:00:00', 2000.1, 'ETH', 'Coinbase'),
                ('2024-07-01 12:01:00', 2001.5, 'ETH', 'Coinbase'),
                ('2024-07-01 12:02:00', 1999.8, 'ETH', 'Coinbase'),
                ('2024-07-01 12:00:00', 400.3, 'LTC', 'Kraken'),
                ('2024-07-01 12:01:00', 401.7, 'LTC', 'Kraken'),
                ('2024-07-01 12:02:00', 399.9, 'LTC', 'Kraken'),
                ('2024-07-01 12:03:00', 90.5, 'BTC', 'Binance'),
                ('2024-07-01 12:04:00', 87.2, 'BTC', 'Binance'),
                ('2024-07-01 12:05:00', 82.9, 'BTC', 'Binance'),
                ('2024-07-01 12:06:00', 93.5, 'BTC', 'Binance'),
                ('2024-07-01 12:07:00', 90.2, 'BTC', 'Binance'),
                ('2024-07-01 12:08:00', 96.9, 'BTC', 'Binance'),
                ('2024-07-01 12:09:00', 115.5, 'BTC', 'Binance'),
                ('2024-07-01 12:10:00', 100.2, 'BTC', 'Binance'),
                ('2024-07-01 12:11:00', 103.9, 'BTC', 'Binance'),
                ('2024-07-01 12:12:00', 100.5, 'BTC', 'Binance'),
                ('2024-07-01 12:13:00', 120.2, 'BTC', 'Binance'),
                ('2024-07-01 12:14:00', 115.9, 'BTC', 'Binance');
                """);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
