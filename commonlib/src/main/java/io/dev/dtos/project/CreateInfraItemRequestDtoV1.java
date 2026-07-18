package io.dev.dtos.project;

import io.dev.dtos.enums.InfraItemType;

import java.util.List;

public record CreateInfraItemRequestDtoV1(
        InfraItemType infraItemType,
        String projectId,
        List<SecretDtoV1> secrets
) {
}
