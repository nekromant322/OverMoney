package com.override.payment_service.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaConfigTest {

    @Test
    void testResponseTopicCreation() {
        KafkaConfig kafkaConfig = new KafkaConfig();
        NewTopic topic = kafkaConfig.responseTopic();

        assertThat(topic.name()).isEqualTo("transaction-response-events-topic");
        assertThat(topic.numPartitions()).isEqualTo(3);
        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
    }
}