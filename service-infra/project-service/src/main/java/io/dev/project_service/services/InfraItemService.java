package io.dev.project_service.services;

import io.dev.dtos.project.CreateInfraItemRequestDtoV1;
import io.dev.dtos.project.CreateInfraItemResponseDtoV1;
import io.dev.events.EventCreatedResourceV1;

import java.util.List;

public interface InfraItemService {

    CreateInfraItemResponseDtoV1 createInfraItem(CreateInfraItemRequestDtoV1 request) throws Exception;

    List<CreateInfraItemResponseDtoV1> getAllInfraItemsByProjectId(String projectId) throws Exception;

    void updateInfraItemEvent(EventCreatedResourceV1 event) throws Exception;

    void deleteInfraItem(String infraItemId) throws Exception;

    void deleteInfraItemInternal(String infraItemId) throws Exception;
}