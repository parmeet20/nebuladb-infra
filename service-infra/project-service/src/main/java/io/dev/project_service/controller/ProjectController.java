package io.dev.project_service.controller;

import io.dev.dtos.project.CreateProjectRequestDtoV1;
import io.dev.dtos.project.CreateProjectResponseDtoV1;
import io.dev.project_service.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Create a new project. The ownerId is extracted from the X-User-Id header
     * set by the API Gateway after validating the JWT token — never trust the client body.
     */
    @PostMapping
    public ResponseEntity<CreateProjectResponseDtoV1> createProject(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateProjectRequestDtoV1 request
    ) throws Exception {

        log.info("Creating project for userId={}", userId);

        // Override any ownerId in the request body with the authenticated user's id
        CreateProjectRequestDtoV1 securedRequest = new CreateProjectRequestDtoV1(
                request.name(),
                request.description(),
                userId
        );

        CreateProjectResponseDtoV1 response = projectService.createProject(securedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<CreateProjectResponseDtoV1> updateProject(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String projectId,
            @Valid @RequestBody CreateProjectRequestDtoV1 request
    ) throws Exception {

        log.info("Updating projectId={} for userId={}", projectId, userId);

        CreateProjectResponseDtoV1 response =
                projectService.updateProject(projectId, userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CreateProjectResponseDtoV1>> getMyProjects(
            @RequestHeader("X-User-Id") String userId
    ) throws Exception {

        log.info("Fetching projects for userId={}", userId);
        return ResponseEntity.ok(projectService.getAllMyProjects(userId));
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String projectId
    ) throws Exception {

        log.info("Deleting projectId={} for userId={}", projectId, userId);
        projectService.deleteProject(projectId, userId);
    }
}