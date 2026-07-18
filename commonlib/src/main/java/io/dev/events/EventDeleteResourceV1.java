package io.dev.events;

public record EventDeleteResourceV1(
        String id,
        String containerId
) {
}