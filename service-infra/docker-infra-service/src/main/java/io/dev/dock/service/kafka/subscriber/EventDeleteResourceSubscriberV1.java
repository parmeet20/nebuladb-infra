package io.dev.dock.service.kafka.subscriber;

import io.dev.dock.service.DockerService;
import io.dev.events.EventDeleteResourceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventDeleteResourceSubscriberV1 {

    private static final String TOPIC = "event.infra.delete";

    private final DockerService dockerService;

    @KafkaListener(topics = TOPIC, containerFactory = "deleteKafkaListenerContainerFactory")
    public void eventDeleteListenerV1(EventDeleteResourceV1 eventDeleteResourceV1) {
        log.info(
                "Received delete resource event: id={}, containerId={}",
                eventDeleteResourceV1.id(),
                eventDeleteResourceV1.containerId()
        );
        dockerService.deleteContainer(eventDeleteResourceV1);
    }
}