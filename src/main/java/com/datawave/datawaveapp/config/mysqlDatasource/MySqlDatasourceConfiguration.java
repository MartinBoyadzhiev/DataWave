package com.datawave.datawaveapp.config.mysqlDatasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"com.datawave.datawaveapp.repository.mysqlRepositories"})
public class MySqlDatasourceConfiguration {

    @Bean
    @Qualifier("mySqlProps")
    @ConfigurationProperties("spring.datasource.mysql")
    public DataSourceProperties mySqlProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource mysqlDataSource(@Qualifier("mySqlProps") DataSourceProperties mySqlProps) {
        return mySqlProps
                .initializeDataSourceBuilder()
                .build();
    }
}
