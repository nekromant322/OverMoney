package com.overmoney.telegram_bot_service.kafka.config;

import com.overmoney.telegram_bot_service.constants.KafkaConstants;
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
    public NewTopic paymentRequestsTopic() {
        return TopicBuilder.name(KafkaConstants.PAYMENT_REQUESTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentResponsesTopic() {
        return TopicBuilder.name(KafkaConstants.PAYMENT_RESPONSES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
