package io.dev.dock.config;

import io.dev.dtos.enums.InfraItemType;

import java.util.Map;

public final class DockerImageRegistry {

    private DockerImageRegistry() {}

    private static final Map<InfraItemType, String> IMAGES = Map.of(
            InfraItemType.MONGO_DB, "mongo:8.0",
            InfraItemType.POSTGRES_DB, "postgres:17",
            InfraItemType.MYSQL_DB, "mysql:latest",
            InfraItemType.MARIA_DB, "mariadb:11",
            InfraItemType.REDIS_DB, "redis:7.4",
            InfraItemType.KAFKA_MQ, "apache/kafka-native:latest",
            InfraItemType.RABBIT_MQ, "rabbitmq:4-management"
    );


    public static String getImage(InfraItemType type) {
        String image = IMAGES.get(type);

        if (image == null) {
            throw new IllegalArgumentException(
                    "Docker image not configured for: " + type
            );
        }

        return image;
    }
}