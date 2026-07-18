package io.securityapp.nebula_auth.dtos;

public record LoginRequestDto (
        String username,
        String password
){}