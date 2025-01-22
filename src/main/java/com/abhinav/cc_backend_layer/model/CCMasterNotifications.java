package com.abhinav.cc_backend_layer.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "CC_MASTER_NOTIFICATIONS")
public class CCMasterNotifications {
	@Id
	private String id;
	private Date date;
	private boolean flag;
	private String content;

}
