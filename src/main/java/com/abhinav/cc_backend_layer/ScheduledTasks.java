package com.abhinav.cc_backend_layer;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterKey;
import com.abhinav.cc_backend_layer.model.EmployeeService;
import com.abhinav.cc_backend_layer.service.CCMasterService;
import com.abhinav.cc_backend_layer.service.CSVService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ScheduledTasks {

	@Autowired
	CCMasterService ccMasterService;
	
	@Autowired
	CSVService csvService;
	
	@Autowired
	EmployeeService employeeService;

	@Scheduled(cron = "0 30 23 * * *", zone = "Asia/Kolkata")
	public void execute() {
		ccMasterService.sendNotifications();
	}
	
	@Scheduled(cron = "0 10 0 * * *", zone = "Asia/Kolkata")
	public void insertDailyRecord() {
		employeeService.insertDailyRecord();
	}
	
	@Scheduled(cron = "0 35 23 * * 0", zone = "Asia/Kolkata")
	public void backup() {
		ccMasterService.dataBackup();
	}

	@Scheduled(cron = "0 0 23 1 * *", zone = "Asia/Kolkata")
	public void insertOnFirstEveryMonth() {
		CCMaster ccMaster = new CCMaster();
		CCMasterKey key = new CCMasterKey();
		key.setCode("PNB02");
		key.setStmtMonthYear(getStmtMonthYear());

		ccMaster.setMinAmt(13000);
		ccMaster.setTotalAmt(13000);
		ccMaster.setStmtDate(new java.sql.Date(System.currentTimeMillis()));
		ccMaster.setDueDate(getDueDate());
		ccMaster.setCurrentStatus("Bill Generated");

		ccMaster.setKey(key);
		log.info("PNB02 Record inserted:" + ccMasterService.create(ccMaster));
	}

	private String getStmtMonthYear() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
		cal.setTime(new Date());

		int month = cal.get(Calendar.MONTH) + 1;
		String monthStr = String.valueOf(month);

		if (monthStr.length() == 1) {
			monthStr = "0" + monthStr;
		}

		return monthStr + cal.get(Calendar.YEAR);
	}

	private java.sql.Date getDueDate() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
		calendar.setTime(new Date(System.currentTimeMillis()));
		calendar.add(Calendar.DAY_OF_MONTH, 20);
		return new java.sql.Date(calendar.getTimeInMillis());
	}
}