package com.datawave.datawaveapp.service.exceptions;

public class ColumnNotFoundException extends RuntimeException {

    public ColumnNotFoundException(String msg) {
        super(msg);
    }
}
