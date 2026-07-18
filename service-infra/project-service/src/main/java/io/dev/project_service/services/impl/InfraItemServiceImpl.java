package io.dev.project_service.services.impl;

import io.dev.dtos.project.CreateInfraItemRequestDtoV1;
import io.dev.dtos.project.CreateInfraItemResponseDtoV1;
import io.dev.events.EventCreatedResourceV1;
import io.dev.events.EventDeleteResourceV1;
import io.dev.project_service.dto.InfraItemRepository;
import io.dev.project_service.dto.ProjectRepository;
import io.dev.project_service.entity.InfraItem;
import io.dev.project_service.entity.Project;
import io.dev.project_service.kafka.publisher.EventCreateResourcePublisherV1;
import io.dev.project_service.kafka.publisher.EventDeleteResourcePublisherV1;
import io.dev.project_service.mapper.InfraItemMapper;
import io.dev.project_service.services.InfraItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InfraItemServiceImpl implements InfraItemService {

    private final ProjectRepository projectRepository;
    private final InfraItemRepository infraItemRepository;
    private final EventCreateResourcePublisherV1 publisherV1;
    private final EventDeleteResourcePublisherV1 deletePublisherV1;

    @Override
    public CreateInfraItemResponseDtoV1 createInfraItem(CreateInfraItemRequestDtoV1 request) {

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Project not found with id: " + request.projectId()));

        InfraItem infraItem = InfraItemMapper.toEntity(request, project);

        InfraItem savedInfraItem = infraItemRepository.save(infraItem);

        publisherV1.publishCreateResourceEventV1(infraItem.getId(), request);

        return InfraItemMapper.toResponse(savedInfraItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreateInfraItemResponseDtoV1> getAllInfraItemsByProjectId(String projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found with id: " + projectId);
        }

        return infraItemRepository.findByProjectId(projectId)
                .stream()
                .map(InfraItemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void updateInfraItemEvent(EventCreatedResourceV1 event) {

        InfraItem infraItem = infraItemRepository.findById(event.id())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Infra item not found with id: " + event.id()
                        ));

        InfraItemMapper.updateFromCreatedEvent(infraItem, event);

        infraItemRepository.save(infraItem);
    }

    @Override
    public void deleteInfraItem(String infraItemId) {

        InfraItem infraItem = infraItemRepository.findById(infraItemId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Infra item not found with id: " + infraItemId));

        String containerId = infraItem.getContainerId();
        
        // Publish delete event with containerId to docker-infra-service
        deletePublisherV1.publishDeleteResourceEventV1(infraItemId, containerId);
        
        // Note: Actual database deletion happens in EventDeletedResourceSubscriberV1 
        // after successful container deletion
    }

    @Override
    public void deleteInfraItemInternal(String infraItemId) {
        if (infraItemRepository.existsById(infraItemId)) {
            infraItemRepository.deleteById(infraItemId);
        }
    }
}