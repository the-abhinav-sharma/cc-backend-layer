package com.abhinav.cc_backend_layer.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinav.cc_backend_layer.model.BpReading;

public interface BpReadingRepository extends JpaRepository<BpReading, Long> {
	List<BpReading> findByReadingDateBetweenOrderByReadingDateAsc(LocalDate startDate, LocalDate endDate);

	List<BpReading> findAllByOrderByReadingDateDesc();
}