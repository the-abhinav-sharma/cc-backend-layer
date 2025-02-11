package com.abhinav.cc_backend_layer.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "CC_USERS_SESSION")
public class UserSession {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Integer sessionid;
	private String username;
	private String token;
	@JsonIgnore
	private Timestamp logintime;
	@JsonIgnore
	private Timestamp logofftime;
	@JsonIgnore
	private boolean active;
}
