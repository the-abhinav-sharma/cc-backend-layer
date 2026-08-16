package com.abhinav.cc_backend_layer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinav.cc_backend_layer.model.Application;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    Optional<Application> findByAppCode(String appCode);
}