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
import com.abhinav.cc_backend_layer.model.PendingTripLog;
import com.abhinav.cc_backend_layer.repository.PendingTripLogRepository;

@RestController
@RequestMapping("/api/fuel-logs")
@CrossOrigin(origins = "*")
public class FuelLogController {

	@Autowired
	private FuelLogRepository repository;
	
	@Autowired
	private PendingTripLogRepository pendingTripRepository;

	@Value("${pit.stop.pin}")
	private String adminWritePin;

	@PostMapping
	public ResponseEntity<?> createLog(@RequestHeader(value = "X-Admin-Pin", required = false) String pin,
	        @RequestBody FuelLog log) {
	    
	    if (pin == null || !pin.equals(adminWritePin)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body("Unauthorized: Invalid admin write credentials.");
	    }
	    
	    if (log.getRatePerLitre() == null && log.getLitres() > 0) {
	        log.setRatePerLitre(log.getAmountSpent() / log.getLitres());
	    }

	    // Enforce data consistency based on chosen environment selection
	    if (log.getTripType() == FuelLog.TripType.CITY) {
	        log.setCityPercentage(100);
	    } else if (log.getTripType() == FuelLog.TripType.HIGHWAY) {
	        log.setCityPercentage(0);
	    } else if (log.getCityPercentage() == null) {
	        log.setCityPercentage(50); // Sensible fallback default for mixed
	    }

	    // --- AUTO-CONSUMPTION OF PENDING TRIP QUEUE ---
	    // Only aggregate pending drives if user didn't manually override it in the refuel form
	    if (log.getKnownHighwayMileage() == null) {
	        List<PendingTripLog> pendingTrips = pendingTripRepository.findAll();

	        if (!pendingTrips.isEmpty()) {
	            double totalWeightedMileage = 0;
	            double totalHighwayKm = 0;

	            for (PendingTripLog trip : pendingTrips) {
	                totalWeightedMileage += (trip.getDistanceKm() * trip.getKnownHighwayMileage());
	                totalHighwayKm += trip.getDistanceKm();
	            }

	            if (totalHighwayKm > 0) {
	                // Weighted average calculation: sum(distance * mileage) / totalDistance
	                double calculatedHwyMileage = totalWeightedMileage / totalHighwayKm;
	                log.setKnownHighwayMileage(calculatedHwyMileage);
	            }

	            // Flush the queue after linking to this fill-up
	            pendingTripRepository.deleteAll();
	        }
	    } else {
	        // If the user manually provided a value during refuel, clear the queue anyway so it doesn't leak into the next fill-up
	        pendingTripRepository.deleteAll();
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

	    // Comprehensive Mileage Segment Breakdown
	    double averageMileage = 0;
	    double cityDistance = 0, cityLitres = 0;
	    double highwayDistance = 0, highwayLitres = 0;

	    if (logs.size() > 1) {
	        double totalDistance = logs.get(logs.size() - 1).getOdometerReading() - logs.get(0).getOdometerReading();
	        double comparativeLitres = 0;

	        for (int i = 1; i < logs.size(); i++) {
	            FuelLog currentLog = logs.get(i);
	            FuelLog previousLog = logs.get(i - 1);
	            
	            double segmentDistance = currentLog.getOdometerReading() - previousLog.getOdometerReading();
	            double segmentLitres = currentLog.getLitres();
	            
	            comparativeLitres += segmentLitres;

	            double cityPct = (currentLog.getCityPercentage() != null ? currentLog.getCityPercentage() : 50) / 100.0;
	            double hwyPct = 1.0 - cityPct;

	            double d_c = segmentDistance * cityPct;
	            double d_h = segmentDistance * hwyPct;

	            double L_c = 0;
	            double L_h = 0;

	            if (currentLog.getTripType() == FuelLog.TripType.CITY) {
	                L_c = segmentLitres;
	                L_h = 0;
	            } else if (currentLog.getTripType() == FuelLog.TripType.HIGHWAY) {
	                L_c = 0;
	                L_h = segmentLitres;
	            } else {
	                // MIXED TRIP HYBRID LOGIC
	                if (currentLog.getKnownHighwayMileage() != null && currentLog.getKnownHighwayMileage() > 0) {
	                    // Exact calculation using car dashboard figure
	                    L_h = d_h / currentLog.getKnownHighwayMileage();
	                    
	                    // Safety check to ensure highway fuel does not exceed total fill volume
	                    L_h = Math.min(L_h, segmentLitres);
	                    L_c = segmentLitres - L_h;
	                } else {
	                    // Ratio estimation (Highway assumed 1.35x as efficient as City)
	                    double ratio = 1.35;
	                    L_c = segmentLitres * (d_c / (d_c + (ratio * d_h)));
	                    L_h = segmentLitres - L_c;
	                }
	            }

	            cityDistance += d_c;
	            cityLitres += L_c;

	            highwayDistance += d_h;
	            highwayLitres += L_h;
	        }
	        
	        averageMileage = comparativeLitres > 0 ? totalDistance / comparativeLitres : 0;
	    }

	    stats.put("totalSpend", totalSpend);
	    stats.put("totalLitres", totalLitres);
	    stats.put("averageMileage", averageMileage);
	    stats.put("logCount", logs.size());
	    
	    // Sub-segmented metrics output
	    stats.put("cityMileage", cityLitres > 0 ? cityDistance / cityLitres : 0);
	    stats.put("highwayMileage", highwayLitres > 0 ? highwayDistance / highwayLitres : 0);

	    // Enhanced 30-Day Expense Prediction
	    double monthlyPrediction = totalSpend;

	    if (logs.size() > 1) {
	        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
	            logs.get(0).getLogDate(),
	            logs.get(logs.size() - 1).getLogDate()
	        );

	        if (daysBetween > 0) {
	            double totalDistance = logs.get(logs.size() - 1).getOdometerReading() - logs.get(0).getOdometerReading();
	            
	            // Sum spend for completed trip segments (excluding the initial baseline fill-up)
	            double comparativeSpend = 0;
	            for (int i = 1; i < logs.size(); i++) {
	                comparativeSpend += logs.get(i).getAmountSpent();
	            }

	            double dailyKm = totalDistance / daysBetween;               // e.g., 429 km / 5 days = 85.8 km/day
	            double costPerKm = comparativeSpend / totalDistance;       // e.g., ₹3,300 / 429 km = ₹7.69/km
	            
	            monthlyPrediction = (dailyKm * 30) * costPerKm;              // ~₹19,794/month
	        }
	    }
	    stats.put("predictedMonthlyExpense", monthlyPrediction);

	    return ResponseEntity.ok(stats);
	}
}