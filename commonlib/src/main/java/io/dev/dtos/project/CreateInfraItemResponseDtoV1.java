package io.dev.dtos.project;

import io.dev.dtos.enums.InfraItemStatus;
import io.dev.dtos.enums.InfraItemType;

import java.util.List;

public record CreateInfraItemResponseDtoV1(
        String id,
        InfraItemType infraItemType,
        String projectId,
        InfraItemStatus status,
        String url,
        String containerId,
        Integer port,
        List<SecretResponseDto> secrets
) {
}