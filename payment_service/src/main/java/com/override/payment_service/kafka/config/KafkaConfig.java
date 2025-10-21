package com.override.payment_service.kafka.config;

import com.override.payment_service.constants.KafkaConstants;
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
        return TopicBuilder.name(KafkaConstants.PAYMENT_REQUESTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic paymentResponsesTopic() {
        return TopicBuilder.name(KafkaConstants.PAYMENT_RESPONSES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic subscriptionUpdateTopic() {
        return TopicBuilder.name(KafkaConstants.SUBSCRIPTION_UPDATE_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic subscriptionNotificationTopic() {
        return TopicBuilder.name(KafkaConstants.SUBSCRIPTION_NOTIFICATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}