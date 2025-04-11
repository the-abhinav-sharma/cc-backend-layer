package com.abhinav.cc_backend_layer.controller;

import java.util.List;
import java.util.Map;

//import org.springframework.ai.image.ImageOptionsBuilder;
//import org.springframework.ai.image.ImagePrompt;
//import org.springframework.ai.image.ImageResponse;
//import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.AmountPerMonth;
import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.UserSession;
import com.abhinav.cc_backend_layer.model.Users;
import com.abhinav.cc_backend_layer.service.CCMasterService;
import com.abhinav.cc_backend_layer.service.CSVService;
import com.abhinav.cc_backend_layer.service.LoginService;
import com.abhinav.cc_backend_layer.service.MailService;

@RestController
@CrossOrigin
public class FrontController {

	@Autowired
	CCMasterService ccMasterService;

	@Autowired
	MailService mailService;

	@Autowired
	CSVService csvService;

	@Autowired
	LoginService loginService;

	@GetMapping("/health")
	public String health() {
		//ccMasterService.sendNotifications();
		//ccMasterService.dataBackup();
		return "CC Backend Spring Boot is up and running";
	}

	@GetMapping("/get")
	public List<CCMaster> get(@RequestHeader("Authorization") String authHeader) {
		if (!checkAuthToken(authHeader)) {
			return null;
		}
		return ccMasterService.getAll();
	}

	@PostMapping("/create")
	public CCMaster create(@RequestBody CCMaster ccMaster, @RequestHeader("Authorization") String authHeader) {
		if (!checkAuthToken(authHeader)) {
			return null;
		}
		return ccMasterService.create(ccMaster);
	}

	@GetMapping("/get/{code}/{monthYear}")
	public CCMaster getByCodeAndMonthYear(@PathVariable String code, @PathVariable String monthYear,
			@RequestHeader("Authorization") String authHeader) {
		if (!checkAuthToken(authHeader)) {
			return null;
		}
		return ccMasterService.getByPrimaryKey(code, monthYear);
	}

	@GetMapping("/get/{param}")
	public List<CCMaster> getByCodeAndMonthYear(@PathVariable String param,
			@RequestHeader("Authorization") String authHeader) {
		if (!checkAuthToken(authHeader)) {
			return null;
		}

		if (param.length() == 6) {
			return ccMasterService.getByMonthYear(param);
		} else {
			return ccMasterService.getByCode(param);
		}
	}

	@GetMapping("/monthlyTotal/{year}")
	public List<AmountPerMonth> monthlyTotal(@PathVariable String year,
			@RequestHeader("Authorization") String authHeader) {
		if (!checkAuthToken(authHeader)) {
			return null;
		}
		return ccMasterService.getAmountPerMonth(year);
	}

	@GetMapping("/cardlyTotal/{year}")
	public List<AmountPerMonth> cardlyTotal(@PathVariable String year,
			@RequestHeader("Authorization") String authHeader) {
		if (!checkAuthToken(authHeader)) {
			return null;
		}
		return ccMasterService.getAmountPerCard(year);
	}

	@GetMapping("/cardNames")
	public Map<String, String> loadCardNames(@RequestHeader("Authorization") String authHeader) {
		if (!checkAuthToken(authHeader)) {
			return null;
		}
		return ccMasterService.codeNames;
	}

	// @GetMapping("/pending")
	public String getPendingPayments() {
		return ccMasterService.getPendingPayments();
	}

	@PostMapping("/login")
	public UserSession login(@RequestBody Users users) {
		return loginService.login(users);
	}

	@PostMapping("/logout")
	public boolean logout(@RequestBody Users users) {
		loginService.logout(users.getUsername());
		return true;
	}

	private boolean checkAuthToken(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return false;
		}
		return loginService.isValidToken(authHeader.substring(7));
	}
}
