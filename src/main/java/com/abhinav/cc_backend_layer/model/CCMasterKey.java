package com.abhinav.cc_backend_layer.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class CCMasterKey {
	private String code;
	private String stmtMonthYear;

}
