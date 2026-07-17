package com.abhinav.cc_backend_layer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.FuelLog;

@RestController
@RequestMapping("/api/fuel-logs")
@CrossOrigin(origins = "*") // Adjust for production
public class FuelLogController {

	@Autowired
	private FuelLogRepository repository;

	@Value("${pit.stop.pin}")
	private String adminWritePin;

	@PostMapping
	public ResponseEntity<?> createLog(@RequestHeader(value = "X-Admin-Pin", required = false) String pin,
			@RequestBody FuelLog log) {
		
		if (pin == null || !pin.equals(adminWritePin)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Invalid admin write credentials.");
        }
		
		// Simple sanity check: calculate rate if one is missing, or keep raw values
		if (log.getRatePerLitre() == null && log.getLitres() > 0) {
			log.setRatePerLitre(log.getAmountSpent() / log.getLitres());
		}
		return ResponseEntity.ok(repository.save(log));
	}

	@GetMapping
	public ResponseEntity<List<FuelLog>> getAllLogs() {
		return ResponseEntity.ok(repository.findAllByOrderByLogDateDesc());
	}

	@GetMapping("/stats")
	public ResponseEntity<Map<String, Object>> getStats() {
		List<FuelLog> logs = repository.findAllByOrderByOdometerReadingAsc();
		Map<String, Object> stats = new HashMap<>();

		if (logs.isEmpty()) {
			return ResponseEntity.ok(stats);
		}

		double totalSpend = 0;
		double totalLitres = 0;
		for (FuelLog log : logs) {
			totalSpend += log.getAmountSpent();
			totalLitres += log.getLitres();
		}

		// Calculate mileage (requires at least 2 entries)
		double averageMileage = 0;
		if (logs.size() > 1) {
			double totalDistance = logs.get(logs.size() - 1).getOdometerReading() - logs.get(0).getOdometerReading();
			// We exclude the litres of the very first log because that fuel was used to get
			// to the first reading
			double comparativeLitres = 0;
			for (int i = 1; i < logs.size(); i++) {
				comparativeLitres += logs.get(i).getLitres();
			}
			averageMileage = comparativeLitres > 0 ? totalDistance / comparativeLitres : 0;
		}

		stats.put("totalSpend", totalSpend);
		stats.put("totalLitres", totalLitres);
		stats.put("averageMileage", averageMileage);
		stats.put("logCount", logs.size());

		// Simple linear predictive run-rate (assuming past 30 days average)
		double monthlyPrediction = totalSpend;
		if (logs.size() > 1) {
			long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(logs.get(0).getLogDate(),
					logs.get(logs.size() - 1).getLogDate());
			if (daysBetween > 0) {
				double dailySpend = totalSpend / daysBetween;
				monthlyPrediction = dailySpend * 30;
			}
		}
		stats.put("predictedMonthlyExpense", monthlyPrediction);

		return ResponseEntity.ok(stats);
	}
}