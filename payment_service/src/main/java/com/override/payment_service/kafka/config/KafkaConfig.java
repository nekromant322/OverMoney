package com.override.payment_service.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
public class KafkaConfig {

    @Bean
    public NewTopic subscriptionRequestTopic() {
        return TopicBuilder.name("subscription-request-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic subscriptionResponseTopic() {
        return TopicBuilder.name("subscription-response-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
