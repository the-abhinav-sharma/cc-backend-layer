package com.abhinav.cc_backend_layer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "CC_USERS")
public class Users {
	@Id
	private String username;
	private String password;
}
