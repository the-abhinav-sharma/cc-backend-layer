package com.abhinav.cc_backend_layer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinav.cc_backend_layer.model.PendingTripLog;

public interface PendingTripLogRepository extends JpaRepository<PendingTripLog, Long> {}