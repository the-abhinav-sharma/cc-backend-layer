package com.abhinav.cc_backend_layer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.Answer;
import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.Question;
import com.abhinav.cc_backend_layer.service.CCMasterService;
import com.abhinav.cc_backend_layer.service.OpenAIService;

@RestController
@CrossOrigin
public class FrontController {

	@Autowired
	CCMasterService ccMasterService;
	
	@Autowired
	private OpenAIService openAIService;

	@GetMapping("/health")
	public String health() {
		return "Spring Boot is up and running";
	}
	
	@PostMapping(path = "/ask", consumes = "application/json", produces = "application/json")
	public Answer getAnswer(@RequestBody Question question) {
		return openAIService.getAnswer(question);
	}

	@GetMapping("/get")
	public List<CCMaster> get() {
		return ccMasterService.getAll();
	}

	@GetMapping("/get/{code}/{monthYear}")
	public CCMaster getByCodeAndMonthYear(@PathVariable String code, @PathVariable String monthYear) {
		return ccMasterService.getByPrimaryKey(code, monthYear);
	}

	@GetMapping("/get/{param}")
	public List<CCMaster> getByCodeAndMonthYear(@PathVariable String param) {
		if (param.length() == 6) {
			return ccMasterService.getByMonthYear(param);
		} else {
			return ccMasterService.getByCode(param);
		}

	}

}
