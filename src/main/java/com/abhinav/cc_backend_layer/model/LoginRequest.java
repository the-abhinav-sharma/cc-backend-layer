package com.abhinav.cc_backend_layer.model;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String appCode,
    @NotBlank String username,
    @NotBlank String password
) {}