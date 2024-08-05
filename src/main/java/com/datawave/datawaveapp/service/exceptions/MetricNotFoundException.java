package com.datawave.datawaveapp.service.exceptions;

public class MetricNotFoundException extends RuntimeException {

    public MetricNotFoundException(String msg) {
        super(msg);
    }
}
