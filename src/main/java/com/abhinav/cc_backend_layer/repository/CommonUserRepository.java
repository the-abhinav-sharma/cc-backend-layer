package com.abhinav.cc_backend_layer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abhinav.cc_backend_layer.model.User;

@Repository
public interface CommonUserRepository extends JpaRepository<User, Long> {

    // Find user by username within a specific application
    Optional<User> findByApplicationAppCodeAndUsername(String appCode, String username);

    // Find user by email within a specific application
    Optional<User> findByApplicationAppCodeAndEmail(String appCode, String email);

    // Uniqueness checks per application
    boolean existsByApplicationAppCodeAndUsername(String appCode, String username);
    boolean existsByApplicationAppCodeAndEmail(String appCode, String email);

    User findByUsername(String username);
}