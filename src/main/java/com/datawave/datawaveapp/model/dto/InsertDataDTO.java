package com.datawave.datawaveapp.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class InsertDataDTO {
    @NotBlank
    private String metricName;
    @NotEmpty
    private String csvData;

    public InsertDataDTO() {
    }

    public InsertDataDTO(String metricName, String csvData) {
        this.metricName = metricName;
        this.csvData = csvData;
    }

    public void setCsvData(String csvData) {
        this.csvData = csvData;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getCsvData() {
        return csvData;
    }

}
