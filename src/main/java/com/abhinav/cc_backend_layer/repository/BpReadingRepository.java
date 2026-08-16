package com.abhinav.cc_backend_layer.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinav.cc_backend_layer.model.BpReading;

public interface BpReadingRepository extends JpaRepository<BpReading, Long> {

    // Fetch range-filtered logs for a specific user, ordered chronologically
    List<BpReading> findByUserIdAndReadingDateBetweenOrderByReadingDateAsc(Long userId, LocalDate startDate, LocalDate endDate);

    // Fetch all logs for a specific user, ordered newest first (for table views)
    List<BpReading> findByUserIdOrderByReadingDateDesc(Long userId);

    // Optional: Check if a reading already exists for a specific user, date, and slot
    Optional<BpReading> findByUserIdAndReadingDateAndTimeOfDay(Long userId, LocalDate readingDate, String timeOfDay);
}