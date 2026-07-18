package io.dev.dock.service.kafka.publisher;

import io.dev.events.EventDeletedResourceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventDeletedResourcePublisherV1 {

    private static final String TOPIC = "deleted.infra.event";

    private final KafkaTemplate<String, EventDeletedResourceV1> kafkaTemplate;

    public void publishDeletedResourceEventV1(EventDeletedResourceV1 event) {

        CompletableFuture<SendResult<String, EventDeletedResourceV1>> future =
                kafkaTemplate.send(
                        TOPIC,
                        event.id(),
                        event
                );

        future.whenComplete((result, ex) -> {

            if (ex != null) {
                log.error(
                        "Failed to publish deleted event. topic={}, infraItemId={}, error={}",
                        TOPIC,
                        event.id(),
                        ex.getMessage(),
                        ex
                );
            } else {
                log.info(
                        "Successfully published deleted event. topic={}, partition={}, offset={}, infraItemId={}, success={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.id(),
                        event.success()
                );
            }

        });
    }
}