package com.abhinav.cc_backend_layer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterKey;
import com.abhinav.cc_backend_layer.repository.CCMasterRepository;

@Service
public class CCMasterService {

	@Autowired
	CCMasterRepository ccMasterRepository;

	public List<CCMaster> getAll() {
		return ccMasterRepository.findAll();
	}

	public CCMaster getByPrimaryKey(String code, String monthYear) {
		CCMasterKey key = new CCMasterKey();
		key.setCode(code);
		key.setStmtMonthYear(monthYear);
		return ccMasterRepository.findById(key).orElse(null);
	}

	public List<CCMaster> getByCode(String code) {
		return ccMasterRepository.findAllByKeyCode(code);
	}

	public List<CCMaster> getByMonthYear(String monthYear) {
		return ccMasterRepository.findAllByKeyStmtMonthYear(monthYear);
	}

}
