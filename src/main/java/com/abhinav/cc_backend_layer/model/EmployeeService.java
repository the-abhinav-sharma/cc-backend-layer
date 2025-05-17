package com.abhinav.cc_backend_layer.model;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abhinav.cc_backend_layer.repository.EmployeeAttendanceRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {
	
	@Autowired
	EmployeeAttendanceRepository employeeAttendanceRepository;
	
	public List<EmployeeAttendance> getAll() {
		return employeeAttendanceRepository.findAll();
	}
	
	public String submitAttendance(EmployeeListWrapper wrapper) {
		for (EmployeeAttendance empAtt : wrapper.getEmployees()) {
            employeeAttendanceRepository.save(empAtt);
        }
		return "Attendance Submitted!";
	}
	
	public void insertDailyRecord() {
		EmployeeAttendance empAtt = new EmployeeAttendance();
		empAtt.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
		empAtt.setDay(new SimpleDateFormat("EEEE").format(new java.util.Date()));
		empAtt.setCM(false);
		empAtt.setCE(false);
		empAtt.setMM(false);
		empAtt.setME(false);
		empAtt.setCW(false);
        employeeAttendanceRepository.save(empAtt);
	}

}
