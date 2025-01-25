package com.abhinav.cc_backend_layer.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abhinav.cc_backend_layer.model.AmountPerMonth;
import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterKey;
import com.abhinav.cc_backend_layer.model.CCMasterNames;
import com.abhinav.cc_backend_layer.model.CCMasterNotifications;
import com.abhinav.cc_backend_layer.repository.CCMasterNamesRepository;
import com.abhinav.cc_backend_layer.repository.CCMasterNotificationsRepository;
import com.abhinav.cc_backend_layer.repository.CCMasterRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CCMasterService {

	@Autowired
	CCMasterRepository ccMasterRepository;

	@Autowired
	CCMasterNamesRepository ccMasterNamesRepository;
	
	@Autowired
	CCMasterNotificationsRepository ccMasterNotificationsRepository;

	public Map<String, String> codeNames = new TreeMap<>();
	
	public CCMaster create(CCMaster ccMaster) {
		if (ccMaster.getCreatedOn() == null || ccMaster.getCreatedOn().equals("")) {
			ccMaster.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		} else {
			ccMaster.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		}
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
			Map<String, String> temp = ccMasterNamesRepository.findAll().stream()
					.collect(Collectors.toMap(CCMasterNames::getCode, CCMasterNames::getName));
			codeNames.putAll(temp);
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
	
	public List<AmountPerMonth> getAmountPerMonth(String year) {
		return ccMasterRepository.getAmountPerMonth(year);
	}
	
	public List<AmountPerMonth> getAmountPerCard(String year) {
		return ccMasterRepository.getAmountPerCard(year);
	}
	
	public String getPendingPayments() {
		StringBuffer sb = new StringBuffer();
		sb.append("********************************************************");
		sb.append(System.lineSeparator());
		sb.append("\t\t\tPending Payment Report");
		sb.append(System.lineSeparator());
		sb.append("********************************************************");
		sb.append(System.lineSeparator());
		sb.append(String.format("%30s %13s %6s", "Card Name |", "Due Date   |", "Total Amount"));
		sb.append(System.lineSeparator());
		sb.append("--------------------------------------------------------");
		sb.append(System.lineSeparator());

		for (CCMaster ccMaster : updateListWithNames(ccMasterRepository.findByCurrentStatusNot("Paid"))) {
			sb.append(String.format("%30s %11s %6s", ccMaster.getName() + " |",
					new SimpleDateFormat("dd-MMM-yyyy").format(ccMaster.getDueDate()) + " |",
					NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(ccMaster.getTotalAmt()) + ""));
			sb.append(System.lineSeparator());
			sb.append("--------------------------------------------------------");
			sb.append(System.lineSeparator());
		}
		log.info(sb.toString());
		return sb.toString();
	}
	
	public void sendNotifications(){
		Optional<CCMasterNotifications> notify = ccMasterNotificationsRepository.findById(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
		if(notify.isPresent()) {
			CCMasterNotifications ccMasterNotify= notify.get();
			if(!ccMasterNotify.isFlag()) {
				ccMasterNotify.setContent(getPendingPayments());
				ccMasterNotify.setFlag(true);
				ccMasterNotificationsRepository.save(ccMasterNotify);	
				log.info("Flag updated for "+new Date(new java.util.Date().getTime()).toString()+" record in Notifications table");
			}
		}else {
			CCMasterNotifications ccMasterNotify = new CCMasterNotifications();
			ccMasterNotify.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
			ccMasterNotify.setFlag(true);
			ccMasterNotify.setContent(getPendingPayments());
			ccMasterNotificationsRepository.save(ccMasterNotify);
			log.info("New record inserted in Notifications table");
		}
	}
}
