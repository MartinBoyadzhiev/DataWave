package com.datawave.datawaveapp.model.entity;

public enum ValueTypeEnum {

    STRING("String"),
    INTEGER("Int32"),
    TIMESTAMP("DateTime"),
    FLOAT("Float32");

    private final String value;

    ValueTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
