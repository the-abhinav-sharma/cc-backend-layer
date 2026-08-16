package com.abhinav.cc_backend_layer.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String appCode,   // e.g., "CARDIO_CARE" or "ZENCOUNTER"
    @NotBlank @Email String email,
    @NotBlank String username,
    @NotBlank String password
) {}