package com.example.stakeholders_service.service;

import com.example.stakeholders_service.model.Role;
import com.example.stakeholders_service.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role findByRoleName(String roleName) {
        return roleRepository.findByName(roleName);
    }
}
