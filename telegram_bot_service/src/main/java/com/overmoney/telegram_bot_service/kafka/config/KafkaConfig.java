package com.overmoney.telegram_bot_service.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "service.transaction.processing", havingValue = "kafka")
public class KafkaConfig {

    @Bean
    NewTopic requestTopic() {
        return TopicBuilder.name("transaction-request-events-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic requestSubscriptionTopic() {
        return TopicBuilder.name("subscription-request-events-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic responseSubscriptionTopic() {
        return TopicBuilder.name("subscription-response-events-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
