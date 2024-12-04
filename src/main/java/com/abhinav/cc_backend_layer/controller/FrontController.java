package com.abhinav.cc_backend_layer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.service.CCMasterService;

@RestController
public class FrontController {

	@Autowired
	CCMasterService ccMasterService;

	@GetMapping("/health")
	public String health() {
		return "Spring Boot is up and running";
	}

	@GetMapping("/get")
	public List<CCMaster> get() {
		return ccMasterService.getAll();
	}

}
