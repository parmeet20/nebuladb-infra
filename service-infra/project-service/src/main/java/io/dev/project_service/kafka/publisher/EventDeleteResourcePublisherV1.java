package io.dev.project_service.kafka.publisher;

import io.dev.events.EventDeleteResourceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventDeleteResourcePublisherV1 {

    private static final String TOPIC = "event.infra.delete";

    private final KafkaTemplate<String, EventDeleteResourceV1> kafkaTemplate;

    public void publishDeleteResourceEventV1(String infraItemId, String containerId) {

        EventDeleteResourceV1 event = new EventDeleteResourceV1(infraItemId, containerId);

        CompletableFuture<SendResult<String, EventDeleteResourceV1>> future = kafkaTemplate.send(
                TOPIC,
                infraItemId,
                event
        );

        future.whenComplete((result, ex) -> {

            if (ex != null) {
                log.error(
                        "Failed to publish delete event. topic={}, infraItemId={}, error={}",
                        TOPIC,
                        infraItemId,
                        ex.getMessage(),
                        ex
                );
            } else {
                log.info(
                        "Successfully published delete event. topic={}, partition={}, offset={}, infraItemId={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        infraItemId
                );
            }

        });

    }
}