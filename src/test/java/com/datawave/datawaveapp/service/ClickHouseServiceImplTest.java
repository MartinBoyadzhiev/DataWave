package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.CreateTableDTO;
import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.model.entity.ValueTypeEnum;
import com.datawave.datawaveapp.repository.MetricMetadataRepository;
import com.datawave.datawaveapp.service.exceptions.MetricAlreadyExistsException;
import com.datawave.datawaveapp.service.exceptions.MetricNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
public class ClickHouseServiceImplTest {

    @Autowired
    private ClickHouseService clickHouseServiceImpl;

    @Autowired
    private MetricMetadataService metricMetadataService;

    @Autowired
    private MetricMetadataRepository metricMetadataRepository;


    @Test
    @Transactional
    @Rollback(true)
    @Disabled
    void createTableWithExistingMetricName() {
        MetricMetadataEntity metricMetadataEntity = new MetricMetadataEntity();
        metricMetadataEntity.setMetricName("existingMetric");
        metricMetadataEntity.setColumns(Set.of(new ColumnMetadataEntity("header1")));
        metricMetadataRepository.save(metricMetadataEntity);

        CreateTableDTO createTableDTO = new CreateTableDTO();
        createTableDTO.setMetricName("existingMetric");
        createTableDTO.setValueType(ValueTypeEnum.FLOAT);
        createTableDTO.setColumns(Map.of("header1", ValueTypeEnum.STRING));
        createTableDTO.setPrimaryKeys(List.of("header1"));

        assertThrows(MetricAlreadyExistsException.class, () -> clickHouseServiceImpl.createTable(createTableDTO));
    }

    @Test
    @DisplayName("Delete table with non-existing metric name")
    void deleteTableWithNonExistingMetricName() {
        String metricName = "nonExistingMetric";

        assertThrows(MetricNotFoundException.class, () -> clickHouseServiceImpl.deleteTable(metricName));
    }

}
