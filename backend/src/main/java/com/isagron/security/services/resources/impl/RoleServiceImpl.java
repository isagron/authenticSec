package com.isagron.security.services.resources.impl;

import com.isagron.security.domain.dtos.AuthorityDto;
import com.isagron.security.domain.dtos.RoleDto;
import com.isagron.security.domain.entities.Authority;
import com.isagron.security.domain.entities.Role;
import com.isagron.security.domain.repositories.AuthorityRepository;
import com.isagron.security.domain.repositories.RoleRepository;
import com.isagron.security.exceptions.AuthorityAlreadyExistException;
import com.isagron.security.exceptions.AuthorityNotExistException;
import com.isagron.security.exceptions.RoleNotExistException;
import com.isagron.security.services.client_notification.WebSocketService;
import com.isagron.security.services.resources.RoleService;
import com.isagron.security.services.validators.AppOperation;
import com.isagron.security.services.validators.ValidatorMng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private ValidatorMng validator;

    @Autowired
    private WebSocketService notificationService;

    @Override
    public Optional<Role> findRoleByName(String roleName) {
        return this.roleRepository.findRoleByName(roleName);
    }

    @Override
    public List<String> getAllRoleNames() {
        return this.roleRepository.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleDto> getAllAsDto() {
        return this.roleRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllAuthoritiesNames() {
        return this.authorityRepository.findAll()
                .stream().map(Authority::getName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAuthorityByName(String authorityName) {
        Authority authority = this.authorityRepository.findByName(authorityName)
                .orElseThrow(() -> new AuthorityNotExistException(authorityName));
        this.authorityRepository.deleteByName(authorityName);
        this.notificationService.sendDeleteMessage(convertToDto(authority));
    }

    @Override
    @Transactional
    public void deleteRoleByName(String roleName) {
        Role role = this.roleRepository.findRoleByName(roleName)
                .orElseThrow(() -> new RoleNotExistException(roleName));
        this.roleRepository.deleteByName(roleName);
        this.notificationService.sendDeleteMessage(convertToDto(role));
    }

    @Override
    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        validator.validate(AppOperation.CREATE_ROLE, roleDto, "Failed to create role: " + roleDto.getName());
        Role role = new Role();
        role.setName(roleDto.getName());
        List<Authority> authorities = listOfAuthoritiesFromNames(roleDto.getAuthorities());
        role.addAuthorities(authorities);
        this.roleRepository.save(role);
        RoleDto res = convertToDto(role);
        this.notificationService.sendCreateMessage(res);
        return res;
    }

    @Override
    @Transactional
    public RoleDto updateRoleByName(String roleName, RoleDto roleDto) {
        RoleDto res = this.roleRepository.findRoleByName(roleName)
                .map(role -> {
                    role.clearAuthorities();
                    role.addAuthorities(listOfAuthoritiesFromNames(roleDto.getAuthorities()));
                    this.roleRepository.save(role);
                    return role;
                })
        .map(this::convertToDto)
        .orElseThrow(() -> new RoleNotExistException(roleName));
        this.notificationService.sendUpdateMessage(res);
        return res;
    }

    @Override
    @Transactional
    public AuthorityDto createAuthority(String authorityName) {
        if (this.authorityRepository.existsByName(authorityName)){
            throw new AuthorityAlreadyExistException(authorityName);
        }
        Authority authority = new Authority();
        authority.setName(authorityName);
        this.authorityRepository.save(authority);
        AuthorityDto response = convertToDto(authority);
        notificationService.sendCreateMessage(response);
        return response;
    }

    @Override
    @Transactional
    public List<AuthorityDto> getAllAuthorities() {
        return this.authorityRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AuthorityDto convertToDto(Authority authority) {
        return AuthorityDto.builder()
                .id(authority.getId())
                .name(authority.getName())
                .roles(authority.getRoles() != null ?
                        authority.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    private List<Authority> listOfAuthoritiesFromNames(List<String> authorities) {
        return authorities
                .stream()
                .map(authName -> authorityRepository.findByName(authName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private RoleDto convertToDto(Role role) {
        return RoleDto.builder()
                .name(role.getName())
                .authorities(role.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList()))
                .build();
    }
}
