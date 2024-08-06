package com.datawave.datawaveapp.model.dto;

import java.time.Instant;
import java.util.Objects;

public class MetricDataDTO implements Comparable<MetricDataDTO> {

    private Instant timestamp;
    private Number value;


    public MetricDataDTO(Instant timestamp, Number value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public MetricDataDTO() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    @Override
    public int compareTo(MetricDataDTO o) {
        return this.timestamp.compareTo(o.timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricDataDTO that = (MetricDataDTO) o;
        return Objects.equals(timestamp, that.timestamp) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, value);
    }
}
