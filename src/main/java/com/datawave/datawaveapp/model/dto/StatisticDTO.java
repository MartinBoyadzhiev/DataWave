package com.datawave.datawaveapp.model.dto;

public class StatisticDTO {
    private String metricName;
//    private String userName;
    private long count;

    public StatisticDTO() {
    }

    public StatisticDTO(String metricName, long count) {
        this.metricName = metricName;
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }


}
