package io.dev.events;

public record EventDeletedResourceV1(
        String id,
        String containerId,
        boolean success,
        String message
) {
}