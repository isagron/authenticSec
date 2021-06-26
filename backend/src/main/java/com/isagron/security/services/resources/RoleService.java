package com.isagron.security.services.resources;


import com.isagron.security.domain.dtos.AuthorityDto;
import com.isagron.security.domain.dtos.RoleDto;
import com.isagron.security.domain.entities.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Optional<Role> findRoleByName(String roleName);

    List<String> getAllRoleNames();

    List<RoleDto> getAllAsDto();

    List<String> getAllAuthoritiesNames();

    void deleteAuthorityByName(String authorityName);

    void deleteRoleByName(String roleName);

    RoleDto createRole(RoleDto roleDto);

    RoleDto updateRoleByName(String roleName, RoleDto roleDto);

    AuthorityDto createAuthority(String authorityName);

    List<AuthorityDto> getAllAuthorities();

}
