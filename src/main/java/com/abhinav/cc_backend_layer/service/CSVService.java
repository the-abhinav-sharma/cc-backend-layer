package com.abhinav.cc_backend_layer.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterCSV;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Component
public class CSVService {

	@Autowired
	CCMasterService ccMasterService;

	@Autowired
	MailService mailService;

	public int generateCSV() {
		Iterator<CCMaster> it = getAll();
		String finalPath = "CC_" + getDateTimeToday() + ".csv";
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
			System.out.println(ex.getStackTrace().toString());
			return 1;
		} finally {
			try {
				writer.close();
				fr.close();
				mailService.sendMailWithAttachment("CC Data Backup!", "Backup taken at " + new Date(), file);
				file.delete();
				return 0;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}

	public Iterator<CCMaster> getAll() {
		return ccMasterService.getAll().iterator();
	}
	
	public static String getDateToday() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		return dateFormat.format(new Date());
	}

	public static String getDateTimeToday() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");
		return dateFormat.format(new Date());
	}

}