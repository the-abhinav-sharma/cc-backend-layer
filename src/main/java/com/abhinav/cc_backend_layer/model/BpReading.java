package com.abhinav.cc_backend_layer.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "bp_readings", uniqueConstraints = { @UniqueConstraint(columnNames = { "reading_date", "time_of_day" }) })
@Data
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
	private String timeOfDay; // "MORNING" or "EVENING"

	private String notes;

}