package com.abhinav.cc_backend_layer.model;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity(name = "CC_MASTER")
public class CCMaster {
	@EmbeddedId
	private CCMasterKey key;
	private String name;
	private Double minAmt;
	private Double totalAmt;
	private Date dueDate;
	private Date payDate;
	private Double balanceAmt;
	private String currentStatus;
	private String remarks;
	private Timestamp createdOn;
	private Timestamp modifiedOn;
}
