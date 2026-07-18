package io.dev.project_service.kafka.publisher;

import io.dev.dtos.project.CreateInfraItemRequestDtoV1;
import io.dev.events.EventCreateResourceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventCreateResourcePublisherV1 {

    private static final String TOPIC = "event.infra.create";

    private final KafkaTemplate<String, EventCreateResourceV1> kafkaTemplate;

    public void publishCreateResourceEventV1(
            String infraItemId,
            CreateInfraItemRequestDtoV1 request
    ) {

        EventCreateResourceV1 event = new EventCreateResourceV1(
                infraItemId,
                request.infraItemType(),
                request.secrets()
        );

        CompletableFuture<SendResult<String, EventCreateResourceV1>> future = kafkaTemplate.send(
                TOPIC,
                infraItemId,
                event
        );

        future.whenComplete((result,ex)->{
           if(ex!=null){
               log.error(
                       "Failed to publish event. topic={}, infraItemId={}, error={}",
                       TOPIC,
                       infraItemId,
                       ex.getMessage(),
                       ex
               );
           }else {
               log.info(
                       "Successfully published event. topic={}, partition={}, offset={}, infraItemId={}",
                       result.getRecordMetadata().topic(),
                       result.getRecordMetadata().partition(),
                       result.getRecordMetadata().offset(),
                       infraItemId
               );
           }
        });

    }
}