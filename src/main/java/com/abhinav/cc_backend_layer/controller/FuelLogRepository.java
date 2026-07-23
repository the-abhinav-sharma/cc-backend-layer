package com.abhinav.cc_backend_layer.controller;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinav.cc_backend_layer.model.FuelLog;

public interface FuelLogRepository extends JpaRepository<FuelLog, Long> {

	List<FuelLog> findAllByOrderByLogDateDesc();

	List<FuelLog> findAllByOrderByOdometerReadingAsc();

	FuelLog findTopByOrderByOdometerReadingDesc();

}
