package com.isagron.security.domain.repositories;


import com.isagron.security.domain.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleByName(String name);

    boolean existsByName(String name);

    void deleteByName(String roleName);
}
