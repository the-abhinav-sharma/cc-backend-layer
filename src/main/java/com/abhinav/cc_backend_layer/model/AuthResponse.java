package com.abhinav.cc_backend_layer.model;

public record AuthResponse(
    String token,
    Long userId,
    String username,
    String appCode
) {}