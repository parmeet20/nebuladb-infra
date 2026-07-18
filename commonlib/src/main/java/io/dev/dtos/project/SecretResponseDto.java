package io.dev.dtos.project;

public record SecretResponseDto(
        String id,
        String key,
        String value
) {
}
