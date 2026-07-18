package io.dev.project_service.mapper;

import io.dev.dtos.enums.InfraItemStatus;
import io.dev.dtos.enums.InfraItemType;
import io.dev.dtos.project.CreateInfraItemRequestDtoV1;
import io.dev.dtos.project.CreateInfraItemResponseDtoV1;
import io.dev.dtos.project.SecretResponseDto;
import io.dev.events.EventCreatedResourceV1;
import io.dev.project_service.entity.InfraItem;
import io.dev.project_service.entity.Project;
import io.dev.project_service.entity.Secrets;

import java.util.ArrayList;
import java.util.List;

public final class InfraItemMapper {

    private InfraItemMapper() {
    }

    public static InfraItem toEntity(
            CreateInfraItemRequestDtoV1 dto,
            Project project
    ) {

        InfraItem infraItem = InfraItem.builder()
                .infraItemType(dto.infraItemType())
                .infraItemStatus(InfraItemStatus.CREATING)
                .project(project)
                .secrets(new ArrayList<>())
                .build();

        if (dto.secrets() != null) {
            dto.secrets().forEach(secretDto -> {
                Secrets secret = Secrets.builder()
                        .key(secretDto.key())
                        .value(secretDto.value())
                        .infraItem(infraItem)
                        .build();

                infraItem.getSecrets().add(secret);
            });
        }

        return infraItem;
    }

    public static CreateInfraItemResponseDtoV1 toResponse(InfraItem infraItem) {

        List<SecretResponseDto> secrets = infraItem.getSecrets()
                .stream()
                .map(secret -> new SecretResponseDto(
                        secret.getId(),
                        secret.getKey(),
                        secret.getValue()
                ))
                .toList();

        return new CreateInfraItemResponseDtoV1(
                infraItem.getId(),
                infraItem.getInfraItemType(),
                infraItem.getProject().getId(),
                infraItem.getInfraItemStatus(),
                infraItem.getUrl(),
                infraItem.getContainerId(),
                infraItem.getPort(),
                secrets
        );
    }
    public static void updateFromCreatedEvent(
            InfraItem infraItem,
            EventCreatedResourceV1 event
    ) {
        infraItem.setUrl(event.url());
        infraItem.setPort(event.port());
        infraItem.setContainerId(event.containerId());
        infraItem.setInfraItemStatus(event.status());
    }
}