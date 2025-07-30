package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.RoleEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.RoleRepository;
import com.EdumentumBackend.EdumentumBackend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleEntity findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new NotFoundException("Role name '" + name + "' not found"));
    }
}
