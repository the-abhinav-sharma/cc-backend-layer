package com.abhinav.cc_backend_layer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.abhinav.cc_backend_layer.model.EmployeeListWrapper;
import com.abhinav.cc_backend_layer.model.EmployeeService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@Slf4j
@Controller
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;

	@GetMapping("/attendance")
	public String showAttendanceForm(Model model) {

		EmployeeListWrapper wrapper = new EmployeeListWrapper();
		wrapper.setEmployees(employeeService.getAll());

		model.addAttribute("employees", wrapper);
		return "attendance";
	}

	@PostMapping("/attendance")
	public String submitAttendance(@ModelAttribute("employees") EmployeeListWrapper wrapper, Model model) {
		model.addAttribute("message", employeeService.submitAttendance(wrapper));
		return "result";
	}
}
