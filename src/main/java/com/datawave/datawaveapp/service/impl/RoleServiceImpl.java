package com.datawave.datawaveapp.service.impl;

import com.datawave.datawaveapp.model.entity.RoleEntity;
import com.datawave.datawaveapp.repository.RoleRepository;
import com.datawave.datawaveapp.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleEntity findByName(String name) {
        return this.roleRepository.findByName(name);
    }
}
