package com.overmoney.telegram_bot_service.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic requestTopic() {
        return TopicBuilder.name("transaction-request-events-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
