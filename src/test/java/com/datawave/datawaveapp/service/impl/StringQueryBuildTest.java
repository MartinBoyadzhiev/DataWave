package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.model.entity.ColumnMetadataEntity;
import com.datawave.datawaveapp.service.exceptions.IllegalCSVDataFormatException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StringQueryBuildTest {

    @Test
    void testStringQueryBuild() {
        DataServiceImpl service = new DataServiceImpl(null, null);
        StringBuilder stringBuilder = service.buildInsertDataPreparedStatementQuery("table", List.of("column1", "column2"));
        String result = stringBuilder.toString();
        String expected = "INSERT INTO table (column1, column2) VALUES (?, ?)";
        assertEquals(expected, result);
    }

    @Test
    void validateHeaders_ReturnsTrue_WhenHeadersAreValid() {
        DataServiceImpl service = new DataServiceImpl(null, null);
        Set<ColumnMetadataEntity> columns = new HashSet<>();
        columns.add(new ColumnMetadataEntity("column1"));
        columns.add(new ColumnMetadataEntity("column2"));

        assertDoesNotThrow(() -> service.validateHeaders(columns, List.of("column1", "column2")));
    }
    @Test
    void validateHeaders_ReturnsFalse_WhenHeadersAreInvalid() {
        DataServiceImpl service = new DataServiceImpl(null, null);
        Set<ColumnMetadataEntity> columns = new HashSet<>();
        columns.add(new ColumnMetadataEntity("column1"));
        columns.add(new ColumnMetadataEntity("column2"));

        assertThrows(IllegalCSVDataFormatException.class, () -> service.validateHeaders(columns, List.of("column1", "column3")));
    }
}
