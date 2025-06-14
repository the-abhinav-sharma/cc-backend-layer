package com.abhinav.cc_backend_layer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinav.cc_backend_layer.model.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, Integer>{
	
	UserSession findTopByUsernameOrderBySessionidDesc(String username);
	UserSession findFirstByOrderBySessionidDesc();
}
