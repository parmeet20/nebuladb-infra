package io.dev.dtos.response;

import java.time.Instant;

public record ApiErrorResponse(Instant instant, String message, Integer statusCode) {}