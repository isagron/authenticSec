package com.isagron.security.domain.repositories;

import com.isagron.security.domain.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository  extends JpaRepository<Authority, Long> {
    boolean existsByName(String authName);

    Optional<Authority> findByName(String authName);

    void deleteByName(String authorityName);

}
