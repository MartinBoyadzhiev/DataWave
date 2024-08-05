package com.datawave.datawaveapp.service.exceptions;

public class MetricAlreadyExistsException extends RuntimeException {

    public MetricAlreadyExistsException(String msg) {
        super(msg);
    }
}
