package com.abhinav.cc_backend_layer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "EMPLOYEE_ATTENDANCE")
public class EmployeeAttendance {
	@Id
	private String date;
	private String day;
	private boolean CM;
	private boolean CE;
	private boolean MM;
	private boolean ME;
	private boolean CW;
}
