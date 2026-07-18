package io.dev.dtos.project;

public record CreateProjectRequestDtoV1(
        String name,
        String description,
        String ownerId
) {}