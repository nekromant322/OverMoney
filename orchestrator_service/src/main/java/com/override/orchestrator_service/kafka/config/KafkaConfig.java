package com.override.orchestrator_service.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic responseTopic() {
        return TopicBuilder.name("transaction-response-events-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
