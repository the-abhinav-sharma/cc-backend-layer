package com.abhinav.cc_backend_layer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.cc_backend_layer.model.Application;
import com.abhinav.cc_backend_layer.model.AuthResponse;
import com.abhinav.cc_backend_layer.model.LoginRequest;
import com.abhinav.cc_backend_layer.model.RegisterRequest;
import com.abhinav.cc_backend_layer.model.User;
import com.abhinav.cc_backend_layer.repository.ApplicationRepository;
import com.abhinav.cc_backend_layer.repository.CommonUserRepository;
import com.abhinav.cc_backend_layer.security.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CommonUserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(CommonUserRepository userRepository,
                          ApplicationRepository applicationRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // 1. Verify Application exists
        Application app = applicationRepository.findByAppCode(request.appCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid App Code: " + request.appCode()));

        // 2. Check for duplicate username or email within THIS application
        if (userRepository.existsByApplicationAppCodeAndUsername(request.appCode(), request.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken for this app.");
        }
        if (userRepository.existsByApplicationAppCodeAndEmail(request.appCode(), request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered for this app.");
        }

        // 3. Save User
        User user = User.builder()
                .application(app)
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        // 4. Generate JWT
        String token = jwtUtils.generateToken(user.getUsername(), app.getAppCode());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getId(), user.getUsername(), app.getAppCode()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // 1. Find user by username AND appCode
        User user = userRepository.findByApplicationAppCodeAndUsername(request.appCode(), request.username())
                .orElse(null);

        // 2. Verify password match
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }

        // 3. Generate JWT
        String token = jwtUtils.generateToken(user.getUsername(), request.appCode());
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getUsername(), request.appCode()));
    }
}