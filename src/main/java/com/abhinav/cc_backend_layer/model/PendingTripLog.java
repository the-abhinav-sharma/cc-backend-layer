package com.abhinav.cc_backend_layer.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pending_trip_logs")
@Data
public class PendingTripLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_date")
    private LocalDate logDate;
    
    @Column(name = "distance_km")
    private Double distanceKm;
    
    @Column(name = "known_highway_mileage")
    private Double knownHighwayMileage;
    
    @Column(name = "notes")
    private String notes;

}