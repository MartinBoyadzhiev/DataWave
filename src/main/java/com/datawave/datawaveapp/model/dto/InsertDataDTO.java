package com.datawave.datawaveapp.model.dto;

public class InsertDataDTO {
    private String metricName;
    private String csvData;

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getCsvData() {
        return csvData;
    }

    public void setCsvData(String csvData) {
        this.csvData = csvData;
    }
}
