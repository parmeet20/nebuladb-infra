package io.dev.dock.service.kafka.subscriber;

import io.dev.dock.service.DockerService;
import io.dev.events.EventCreateResourceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventCreateResourceSubscriberV1 {

    private static final String TOPIC = "event.infra.create";

    private final DockerService dockerService;

    @KafkaListener(topics = TOPIC, containerFactory = "createKafkaListenerContainerFactory")
    public void eventCreateListenerV1(EventCreateResourceV1 eventCreateResourceV1)
            throws InterruptedException {
        log.info(
                "Received create resource event: id={}, infraItemType={}, secrets={}",
                eventCreateResourceV1.id(),
                eventCreateResourceV1.infraItemType(),
                eventCreateResourceV1.secrets()
        );
        dockerService.createContainer(eventCreateResourceV1);
    }
}