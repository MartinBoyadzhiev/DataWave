package com.datawave.datawaveapp.config.clickhouseDatasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ClickhouseDatasourceConfiguration {

    @Bean
    @Qualifier("clickProps")
    @ConfigurationProperties("spring.datasource.clickhouse")
    public DataSourceProperties clickhouseProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Qualifier("clickDatasource")
    public DataSource clckhouseDataSource(@Qualifier("clickProps") DataSourceProperties clickhouseProps) {
        return clickhouseProps
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Qualifier("clickTemplate")
    public JdbcTemplate clickhouseJdbc(@Qualifier("clickDatasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
