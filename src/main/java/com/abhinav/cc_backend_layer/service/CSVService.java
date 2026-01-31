package com.abhinav.cc_backend_layer.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterCSV;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CSVService {

	@Autowired
	MailService mailService;

	public boolean generateCSV(List<CCMaster> masterList) {
		Iterator<CCMaster> it = masterList.iterator();
		String finalPath = "CC_" + getCurrentTimestamp("ddMMyyyy_HHmmss") + ".csv";
		File file = new File(finalPath);
		BufferedWriter writer = null;
		FileWriter fr = null;
		try {
			fr = new FileWriter(file);
			writer = new BufferedWriter(fr);
			StatefulBeanToCsv<CCMasterCSV> beanToCsv = new StatefulBeanToCsvBuilder<CCMasterCSV>(writer)
					.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).build();
			while (it.hasNext()) {
				CCMaster ccMaster = it.next();
				CCMasterCSV ccMasterCSV = new CCMasterCSV();
				ccMasterCSV.setCode(ccMaster.getKey().getCode());
				ccMasterCSV.setStmtMonthYear(ccMaster.getKey().getStmtMonthYear());
				ccMasterCSV.setUsername(ccMaster.getKey().getUsername());
				ccMasterCSV.setName(ccMaster.getName());
				ccMasterCSV.setMinAmt(ccMaster.getMinAmt());
				ccMasterCSV.setTotalAmt(ccMaster.getTotalAmt());
				ccMasterCSV.setStmtDate(ccMaster.getStmtDate());
				ccMasterCSV.setDueDate(ccMaster.getDueDate());
				ccMasterCSV.setPayDate(ccMaster.getPayDate());
				ccMasterCSV.setCurrentStatus(ccMaster.getCurrentStatus());
				ccMasterCSV.setRemarks(ccMaster.getRemarks());
				ccMasterCSV.setCreatedOn(ccMaster.getCreatedOn());
				ccMasterCSV.setModifiedOn(ccMaster.getModifiedOn());

				beanToCsv.write(ccMasterCSV);
			}
		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException ex) {
			log.info(ex.getStackTrace().toString());
			return false;
		} finally {
			try {
				writer.close();
				fr.close();
				mailService.sendMailWithAttachment("CC Data Backup!", "Backup taken at " + getCurrentTimestamp("dd-MM-yyyy HH:mm:ss"), file);
				file.delete();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public String getCurrentTimestamp(String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		return sdf.format(new Date());
	}

}