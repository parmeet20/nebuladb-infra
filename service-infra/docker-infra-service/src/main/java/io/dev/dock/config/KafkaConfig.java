package io.dev.dock.config;

import io.dev.events.EventCreateResourceV1;
import io.dev.events.EventDeleteResourceV1;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    /**
     * Topic that docker-infra-service PUBLISHES to (after provisioning).
     */
    @Bean
    public NewTopic createdInfraEventTopic() {
        return TopicBuilder.name("created.infra.event")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Topic that docker-infra-service CONSUMES from (request to provision).
     */
    @Bean
    public NewTopic createInfraEventTopic() {
        return TopicBuilder.name("event.infra.create")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Topic for delete requests.
     */
    @Bean
    public NewTopic deleteInfraEventTopic() {
        return TopicBuilder.name("event.infra.delete")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Consumer factory for create events (EventCreateResourceV1)
     */
    @Bean
    public ConsumerFactory<String, EventCreateResourceV1> createEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "docker-infra-service-create");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "io.dev.events");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EventCreateResourceV1.class.getName());

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(EventCreateResourceV1.class));
    }

    /**
     * Consumer factory for delete events (EventDeleteResourceV1)
     */
    @Bean
    public ConsumerFactory<String, EventDeleteResourceV1> deleteEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "docker-infra-service-delete");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "io.dev.events");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EventDeleteResourceV1.class.getName());

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(EventDeleteResourceV1.class));
    }

    /**
     * Container factory for create event listeners
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventCreateResourceV1> createKafkaListenerContainerFactory(
            ConsumerFactory<String, EventCreateResourceV1> createEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, EventCreateResourceV1> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createEventConsumerFactory);
        factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.RECORD);
        return factory;
    }

    /**
     * Container factory for delete event listeners
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventDeleteResourceV1> deleteKafkaListenerContainerFactory(
            ConsumerFactory<String, EventDeleteResourceV1> deleteEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, EventDeleteResourceV1> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deleteEventConsumerFactory);
        factory.getContainerProperties().setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.RECORD);
        return factory;
    }
}
