package io.dev.project_service.dto;

import io.dev.project_service.entity.Secrets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretsRepository extends JpaRepository<Secrets, String> {
}