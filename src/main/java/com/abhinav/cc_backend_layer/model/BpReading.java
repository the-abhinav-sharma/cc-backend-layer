package com.abhinav.cc_backend_layer.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "bp_readings", 
    uniqueConstraints = { 
        @UniqueConstraint(
            name = "uq_user_reading_slot", 
            columnNames = { "user_id", "reading_date", "time_of_day" }
        ) 
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer systolic;
    private Integer diastolic;
    private Integer pulse;

    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    @Column(name = "time_of_day", nullable = false)
    private String timeOfDay; // "MORNING", "AFTERNOON", or "EVENING"

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

}