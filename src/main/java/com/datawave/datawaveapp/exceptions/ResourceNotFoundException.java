package com.datawave.datawaveapp.exceptions;

public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String resource) {
        super(resource);
    }
}
