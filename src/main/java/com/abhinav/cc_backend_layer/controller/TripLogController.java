package com.abhinav.cc_backend_layer.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.PendingTripLog;
import com.abhinav.cc_backend_layer.repository.PendingTripLogRepository;

@RestController
@RequestMapping("/api/pending-trips")
@CrossOrigin(origins = "*")
public class TripLogController {
	
	@Autowired
	private PendingTripLogRepository pendingTripLogRepository;
	
	@PostMapping
	public ResponseEntity<PendingTripLog> createPendingTrip(@RequestBody PendingTripLog trip) {
	    if (trip.getLogDate() == null) {
	        trip.setLogDate(LocalDate.now());
	    }
	    return ResponseEntity.ok(pendingTripLogRepository.save(trip));
	}
	
	@GetMapping
	public ResponseEntity<List<PendingTripLog>> getPendingTrips() {
	    return ResponseEntity.ok(pendingTripLogRepository.findAll());
	}
}

