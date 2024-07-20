package com.datawave.datawaveapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "column_names")
public class ColumnName extends BaseEntity {
    @Column
    private String name;

    public String getName() {
        return name;
    }
    public ColumnName() {
    }

    public ColumnName(String name) {
        this.name = name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnName that = (ColumnName) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
