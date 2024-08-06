package com.datawave.datawaveapp.web;

import com.datawave.datawaveapp.model.dto.CreateTableDTO;
import com.datawave.datawaveapp.model.dto.InsertDataDTO;
import com.datawave.datawaveapp.service.ClickHouseService;
import com.datawave.datawaveapp.service.DataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class MetricController {

    private final DataService dataService;
    private final ClickHouseService clickHouseService;

    public MetricController(DataService dataService, ClickHouseService clickHouseService) {
        this.dataService = dataService;
        this.clickHouseService = clickHouseService;
    }

    @PutMapping("/metric/insert-data")
    public ResponseEntity insertData(@Valid @RequestBody InsertDataDTO insertDataDTO) throws IOException {
        this.dataService.insertData(insertDataDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/metric/delete")
    public ResponseEntity deleteTable(@Valid
                                @Pattern(regexp = "^[a-zA-Z0-9]*$")
                                @RequestParam String metricName) {
        this.clickHouseService.deleteTable(metricName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/metric/create")
    public ResponseEntity createTable(@Valid @RequestBody CreateTableDTO createTableData) {
        this.clickHouseService.createTable(createTableData);
        return ResponseEntity.ok().build();
    }
}
