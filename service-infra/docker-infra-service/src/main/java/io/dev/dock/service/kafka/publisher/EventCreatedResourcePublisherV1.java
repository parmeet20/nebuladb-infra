package io.dev.dock.service.kafka.publisher;

import io.dev.events.EventCreatedResourceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventCreatedResourcePublisherV1 {

    private static final String TOPIC = "created.infra.event";

    private final KafkaTemplate<String, EventCreatedResourceV1> kafkaTemplate;

    public void publishCreatedResourceEventV1(EventCreatedResourceV1 event) {

        CompletableFuture<SendResult<String, EventCreatedResourceV1>> future =
                kafkaTemplate.send(
                        TOPIC,
                        event.id(),
                        event
                );

        future.whenComplete((result, ex) -> {

            if (ex != null) {
                log.error(
                        "Failed to publish event. topic={}, infraItemId={}, error={}",
                        TOPIC,
                        event.id(),
                        ex.getMessage(),
                        ex
                );
            } else {
                log.info(
                        "Successfully published event. topic={}, partition={}, offset={}, infraItemId={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.id()
                );
            }

        });
    }
}