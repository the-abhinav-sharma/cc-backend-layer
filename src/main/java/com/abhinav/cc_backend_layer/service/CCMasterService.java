package com.abhinav.cc_backend_layer.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
		ccMaster.getKey().setUsername(username);
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
		return sortListByDueDate(updateListWithNames(ccMasterRepository.findAllByKeyUsername(username)));
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
		key.setUsername(username);

		Optional<CCMaster> optCCMaster = ccMasterRepository.findById(key);
		if (optCCMaster.isEmpty()) {
			return null;
		} else {
			if (optCCMaster.get().getKey().getUsername().equalsIgnoreCase(username)) {
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

//	public String getPendingPayments(String username) {
//		StringBuffer sb = new StringBuffer();
//		Integer pendingTotalAmt = 0;
//		sb.append("******************************************************");
//		sb.append(System.lineSeparator());
//		sb.append("\t\t\tReport Generated on " + new SimpleDateFormat("dd-MMM-yyyy").format(new java.util.Date()));
//		sb.append(System.lineSeparator());
//		sb.append("******************************************************");
//		sb.append(System.lineSeparator());
//		sb.append(String.format("%30s %13s %6s", "Card Name |", "Due Date   |", "Total Amount"));
//		sb.append(System.lineSeparator());
//		sb.append("------------------------------------------------------");
//		sb.append(System.lineSeparator());
//
//		for (CCMaster ccMaster : sortListByDueDate(
//				updateListWithNames(ccMasterRepository.findByUsernameAndCurrentStatusNot(username, "Paid")))) {
//			pendingTotalAmt = pendingTotalAmt + ccMaster.getTotalAmt();
//			sb.append(String.format("%30s %11s %6s", ccMaster.getName() + " |",
//					new SimpleDateFormat("dd-MMM-yyyy").format(ccMaster.getDueDate()) + " |",
//					NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(ccMaster.getTotalAmt()) + ""));
//			sb.append(System.lineSeparator());
//			sb.append("------------------------------------------------------");
//			sb.append(System.lineSeparator());
//		}
//		sb.append("******************************************************");
//		sb.append(System.lineSeparator());
//		sb.append("\t\t\tTotal Amount Pending : "
//				+ NumberFormat.getCurrencyInstance(new Locale("en", "IN")).format(pendingTotalAmt));
//		sb.append(System.lineSeparator());
//		sb.append("******************************************************");
//		sb.append(System.lineSeparator());
//		
//		if(pendingTotalAmt == 0 ) {
//			return "No pending payments for you as of today. Thank you.";
//		}
//		return sb.toString();
//	}

	public String getPendingPayments(String username) {

	    List<CCMaster> list = updateListWithNames(
	            ccMasterRepository.findByUsernameAndCurrentStatusNot(username, "Paid")
	    );

	    if (list.isEmpty()) {
	        return "<p style='font-family:Arial,sans-serif;font-size:16px;'>"
	                + "No pending payments for you as of today. Thank you.</p>";
	    }

	    // Sort by urgency: earliest due first
	    list.sort(Comparator.comparing(CCMaster::getDueDate));

	    int pendingTotalAmt = 0;
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

	    StringBuilder sb = new StringBuilder();

	    sb.append("<html><head>");
	    // CSS to force iOS/Outlook data detectors to inherit your text styles
	    sb.append("<style>");
	    sb.append("a[x-apple-data-detectors] { color: inherit !important; text-decoration: none !important; font-size: inherit !important; font-family: inherit !important; font-weight: inherit !important; line-height: inherit !important; }");
	    sb.append("</style>");
	    sb.append("</head>");
	    
	    sb.append("<body style='margin:0;padding:10px;font-family:Arial,sans-serif;'>");

	    sb.append("<h2 style='text-align:center;margin-bottom:5px;'>Pending Payments</h2>");
	    sb.append("<p style='text-align:center;font-size:14px;color:#555;margin-top:0;'>")
	      .append("Report Generated on ").append(dateFormat.format(new java.util.Date()))
	      .append("</p>");

	    sb.append("<table width='100%' cellpadding='5' cellspacing='0' ")
	      .append("style='border-collapse:collapse;border:1px solid #ddd;font-size:15px;'>");

	    sb.append("<tr style='background-color:#f4f4f4;'>")
	      .append("<th align='left' style='padding:8px;'>Card</th>")
	      .append("<th align='left' style='padding:8px;'>Due Date</th>")
	      .append("<th align='left' style='padding:8px;'>Status</th>")
	      .append("<th align='right' style='padding:8px;'>Amount</th>")
	      .append("</tr>");

	    LocalDate today = LocalDate.now();

	    for (CCMaster ccMaster : list) {
	            if (ccMaster.getDueDate() == null || ccMaster.getTotalAmt() == null || ccMaster.getName() == null) {
	                continue;
	            }

	            pendingTotalAmt += ccMaster.getTotalAmt();
	            LocalDate dueDate = ccMaster.getDueDate().toLocalDate();
	            long daysLeft = ChronoUnit.DAYS.between(today, dueDate);

	            String statusText;
	            String statusColor;

	            if (daysLeft < 0) {
	                statusText = "Overdue by " + Math.abs(daysLeft) + " days";
	                statusColor = "#c62828";
	            } else if (daysLeft == 0) {
	                statusText = "Due today";
	                statusColor = "#c62828";
	            } else if (daysLeft <= 3) {
	                statusText = "Due in " + daysLeft + " days";
	                statusColor = "#ef6c00";
	            } else {
	                statusText = "Due in " + daysLeft + " days";
	                statusColor = "#2e7d32";
	            }

	            // Neutralize status text for Outlook
	            String neutralizedStatus = statusText.replace("Due", "Du&#8204;e").replace("days", "da&#8204;ys");

	            sb.append("<tr>");
	            sb.append("<td style='max-width:250px;word-wrap:break-word;padding:8px;'>").append(ccMaster.getName()).append("</td>");

	            // Date Column
	            String antiLinkDate = dateFormat.format(ccMaster.getDueDate()).replaceFirst("-", "-&#8204;");
	            sb.append("<td style='white-space:nowrap;padding:8px;color:#333333;'><span style='color:#333333;text-decoration:none;'>")
	              .append(antiLinkDate).append("</span></td>");

	            // Status Column
	            sb.append("<td style='padding:8px;'>")
	              .append("<span style='color:").append(statusColor).append(";font-weight:bold;'>")
	              .append("<font color='").append(statusColor).append("'>")
	              .append(neutralizedStatus)
	              .append("</font></span></td>");

	            // Amount Column
	            sb.append("<td align='right' style='font-weight:bold;padding:8px;'>").append(currencyFormat.format(ccMaster.getTotalAmt())).append("</td>");
	            sb.append("</tr>");
	    }

	    // Total row
	    sb.append("<tr style='background-color:#e8f5e9;font-weight:bold;'>")
	      .append("<td colspan='3' align='right' style='padding:8px;'>Total Pending</td>")
	      .append("<td align='right' style='color:#2e7d32;padding:8px;'>")
	      .append(currencyFormat.format(pendingTotalAmt))
	      .append("</td>")
	      .append("</tr>");

	    sb.append("</table>");
	    sb.append("</body></html>");

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
