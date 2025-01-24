package com.abhinav.cc_backend_layer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abhinav.cc_backend_layer.service.CCMasterService;

import jakarta.annotation.PostConstruct;

@Component
public class LoadOnStartup {

	@Autowired
	CCMasterService ccMasterService;

	@PostConstruct
	public void init() {
		ccMasterService.loadCardNames();
		ccMasterService.getPendingPayments();
	}
}