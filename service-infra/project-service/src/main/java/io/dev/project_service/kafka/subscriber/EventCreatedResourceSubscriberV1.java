package io.dev.project_service.kafka.subscriber;

import io.dev.events.EventCreatedResourceV1;
import io.dev.project_service.services.InfraItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventCreatedResourceSubscriberV1 {

    private final static String TOPIC = "created.infra.event";

    private final InfraItemService infraItemService;

    @KafkaListener(topics = TOPIC, containerFactory = "createdKafkaListenerContainerFactory")
    public void eventCreateListenerV1(EventCreatedResourceV1 resourceV1) throws Exception {
        infraItemService.updateInfraItemEvent(resourceV1);
    }

}
