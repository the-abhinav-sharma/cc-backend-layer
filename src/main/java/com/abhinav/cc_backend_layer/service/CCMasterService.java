package com.abhinav.cc_backend_layer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterKey;
import com.abhinav.cc_backend_layer.model.CCMasterNames;
import com.abhinav.cc_backend_layer.model.AmountPerMonth;
import com.abhinav.cc_backend_layer.repository.CCMasterNamesRepository;
import com.abhinav.cc_backend_layer.repository.CCMasterRepository;

@Service
public class CCMasterService {

	@Autowired
	CCMasterRepository ccMasterRepository;

	@Autowired
	CCMasterNamesRepository ccMasterNamesRepository;

	public Map<String, String> codeNames = new HashMap<>();
	
	public CCMaster create(CCMaster ccMaster) {
		return ccMasterRepository.saveAndFlush(ccMaster);
	}

	public List<CCMaster> getAll() {
		return updateListWithNames(ccMasterRepository.findAll());
	}

	public CCMaster getByPrimaryKey(String code, String monthYear) {
		CCMasterKey key = new CCMasterKey();
		key.setCode(code);
		key.setStmtMonthYear(monthYear);
		return updateObjectWithName(ccMasterRepository.findById(key).orElse(null));
	}

	public List<CCMaster> getByCode(String code) {
		return updateListWithNames(ccMasterRepository.findAllByKeyCode(code));
	}

	public List<CCMaster> getByMonthYear(String monthYear) {
		return updateListWithNames(ccMasterRepository.findAllByKeyStmtMonthYear(monthYear));
	}

	public void loadCardNames() {
		if (codeNames.isEmpty()) {
			codeNames = ccMasterNamesRepository.findAll().stream()
					.collect(Collectors.toMap(CCMasterNames::getCode, CCMasterNames::getName));
		}
	}

	private List<CCMaster> updateListWithNames(List<CCMaster> list) {
		list.forEach(cc -> cc.setName(codeNames.get(cc.getKey().getCode())));
		return list;
	}

	private CCMaster updateObjectWithName(CCMaster ccMaster) {
		if (ccMaster != null) {
			ccMaster.setName(codeNames.get(ccMaster.getKey().getCode()));
		}
		return ccMaster;
	}
	
	public List<AmountPerMonth> getAmountPerMonth() {
		return ccMasterRepository.getAmountPerMonth();
	}
	
	public List<AmountPerMonth> getAmountPerCard() {
		return ccMasterRepository.getAmountPerCard();
	}
}
