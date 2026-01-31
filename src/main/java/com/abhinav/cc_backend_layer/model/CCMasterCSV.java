package com.abhinav.cc_backend_layer.model;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class CCMasterCSV {
	private String code;
	private String stmtMonthYear;
	private String username;
	private String name;
	private Integer minAmt;
	private Integer totalAmt;
	private Date stmtDate;
	private Date dueDate;
	private Date payDate;
	private String currentStatus;
	private String remarks;
	private Timestamp createdOn;
	private Timestamp modifiedOn;
}
