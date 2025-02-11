package com.abhinav.cc_backend_layer.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abhinav.cc_backend_layer.model.UserSession;
import com.abhinav.cc_backend_layer.model.Users;
import com.abhinav.cc_backend_layer.repository.UserRepository;
import com.abhinav.cc_backend_layer.repository.UserSessionRepository;

@Component
public class LoginService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserSessionRepository userSessionRepository;

	public UserSession login(Users users) {
		logout(users.getUsername());
		String passwordFromUI = users.getPassword();
		Optional<Users> usersOP = userRepository.findById(users.getUsername());
		if (usersOP.isPresent() && usersOP.get().getPassword().equals(passwordFromUI)) {
			UserSession session = new UserSession();
			session.setUsername(usersOP.get().getUsername());
			session.setToken(UUID.randomUUID().toString().replace("-", ""));
			session.setLogintime(getCurrentTimestamp());
			session.setActive(true);
			return userSessionRepository.save(session);
		}
		return null;
	}

	public void logout(String username) {
		UserSession session = userSessionRepository.findTopByUsernameOrderBySessionidDesc(username);
		if (session != null && session.isActive()) {
			session.setLogofftime(getCurrentTimestamp());
			session.setActive(false);
			userSessionRepository.save(session);
		}
	}

	public Timestamp getCurrentTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
		return Timestamp.valueOf(sdf.format(new Date()));
	}

}
