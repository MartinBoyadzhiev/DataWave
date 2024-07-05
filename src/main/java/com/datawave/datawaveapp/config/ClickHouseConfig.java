package com.datawave.datawaveapp.config;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClickHouseConfig {

    @Bean
    public ClickHouseClient clickHouseClient(ClickHouseNode clickHouseNode) {
        return ClickHouseClient.newInstance(clickHouseNode.getProtocol());
    }

    @Bean
    public ClickHouseNode clickHouseNode() {
        return ClickHouseNode.builder()
                .host("localhost")
                .port(ClickHouseProtocol.HTTP, 8123)
                .database("default")
                .build();
    }
}
