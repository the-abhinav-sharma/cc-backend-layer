package com.abhinav.cc_backend_layer.model;

import java.util.List;

public class EmployeeListWrapper {
	
	private List<EmployeeAttendance> employees;

	public List<EmployeeAttendance> getEmployees() {
		return employees;
	}

	public void setEmployees(List<EmployeeAttendance> employees) {
		this.employees = employees;
	}
}
