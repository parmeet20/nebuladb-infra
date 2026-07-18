package io.securityapp.nebula_auth.dtos;

public record SignupRequestDto (
        String username,
        String password
){}