package io.dev.project_service.entity;

import io.dev.dtos.enums.InfraItemStatus;
import io.dev.dtos.enums.InfraItemType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "infra_items",
        indexes = {
                @Index(name = "idx_infra_type", columnList = "infra_item_type"),
                @Index(name = "idx_infra_project", columnList = "project_id"),
                @Index(name = "idx_infra_url", columnList = "url")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_url_port",
                        columnNames = {
                                "project_id",
                                "url",
                                "port"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfraItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "infra_item_type", nullable = false)
    private InfraItemType infraItemType;

    private String url;

    private Integer port;
    String containerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "project_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_infra_project")
    )
    private Project project;

    @Enumerated(EnumType.STRING)
    private InfraItemStatus infraItemStatus;

    @Builder.Default
    @OneToMany(
            mappedBy = "infraItem",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Secrets> secrets = new ArrayList<>();

}