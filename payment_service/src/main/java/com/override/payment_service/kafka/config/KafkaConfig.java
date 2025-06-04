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
    NewTopic paymentRequestsTopic() {
        return TopicBuilder.name("payment-requests")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic paymentResponsesTopic() {
        return TopicBuilder.name("payment-responses")
                .partitions(3)
                .replicas(1)
                .build();
    }
}