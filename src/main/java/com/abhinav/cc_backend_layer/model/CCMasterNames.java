package com.abhinav.cc_backend_layer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "CC_MASTER_NAMES")
public class CCMasterNames {
	@Id
	private String code;
	private String name;
}
