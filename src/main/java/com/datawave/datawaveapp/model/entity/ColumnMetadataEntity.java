package com.datawave.datawaveapp.model.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "column_metadata")
public class ColumnMetadataEntity extends BaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ValueTypeEnum type;

    public ColumnMetadataEntity() {
    }
    public ColumnMetadataEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueTypeEnum getType() {
        return type;
    }

    public void setType(ValueTypeEnum type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnMetadataEntity that = (ColumnMetadataEntity) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
