package com.abhinav.cc_backend_layer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinav.cc_backend_layer.model.Users;

public interface UserRepository extends JpaRepository<Users, String>{

}
