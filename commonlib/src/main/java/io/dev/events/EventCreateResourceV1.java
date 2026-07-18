package io.dev.events;

import io.dev.dtos.enums.InfraItemType;
import io.dev.dtos.project.SecretDtoV1;

import java.util.List;

public record EventCreateResourceV1(
        String id,
        InfraItemType infraItemType,
        List<SecretDtoV1> secrets
) {
}

