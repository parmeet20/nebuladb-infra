package io.dev.project_service.services.impl;

import io.dev.dtos.project.CreateProjectRequestDtoV1;
import io.dev.dtos.project.CreateProjectResponseDtoV1;
import io.dev.project_service.dto.ProjectRepository;
import io.dev.project_service.entity.Project;
import io.dev.project_service.exceptions.ProjectAccessDeniedException;
import io.dev.project_service.mapper.ProjectMapper;
import io.dev.project_service.services.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public CreateProjectResponseDtoV1 createProject(
            CreateProjectRequestDtoV1 dtoV1
    ) {
        Project project = ProjectMapper.toEntity(dtoV1);
        Project savedProject = projectRepository.save(project);
        log.info("Created project id={} for ownerId={}", savedProject.getId(), savedProject.getOwnerId());
        return ProjectMapper.toProjectResponseDto(savedProject);
    }

    @Override
    public CreateProjectResponseDtoV1 updateProject(
            String projectId,
            String userId,
            CreateProjectRequestDtoV1 dtoV1
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Project not found with id: " + projectId
                        ));

        if (!project.getOwnerId().equals(userId)) {
            log.warn("Unauthorized update attempt: userId={} on projectId={}", userId, projectId);
            throw new ProjectAccessDeniedException(
                    "You do not have permission to update this project"
            );
        }

        ProjectMapper.updateEntity(project, dtoV1);
        log.info("Updated project id={}", projectId);
        return ProjectMapper.toProjectResponseDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreateProjectResponseDtoV1> getAllMyProjects(
            String ownerId
    ) {
        return projectRepository.findByOwnerId(ownerId)
                .stream()
                .map(ProjectMapper::toProjectResponseDto)
                .toList();
    }

    @Override
    public void deleteProject(String projectId, String userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Project not found with id: " + projectId
                        ));

        if (!project.getOwnerId().equals(userId)) {
            log.warn("Unauthorized delete attempt: userId={} on projectId={}", userId, projectId);
            throw new ProjectAccessDeniedException(
                    "You do not have permission to delete this project"
            );
        }

        projectRepository.deleteById(projectId);
        log.info("Deleted project id={}", projectId);
    }
}