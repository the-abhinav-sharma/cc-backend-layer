package com.abhinav.cc_backend_layer.model;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity(name = "CC_MASTER_TEST")
public class CCMaster {
	@EmbeddedId
	private CCMasterKey key;
	@Transient
	private String name;
	private Double minAmt;
	private Double totalAmt;
	private Date stmtDate;
	private Date dueDate;
	private Date payDate;
	//private Double balanceAmt;
	private String currentStatus;
	private String remarks;
	private Timestamp createdOn;
	private Timestamp modifiedOn;
}
