package io.securityapp.nebula_auth.dtos;

public record MeResponseDto(
        String username,
        String userId,
        String email
) {}