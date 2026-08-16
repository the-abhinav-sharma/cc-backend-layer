package com.abhinav.cc_backend_layer.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.abhinav.cc_backend_layer.model.BpReading;
import com.abhinav.cc_backend_layer.model.User;
import com.abhinav.cc_backend_layer.repository.BpReadingRepository;

@Service
public class BpReadingService {

    private final BpReadingRepository bpReadingRepository;

    public BpReadingService(BpReadingRepository bpReadingRepository) {
        this.bpReadingRepository = bpReadingRepository;
    }

    public BpReading saveReading(BpReading reading, User user) {
        reading.setUser(user); // Set the logged-in user
        return bpReadingRepository.save(reading);
    }

    public List<BpReading> getReadingsByRange(User user, LocalDate startDate, LocalDate endDate) {
        // Query isolated records by user ID
        return bpReadingRepository.findByUserIdAndReadingDateBetweenOrderByReadingDateAsc(user.getId(), startDate, endDate);
    }
}