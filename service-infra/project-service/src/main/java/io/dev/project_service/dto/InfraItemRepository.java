package io.dev.project_service.dto;

import io.dev.project_service.entity.InfraItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface InfraItemRepository extends JpaRepository<InfraItem, String> {
    List<InfraItem> findByProjectId(String projectId);
}