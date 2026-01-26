package com.abhinav.cc_backend_layer.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
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
import com.abhinav.cc_backend_layer.model.Users;
import com.abhinav.cc_backend_layer.repository.CCMasterNamesRepository;
import com.abhinav.cc_backend_layer.repository.CCMasterNotificationsRepository;
import com.abhinav.cc_backend_layer.repository.CCMasterRepository;
import com.abhinav.cc_backend_layer.repository.UserRepository;

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

	@Autowired
	MailService mailService;

	@Autowired
	CSVService csvService;

	@Autowired
	UserRepository userRepository;

	public Map<String, String> codeNames = new TreeMap<>();

	public CCMaster create(CCMaster ccMaster) {
		if (ccMaster.getCreatedOn() == null || ccMaster.getCreatedOn().equals("")) {
			ccMaster.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		} else {
			ccMaster.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		}
		ccMaster = ccMasterRepository.saveAndFlush(ccMaster);

		new Thread(() -> {
			Optional<CCMasterNotifications> notify = getNotificationObject();
			if (notify.isEmpty()) {
				createNotifyRecord(null, false);
			} else {
				notify.get().setFlag(false);
				ccMasterNotificationsRepository.save(notify.get());
			}
		}).start();

		return ccMaster;
	}

	public CCMaster create(CCMaster ccMaster, String username) {
		if (ccMaster.getCreatedOn() == null || ccMaster.getCreatedOn().equals("")) {
			ccMaster.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		} else {
			ccMaster.setModifiedOn(new Timestamp(System.currentTimeMillis()));
		}
		ccMaster.setUsername(username);
		ccMaster = ccMasterRepository.saveAndFlush(ccMaster);

		new Thread(() -> {
			Optional<CCMasterNotifications> notify = getNotificationObject();
			if (notify.isEmpty()) {
				createNotifyRecord(null, false);
			} else {
				notify.get().setFlag(false);
				ccMasterNotificationsRepository.save(notify.get());
			}
		}).start();

		return ccMaster;
	}

	public List<CCMaster> getAll() {
		return sortListByDueDate(updateListWithNames(ccMasterRepository.findAll()));
	}

	public List<CCMaster> getAll(String username) {
		return sortListByDueDate(updateListWithNames(ccMasterRepository.findAllByUsername(username)));
	}

	public CCMaster getByPrimaryKey(String code, String monthYear) {
		CCMasterKey key = new CCMasterKey();
		key.setCode(code);
		key.setStmtMonthYear(monthYear);
		return updateObjectWithName(ccMasterRepository.findById(key).orElse(null));
	}

	public CCMaster getByPrimaryKey(String code, String monthYear, String username) {
		CCMasterKey key = new CCMasterKey();
		key.setCode(code);
		key.setStmtMonthYear(monthYear);

		Optional<CCMaster> optCCMaster = ccMasterRepository.findById(key);
		if (optCCMaster.isEmpty()) {
			return null;
		} else {
			if (optCCMaster.get().getUsername().equalsIgnoreCase(username)) {
				return updateObjectWithName(optCCMaster.get());// TBC
			}
		}
		return null;
	}

	public List<CCMaster> getByCode(String code) {
		return updateListWithNames(ccMasterRepository.findAllByKeyCode(code));
	}

	public List<CCMaster> getByCode(String code, String username) {
		return updateListWithNames(ccMasterRepository.findAllByKeyCodeAndUsername(code, username));
	}

	public List<CCMaster> getByMonthYear(String monthYear) {
		return updateListWithNames(ccMasterRepository.findAllByKeyStmtMonthYear(monthYear));
	}

	public List<CCMaster> getByMonthYear(String monthYear, String username) {
		return updateListWithNames(ccMasterRepository.findAllByKeyStmtMonthYearAndUsername(monthYear, username));
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

	public List<AmountPerMonth> getAmountPerMonth(String year, String username) {
		return ccMasterRepository.getAmountPerMonthByUser(year, username);
	}

	public List<AmountPerMonth> getAmountPerCard(String year) {
		return ccMasterRepository.getAmountPerCard(year);
	}

	public List<AmountPerMonth> getAmountPerCard(String year, String username) {
		return ccMasterRepository.getAmountPerCardByUser(year, username);
	}

	public String getPendingPayments(String username) {
		StringBuffer sb = new StringBuffer();
		Integer pendingTotalAmt = 0;
		sb.append("******************************************************");
		sb.append(System.lineSeparator());
		sb.append("\t\t\tReport Generated on " + new SimpleDateFormat("dd-MMM-yyyy").format(new java.util.Date()));
		sb.append(System.lineSeparator());
		sb.append("******************************************************");
		sb.append(System.lineSeparator());
		sb.append(String.format("%30s %13s %6s", "Card Name |", "Due Date   |", "Total Amount"));
		sb.append(System.lineSeparator());
		sb.append("------------------------------------------------------");
		sb.append(System.lineSeparator());

		for (CCMaster ccMaster : sortListByDueDate(
				updateListWithNames(ccMasterRepository.findByUsernameAndCurrentStatusNot(username, "Paid")))) {
			pendingTotalAmt = pendingTotalAmt + ccMaster.getTotalAmt();
			sb.append(String.format("%30s %11s %6s", ccMaster.getName() + " |",
					new SimpleDateFormat("dd-MMM-yyyy").format(ccMaster.getDueDate()) + " |",
					NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(ccMaster.getTotalAmt()) + ""));
			sb.append(System.lineSeparator());
			sb.append("------------------------------------------------------");
			sb.append(System.lineSeparator());
		}
		sb.append("******************************************************");
		sb.append(System.lineSeparator());
		sb.append("\t\t\tTotal Amount Pending : "
				+ NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(pendingTotalAmt));
		sb.append(System.lineSeparator());
		sb.append("******************************************************");
		sb.append(System.lineSeparator());
		
		if(pendingTotalAmt == 0 ) {
			return "No pending payments for you as of today. Thank you.";
		}
		return sb.toString();
	}

	public void sendNotifications() {
		List<Users> users = userRepository.findAll();
		for (Users user : users) {
			new Thread(() -> {
				sendMailToUser(user.getUsername(), user.getEmail());
			}).start();
		}
	}

	private void sendMailToUser(String username, String email) {
		String emailBody = getPendingPayments(username);
		if (mailService.sendEmail("Credit Card Pending Payments", emailBody, email)) {
			log.info(emailBody);
			log.info("Mail sent successfully for user:" + username + " to email:" + email + " at "
					+ new java.util.Date());
		}
	}

	private void createNotifyRecord(String content, boolean flag) {
		CCMasterNotifications ccMasterNotify = new CCMasterNotifications();
		ccMasterNotify.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
		ccMasterNotify.setFlag(flag);
		ccMasterNotify.setContent(content);
		ccMasterNotificationsRepository.save(ccMasterNotify);
		log.info("New record inserted in Notifications table");
	}

	private Optional<CCMasterNotifications> getNotificationObject() {
		return ccMasterNotificationsRepository
				.findById(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
	}

	private List<CCMaster> sortListByDueDate(List<CCMaster> list) {
		return list.stream().sorted(Comparator.comparing(CCMaster::getDueDate)).collect(Collectors.toList());
	}

	public void dataBackup() {
		csvService.generateCSV(ccMasterRepository.findAll());
	}
}
