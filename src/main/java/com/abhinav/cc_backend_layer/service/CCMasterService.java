package com.abhinav.cc_backend_layer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.repository.CCMasterRepository;

@Service
public class CCMasterService {

	@Autowired
	CCMasterRepository ccMasterRepository;

	public List<CCMaster> getAll() {
		return ccMasterRepository.findAll();
	}

}
