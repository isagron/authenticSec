package com.isagron.security.domain.repositories;

import com.isagron.security.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUserName(String userName);

    Optional<User> findUserByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    Page<User> findAll(Pageable p);

    Page<User> findAllByRole(String role, Pageable p);

    Page<User> findAllByUserName(String userName, Pageable p);

    Page<User> findAllByUserNameAndRole(String userName, String role, Pageable p);

    void deleteByUserId(String id);

    Optional<User> findByUserId(String id);
}
