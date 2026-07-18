package io.securityapp.nebula_auth.error;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ApiErrorResponse(
        Instant timestamp,
        String error,
        String message,
        HttpStatus status
) {
}