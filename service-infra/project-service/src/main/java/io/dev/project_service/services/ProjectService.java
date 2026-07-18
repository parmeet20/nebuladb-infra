package io.dev.project_service.services;

import io.dev.dtos.project.CreateProjectRequestDtoV1;
import io.dev.dtos.project.CreateProjectResponseDtoV1;

import java.util.List;

public interface ProjectService {
    CreateProjectResponseDtoV1 createProject(CreateProjectRequestDtoV1 dtoV1) throws Exception;
    CreateProjectResponseDtoV1 updateProject(String projectId, String userId, CreateProjectRequestDtoV1 dtoV1) throws Exception;
    List<CreateProjectResponseDtoV1> getAllMyProjects(String ownerId) throws Exception;
    void deleteProject(String projectId, String userId) throws Exception;
}
