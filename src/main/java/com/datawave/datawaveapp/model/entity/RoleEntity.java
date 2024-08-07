package com.datawave.datawaveapp.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class RoleEntity extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;

    public RoleEntity(String name) {
        this.name = name;
    }

    public RoleEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
