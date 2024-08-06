package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.dto.CreateTableDTO;
import com.datawave.datawaveapp.model.dto.InsertDataDTO;
import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import com.datawave.datawaveapp.model.entity.MetricMetadataEntity;
import com.datawave.datawaveapp.model.entity.ValueTypeEnum;
import com.datawave.datawaveapp.service.exceptions.IllegalCSVDataFormatException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



@SpringBootTest
class DataServiceImpl {

    @Autowired
    private DataService dataServiceImpl;

    @Autowired
    private MetricMetadataService metricMetadataService;

    @Autowired
    private ClickHouseService clickHouseService;

    @Test
    @Transactional
    @Rollback(true)
    void insertDataSuccessfully() throws IOException {
        CreateTableDTO createTableDTO = new CreateTableDTO();
        String name = UUID.randomUUID().toString().substring(0, 8);
        createTableDTO.setMetricName(name);
        createTableDTO.setValueType(ValueTypeEnum.FLOAT);
        createTableDTO.setColumns(Map.of("header1", ValueTypeEnum.STRING, "header2", ValueTypeEnum.STRING));
        createTableDTO.setPrimaryKeys(List.of("header1"));

        clickHouseService.createTable(createTableDTO);

        InsertDataDTO data = validData(name);

        assertDoesNotThrow(() -> dataServiceImpl.insertData(data));
    }

    @Test
    @Disabled
    void metricNotFound() {
        InsertDataDTO data = validData("do");

        when(metricMetadataService.getByMetricName(anyString())).thenReturn(Optional.empty());

//        assertThrows(MetricNotFoundException.class, () -> dataServiceImpl.insertData(insertDataDTO));
    }

    @Test
    @Disabled
    void csvContainsNullData() {
        InsertDataDTO insertDataDTO = new InsertDataDTO("metric1", "header1,header2\nvalue1,");
        MetricMetadataEntity metricMetadataEntity = mock(MetricMetadataEntity.class);
        ColumnMetadataEntity column1 = new ColumnMetadataEntity("header1");
        column1.setType(ValueTypeEnum.STRING);
        ColumnMetadataEntity column2 = new ColumnMetadataEntity("header2");
        column2.setType(ValueTypeEnum.STRING);

        assertThrows(IllegalCSVDataFormatException.class, () -> dataServiceImpl.insertData(insertDataDTO));
    }

    @Test
    @Disabled
    void headersDoNotMatchColumnNames() {
        InsertDataDTO insertDataDTO = new InsertDataDTO("metric1", "header1,header3\nvalue1,value2");
        MetricMetadataEntity metricMetadataEntity = mock(MetricMetadataEntity.class);
        ColumnMetadataEntity column1 = new ColumnMetadataEntity("header1");
        column1.setType(ValueTypeEnum.STRING);
        ColumnMetadataEntity column2 = new ColumnMetadataEntity("header2");
        column2.setType(ValueTypeEnum.STRING);

        assertThrows(IllegalCSVDataFormatException.class, () -> dataServiceImpl.insertData(insertDataDTO));
    }
    private InsertDataDTO validData(String name) {
        return new InsertDataDTO(name, "header1,header2\nvalue1,value2");
    }
    private InsertDataDTO invalidData() {
        return new InsertDataDTO("metric1", "header1,header3\nvalue1,value2");
    }
    private MetricMetadataEntity createMetricMetadata() {
        MetricMetadataEntity mtd = new MetricMetadataEntity();
        ColumnMetadataEntity column1 = new ColumnMetadataEntity("timestamp");
        column1.setType(ValueTypeEnum.TIMESTAMP);
        ColumnMetadataEntity column2 = new ColumnMetadataEntity("value");
        column2.setType(ValueTypeEnum.FLOAT);
        ColumnMetadataEntity column3 = new ColumnMetadataEntity("header1");
        column3.setType(ValueTypeEnum.STRING);
        ColumnMetadataEntity column4 = new ColumnMetadataEntity("header2");
        column4.setType(ValueTypeEnum.STRING);

        mtd.setColumns(Set.of(column1, column2));
        mtd.setMetricName("metric1");

        return mtd;
    }
}
