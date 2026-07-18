package com.abhinav.cc_backend_layer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "fuel_logs")
@Data
public class FuelLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(nullable = false)
    private String city;

    @Column(name = "fuel_station", nullable = false)
    private String fuelStation;

    @Column(nullable = false)
    private Double litres;

    @Column(name = "odometer_reading", nullable = false)
    private Double odometerReading;

    @Column(name = "amount_spent", nullable = false)
    private Double amountSpent;

    @Column(name = "rate_per_litre", nullable = false)
    private Double ratePerLitre;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type", nullable = false)
    private TripType tripType;

    @Column(name = "city_percentage")
    private Integer cityPercentage; // Values from 0 to 100

    public enum FuelType {
        NORMAL, XP100
    }

    public enum TripType {
        CITY, HIGHWAY, MIXED
    }
}