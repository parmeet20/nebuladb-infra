package io.dev.dock.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import io.dev.dock.config.DockerImageRegistry;
import io.dev.dock.service.kafka.publisher.EventCreatedResourcePublisherV1;
import io.dev.dock.service.kafka.publisher.EventDeletedResourcePublisherV1;
import io.dev.dtos.enums.InfraItemStatus;
import io.dev.dtos.enums.InfraItemType;
import io.dev.events.EventCreateResourceV1;
import io.dev.events.EventCreatedResourceV1;
import io.dev.events.EventDeleteResourceV1;
import io.dev.events.EventDeletedResourceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DockerService {


    private final DockerClient dockerClient;
    private final EventCreatedResourcePublisherV1 publisherV1;
    private final EventDeletedResourcePublisherV1 deletedPublisherV1;


    public void createContainer(
            EventCreateResourceV1 event
    ) throws InterruptedException {


        InfraItemType type = event.infraItemType();


        String image = DockerImageRegistry.getImage(event.infraItemType());


        if(image == null){
            throw new IllegalArgumentException(
                    "No docker image configured for "+type
            );
        }


        // Pull Image

        dockerClient.pullImageCmd(image.split(":")[0])
                .withTag(image.split(":")[1])
                .start()
                .awaitCompletion();



        String containerName =
                type.name().toLowerCase()
                        +"-"+ UUID.randomUUID();



        ExposedPort exposedPort =
                ExposedPort.tcp(
                        getDefaultPort(type)
                );


        HostConfig hostConfig =
                HostConfig.newHostConfig()
                        .withPortBindings(
                                new PortBinding(
                                        Ports.Binding.empty(),
                                        exposedPort
                                )
                        );



        List<String> env =
                buildEnvironment(type,event);



        CreateContainerResponse container =
                dockerClient.createContainerCmd(image)
                        .withName(containerName)
                        .withExposedPorts(exposedPort)
                        .withEnv(env)
                        .withHostConfig(hostConfig)
                        .exec();



        dockerClient.startContainerCmd(
                container.getId()
        ).exec();



        Thread.sleep(3000);



        InspectContainerResponse inspect =
                dockerClient.inspectContainerCmd(
                        container.getId()
                ).exec();



        String hostPort =
                inspect
                        .getNetworkSettings()
                        .getPorts()
                        .getBindings()
                        .get(exposedPort)[0]
                        .getHostPortSpec();


        publisherV1.publishCreatedResourceEventV1(new EventCreatedResourceV1(event.id(), "localhost", Integer.valueOf(hostPort),container.getId(), InfraItemStatus.UP));
    }



    private int getDefaultPort(
            InfraItemType type
    ){

        return switch(type){

            case MYSQL_DB -> 3306;
            case POSTGRES_DB -> 5432;
            case MARIA_DB -> 3306;
            case MONGO_DB -> 27017;
            case REDIS_DB -> 6379;
            case KAFKA_MQ -> 9092;
            case RABBIT_MQ -> 5672;

        };

    }



    private List<String> buildEnvironment(
            InfraItemType type,
            EventCreateResourceV1 event
    ){


        return switch(type){


            case MYSQL_DB,
                 MARIA_DB -> List.of(
                    "MYSQL_ROOT_PASSWORD=password",
                    "MYSQL_DATABASE=nebula"
            );


            case POSTGRES_DB -> List.of(
                    "POSTGRES_PASSWORD=password",
                    "POSTGRES_DB=nebula"
            );


            case MONGO_DB -> List.of(
                    "MONGO_INITDB_ROOT_USERNAME=root",
                    "MONGO_INITDB_ROOT_PASSWORD=password"
            );


            default -> List.of();

        };


    }

    public void deleteContainer(
            EventDeleteResourceV1 event
    ) {
        String containerId = event.containerId();
        String infraItemId = event.id();

        log.info("Deleting container {} for infraItemId={}", containerId, infraItemId);

        try {
            // Stop container first
            dockerClient.stopContainerCmd(containerId).exec();
            
            // Remove container
            dockerClient.removeContainerCmd(containerId).exec();
            
            log.info("Successfully deleted container {} for infraItemId={}", containerId, infraItemId);
            
            // Publish deleted event
            deletedPublisherV1.publishDeletedResourceEventV1(
                new EventDeletedResourceV1(infraItemId, containerId, true, "Container deleted successfully")
            );
            
        } catch (Exception e) {
            log.error("Failed to delete container {} for infraItemId={}: {}", containerId, infraItemId, e.getMessage());
            deletedPublisherV1.publishDeletedResourceEventV1(
                new EventDeletedResourceV1(infraItemId, containerId, false, "Failed to delete container: " + e.getMessage())
            );
            throw new RuntimeException("Failed to delete container: " + e.getMessage(), e);
        }
    }

}