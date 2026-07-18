package io.dev.project_service.controller;

import io.dev.dtos.project.CreateInfraItemRequestDtoV1;
import io.dev.dtos.project.CreateInfraItemResponseDtoV1;
import io.dev.project_service.services.InfraItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/infra-items")
@RequiredArgsConstructor
@Slf4j
public class InfraItemController {

    private final InfraItemService infraItemService;

    @PostMapping
    public ResponseEntity<CreateInfraItemResponseDtoV1> createInfraItem(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateInfraItemRequestDtoV1 request
    ) throws Exception {

        log.info("Creating infra item for userId={}, projectId={}, type={}",
                userId, request.projectId(), request.infraItemType());

        CreateInfraItemResponseDtoV1 response =
                infraItemService.createInfraItem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<CreateInfraItemResponseDtoV1>> getAllByProject(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String projectId
    ) throws Exception {

        log.info("Fetching infra items for projectId={}, userId={}", projectId, userId);

        return ResponseEntity.ok(
                infraItemService.getAllInfraItemsByProjectId(projectId)
        );
    }

    @DeleteMapping("/{infraItemId}")
    public ResponseEntity<Void> deleteInfraItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String infraItemId
    ) throws Exception {

        log.info("Deleting infraItemId={} for userId={}", infraItemId, userId);
        infraItemService.deleteInfraItem(infraItemId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}