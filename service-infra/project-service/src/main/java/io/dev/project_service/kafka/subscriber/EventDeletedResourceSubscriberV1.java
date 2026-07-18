package io.dev.project_service.kafka.subscriber;

import io.dev.events.EventDeletedResourceV1;
import io.dev.project_service.services.InfraItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventDeletedResourceSubscriberV1 {

    private final static String TOPIC = "deleted.infra.event";

    private final InfraItemService infraItemService;

    @KafkaListener(topics = TOPIC, containerFactory = "deletedKafkaListenerContainerFactory")
    public void eventDeletedListenerV1(EventDeletedResourceV1 event) throws Exception {
        log.info(
                "Received deleted resource event: id={}, containerId={}, success={}, message={}",
                event.id(),
                event.containerId(),
                event.success(),
                event.message()
        );
        
        if (event.success()) {
            infraItemService.deleteInfraItemInternal(event.id());
            log.info("Successfully deleted infra item from database: {}", event.id());
        } else {
            log.warn("Container deletion failed for infra item {}: {}", event.id(), event.message());
            // Optionally handle failure case - maybe update status to ERROR
        }
    }

}