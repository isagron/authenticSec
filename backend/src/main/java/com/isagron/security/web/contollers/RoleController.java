package com.isagron.security.web.contollers;

import com.isagron.security.domain.dtos.AuthorityDto;
import com.isagron.security.domain.dtos.RoleDto;
import com.isagron.security.services.resources.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/names")
    public List<String> getAllRoleNames() {
        return roleService.getAllRoleNames();
    }

    @GetMapping()
    public List<RoleDto> getAll() {
        return roleService.getAllAsDto();
    }


    @GetMapping("/authorities")
    public List<AuthorityDto> getAllAuthorities() {
        return roleService.getAllAuthorities();
    }

    @PostMapping
    public RoleDto createRole(@RequestBody RoleDto roleDto){
        return this.roleService.createRole(roleDto);
    }

    @PostMapping("/authorities")
    public AuthorityDto createAuthority(@RequestBody String authorityName){
        return this.roleService.createAuthority(authorityName);
    }

    @PutMapping("/{roleName}")
    public RoleDto updateRole(@PathVariable String roleName, @RequestBody RoleDto roleDto){
        return this.roleService.updateRoleByName(roleName, roleDto);
    }

    @DeleteMapping("/authorities/{authorityName}")
    public void deleteAuthorityByName(@PathVariable String authorityName) {
        roleService.deleteAuthorityByName(authorityName);
    }

    @DeleteMapping("/{roleName}")
    public void deleteRoleByName(@PathVariable String roleName) {
        roleService.deleteRoleByName(roleName);
    }
}
