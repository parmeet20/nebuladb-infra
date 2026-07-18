package io.dev.project_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "secrets",
        indexes = {
                @Index(name = "idx_secret_infra", columnList = "infra_item_id"),
                @Index(name = "idx_secret_key", columnList = "secret_key")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_secret_key",
                        columnNames = {"infra_item_id", "secret_key"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Secrets {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "secret_key", nullable = false, length = 100)
    private String key;

    @Column(name = "secret_value", nullable = false, length = 2000)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "infra_item_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_secret_infra")
    )
    private InfraItem infraItem;
}