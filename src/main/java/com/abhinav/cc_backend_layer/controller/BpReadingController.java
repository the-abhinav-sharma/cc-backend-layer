package com.abhinav.cc_backend_layer.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.BpReading;
import com.abhinav.cc_backend_layer.repository.BpReadingRepository;

@RestController
@RequestMapping("/api/bp")
@CrossOrigin(origins = "*")
public class BpReadingController {

	private final BpReadingRepository repository;

	public BpReadingController(BpReadingRepository repository) {
		this.repository = repository;
	}

	@PostMapping
	public BpReading logReading(@RequestBody BpReading reading) {
		if (reading.getReadingDate() == null) {
			reading.setReadingDate(LocalDate.now());
		}
		return repository.save(reading);
	}

	@GetMapping
	public List<BpReading> getAllReadings() {
		return repository.findAllByOrderByReadingDateDesc();
	}

	@GetMapping("/range")
	public List<BpReading> getReadingsByRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
		return repository.findByReadingDateBetweenOrderByReadingDateAsc(start, end);
	}
}