package com.jm.demo.service;

import com.jm.demo.data.dto.RoleDto;
import com.jm.demo.data.model.Role;
import com.jm.demo.exceptions.NotFoundException;
import com.jm.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public Collection<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role create(RoleDto role) {
        log.info("Saving new role {} to database", role.getName());
        final Role roleMapped = modelMapper.map(role, Role.class);
        return roleRepository.save(roleMapped);
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new NotFoundException(name));
    }
}
