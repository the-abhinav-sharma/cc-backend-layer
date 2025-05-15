package com.abhinav.cc_backend_layer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinav.cc_backend_layer.model.EmployeeAttendance;

public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance, String> {

}
