package io.dev.events;

import io.dev.dtos.enums.InfraItemStatus;

public record EventCreatedResourceV1(
        String id,
        String url,
        Integer port,
        String containerId,
        InfraItemStatus status
) {
}
