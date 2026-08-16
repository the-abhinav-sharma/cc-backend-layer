package com.abhinav.cc_backend_layer.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.BpReading;
import com.abhinav.cc_backend_layer.model.User;
import com.abhinav.cc_backend_layer.repository.BpReadingRepository;
import com.abhinav.cc_backend_layer.repository.CommonUserRepository;

@RestController
@RequestMapping("/api/bp")
@CrossOrigin(origins = "*")
public class BpReadingController {

	private final BpReadingRepository repository;
	private final CommonUserRepository userRepository;

	public BpReadingController(BpReadingRepository repository, CommonUserRepository userRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
	}

	private User getAuthenticatedUser(String principal) {
		String[] parts = principal.split(":");
		String username = parts[0];
		String appCode = parts[1];

		return userRepository.findByApplicationAppCodeAndUsername(appCode, username)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	@PostMapping
	public BpReading logReading(@RequestBody BpReading reading, @AuthenticationPrincipal String principal) {
		User currentUser = getAuthenticatedUser(principal);
		
		if (reading.getReadingDate() == null) {
			reading.setReadingDate(LocalDate.now());
		}
		reading.setUser(currentUser);
		return repository.save(reading);
	}

	@GetMapping
	public List<BpReading> getAllReadings(@AuthenticationPrincipal String principal) {
		User currentUser = getAuthenticatedUser(principal);
		return repository.findByUserIdOrderByReadingDateDesc(currentUser.getId());
	}

	@GetMapping("/range")
	public List<BpReading> getReadingsByRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
			@AuthenticationPrincipal String principal) {
		User currentUser = getAuthenticatedUser(principal);
		return repository.findByUserIdAndReadingDateBetweenOrderByReadingDateAsc(currentUser.getId(), start, end);
	}
}