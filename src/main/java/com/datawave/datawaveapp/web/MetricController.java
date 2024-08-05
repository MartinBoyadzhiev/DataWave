package com.datawave.datawaveapp.web;

import com.datawave.datawaveapp.model.dto.BasicResponseDTO;
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
    public ResponseEntity<String> insertData(@RequestBody InsertDataDTO insertDataDTO) throws IOException {
        this.dataService.insertData(insertDataDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/metric/delete")
    public ResponseEntity<BasicResponseDTO> deleteTable(@Valid
                                                        @Pattern(regexp = "^[a-zA-Z0-9]*$")
                                                        @RequestParam String metricName) {
        return this.clickHouseService.deleteTable(metricName);
    }

    @PostMapping("/metric/create")
    public ResponseEntity<BasicResponseDTO> createTable(@Valid @RequestBody CreateTableDTO createTableData) {
        return this.clickHouseService.createTable(createTableData);
    }
}
