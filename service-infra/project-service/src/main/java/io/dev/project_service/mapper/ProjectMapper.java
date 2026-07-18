package io.dev.project_service.mapper;

import io.dev.dtos.project.CreateProjectRequestDtoV1;
import io.dev.dtos.project.CreateProjectResponseDtoV1;
import io.dev.project_service.entity.Project;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static Project toEntity(CreateProjectRequestDtoV1 dto) {
        return Project.builder()
                .name(dto.name())
                .description(dto.description())
                .ownerId(dto.ownerId())
                .build();
    }

    public static CreateProjectResponseDtoV1 toProjectResponseDto(Project project) {
        return new CreateProjectResponseDtoV1(
                project.getId(),
                project.getName(),
                project.getDescription()
        );
    }

    public static void updateEntity(Project project, CreateProjectRequestDtoV1 dto) {
        project.setName(dto.name());
        project.setDescription(dto.description());
    }
}