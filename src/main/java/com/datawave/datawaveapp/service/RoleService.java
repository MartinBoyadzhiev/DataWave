package com.datawave.datawaveapp.service;

import com.datawave.datawaveapp.model.entity.RoleEntity;

public interface RoleService {
    RoleEntity findByName(String name);
}
